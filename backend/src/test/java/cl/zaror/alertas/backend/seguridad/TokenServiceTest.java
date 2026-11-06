package cl.zaror.alertas.backend.seguridad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import cl.zaror.alertas.backend.auth.Usuario;

class TokenServiceTest {

    private static final String SECRETO = "una-clave-de-prueba-con-mas-de-32-caracteres";
    private static final SecretKey CLAVE = SeguridadConfig.claveDesde(SECRETO);

    private final TokenService servicio = new TokenService(
            new NimbusJwtEncoder(new ImmutableSecret<>(CLAVE)), Clock.systemUTC(), 120);

    private final Usuario usuario = new Usuario(7L, "Vecino Uno", "912345678", "Calle Ejemplo 101",
            "hash", "VECINO", "APROBADO", 0, null);

    private JwtDecoder decodificador(SecretKey clave) {
        NimbusJwtDecoder decodificador = NimbusJwtDecoder.withSecretKey(clave)
                .macAlgorithm(MacAlgorithm.HS256).build();
        decodificador.setJwtValidator(JwtValidators.createDefaultWithIssuer(TokenService.EMISOR));
        return decodificador;
    }

    @Test
    void elTokenLlevaElIdYElRolPeroNoDatosPersonales() {
        var respuesta = servicio.generar(usuario);

        Jwt jwt = decodificador(CLAVE).decode(respuesta.token());

        assertThat(jwt.getSubject()).isEqualTo("7");
        assertThat(jwt.getClaimAsString("rol")).isEqualTo("VECINO");
        assertThat(jwt.getClaims()).doesNotContainKeys("telefono", "nombre", "direccion");
        assertThat(respuesta.tipo()).isEqualTo("Bearer");
        assertThat(respuesta.expiraEnSegundos()).isEqualTo(7200);
    }

    @Test
    void unTokenFirmadoConOtraClaveSeRechaza() {
        var respuesta = servicio.generar(usuario);
        SecretKey otraClave = SeguridadConfig.claveDesde("otra-clave-distinta-de-mas-de-32-caracteres");

        assertThatThrownBy(() -> decodificador(otraClave).decode(respuesta.token()))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void unTokenExpiradoSeRechaza() {
        // Emitido en 2020 con 2 horas de vida: hoy ya está vencido.
        Clock enElPasado = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC);
        TokenService antiguo = new TokenService(
                new NimbusJwtEncoder(new ImmutableSecret<>(CLAVE)), enElPasado, 120);
        var respuesta = antiguo.generar(usuario);

        assertThatThrownBy(() -> decodificador(CLAVE).decode(respuesta.token()))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void unaClaveCortaOVaciaNoSeAcepta() {
        assertThatThrownBy(() -> SeguridadConfig.claveDesde("corta"))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SeguridadConfig.claveDesde(""))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SeguridadConfig.claveDesde(null))
                .isInstanceOf(IllegalStateException.class);
    }
}
