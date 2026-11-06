package cl.zaror.alertas.backend.integracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Prueba el flujo completo contra el servidor real y la base Oracle.
 * Requiere JWT_SECRET y las variables de la base. Borra el usuario de prueba al terminar.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfSystemProperty(named = "oracle.integration", matches = "true")
class AuthIntegrationTest {

    private static final String TELEFONO = "900000001";
    private static final String CLAVE = "clave1234";
    private static final Pattern TOKEN = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");

    @Value("${local.server.port}")
    private int puerto;

    @Autowired
    private JdbcTemplate jdbc;

    private final HttpClient http = HttpClient.newHttpClient();

    @BeforeEach
    @AfterEach
    void borrarUsuarioDePrueba() {
        jdbc.update("DELETE FROM USUARIOS WHERE TELEFONO = ?", TELEFONO);
    }

    private HttpResponse<String> publicar(String ruta, String json) throws Exception {
        HttpRequest solicitud = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return http.send(solicitud, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> consultar(String ruta, String token) throws Exception {
        HttpRequest.Builder constructor = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta));
        if (token != null) {
            constructor.header("Authorization", "Bearer " + token);
        }
        return http.send(constructor.GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> registrar() throws Exception {
        return publicar("/api/auth/registro", String.format(
                "{\"nombre\":\"Vecino Prueba\",\"telefono\":\"%s\",\"direccion\":\"Calle Ejemplo 101\",\"clave\":\"%s\"}",
                TELEFONO, CLAVE));
    }

    private HttpResponse<String> login(String clave) throws Exception {
        return publicar("/api/auth/login",
                String.format("{\"telefono\":\"%s\",\"clave\":\"%s\"}", TELEFONO, clave));
    }

    @Test
    void flujoCompletoDeRegistroAprobacionYLogin() throws Exception {
        HttpResponse<String> registro = registrar();
        assertThat(registro.statusCode()).isEqualTo(201);
        assertThat(registro.body()).contains("PENDIENTE").doesNotContain(CLAVE).doesNotContain("hash");

        assertThat(registrar().statusCode()).isEqualTo(409);
        assertThat(login("incorrecta1").statusCode()).isEqualTo(401);
        assertThat(login(CLAVE).statusCode()).isEqualTo(403);

        jdbc.update("UPDATE USUARIOS SET ESTADO = 'APROBADO', "
                + "CUADRA_ID = (SELECT MIN(ID) FROM CUADRAS), FECHA_APROBACION = SYSTIMESTAMP "
                + "WHERE TELEFONO = ?", TELEFONO);

        HttpResponse<String> entrada = login(CLAVE);
        assertThat(entrada.statusCode()).isEqualTo(200);
        Matcher encontrado = TOKEN.matcher(entrada.body());
        assertThat(encontrado.find()).isTrue();
        String token = encontrado.group(1);

        HttpResponse<String> perfil = consultar("/api/auth/yo", token);
        assertThat(perfil.statusCode()).isEqualTo(200);
        assertThat(perfil.body()).contains(TELEFONO);

        assertThat(consultar("/api/auth/yo", null).statusCode()).isEqualTo(401);
        assertThat(consultar("/api/auth/yo", token + "x").statusCode()).isEqualTo(401);
        assertThat(consultar("/api/salud/bd", token).statusCode()).isEqualTo(403);
        assertThat(consultar("/api/salud", null).statusCode()).isEqualTo(200);
    }

    @Test
    void cincoIntentosFallidosBloqueanLaCuenta() throws Exception {
        assertThat(registrar().statusCode()).isEqualTo(201);

        for (int intento = 1; intento <= 4; intento++) {
            assertThat(login("incorrecta1").statusCode()).isEqualTo(401);
        }
        assertThat(login("incorrecta1").statusCode()).isEqualTo(429);
        assertThat(login(CLAVE).statusCode()).isEqualTo(429);
    }
}
