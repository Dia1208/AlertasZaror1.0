package cl.zaror.alertas.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.zaror.alertas.backend.auth.AuthExcepciones.CredencialesInvalidasException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.CuentaBloqueadaException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.CuentaNoAprobadaException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.TelefonoYaRegistradoException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.ValidacionException;
import cl.zaror.alertas.backend.seguridad.TokenService;

class AuthServiceTest {

    private static final Instant AHORA = Instant.parse("2026-11-06T12:00:00Z");
    private static final String TELEFONO = "912345678";
    private static final String CLAVE = "clave1234";

    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final String hashDeLaClave = encoder.encode(CLAVE);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final TokenService tokens = mock(TokenService.class);
    private final AuthService servicio =
            new AuthService(usuarios, encoder, tokens, Clock.fixed(AHORA, ZoneOffset.UTC));

    private Usuario usuario(String estado, int intentos, Instant bloqueadoHasta) {
        return new Usuario(1L, "Vecino Uno", TELEFONO, "Calle Ejemplo 101", hashDeLaClave,
                Usuario.ROL_VECINO, estado, intentos, bloqueadoHasta);
    }

    private void existeUsuario(Usuario usuario) {
        when(usuarios.buscarPorTelefono(TELEFONO)).thenReturn(Optional.of(usuario));
    }

    // ---------- registro ----------

    @Test
    void registrarGuardaElHashYQuedaPendiente() {
        when(usuarios.crear(any(), any(), any(), any())).thenReturn(7L);
        when(usuarios.buscarPorId(7L)).thenReturn(Optional.of(usuario(Usuario.ESTADO_PENDIENTE, 0, null)));

        var respuesta = servicio.registrar(
                new RegistroRequest(" Vecino Uno ", TELEFONO, "Calle Ejemplo 101", CLAVE));

        ArgumentCaptor<String> hash = ArgumentCaptor.forClass(String.class);
        verify(usuarios).crear(eq("Vecino Uno"), eq(TELEFONO), eq("Calle Ejemplo 101"), hash.capture());
        assertThat(hash.getValue()).isNotEqualTo(CLAVE);
        assertThat(encoder.matches(CLAVE, hash.getValue())).isTrue();
        assertThat(respuesta.estado()).isEqualTo("PENDIENTE");
        assertThat(respuesta.rol()).isEqualTo("VECINO");
    }

    @Test
    void registrarConTelefonoExistenteDebeFallar() {
        existeUsuario(usuario(Usuario.ESTADO_APROBADO, 0, null));

        assertThatThrownBy(() -> servicio.registrar(
                new RegistroRequest("Otro Vecino", TELEFONO, "Calle Ejemplo 102", CLAVE)))
                .isInstanceOf(TelefonoYaRegistradoException.class);
        verify(usuarios, never()).crear(any(), any(), any(), any());
    }

    @Test
    void registrarConDatosInvalidosDebeFallarSinGuardar() {
        assertThatThrownBy(() -> servicio.registrar(new RegistroRequest("", "1", "", "x")))
                .isInstanceOf(ValidacionException.class);
        verify(usuarios, never()).crear(any(), any(), any(), any());
    }

    // ---------- login ----------

    @Test
    void loginCorrectoDevuelveElToken() {
        Usuario aprobado = usuario(Usuario.ESTADO_APROBADO, 0, null);
        existeUsuario(aprobado);
        when(tokens.generar(aprobado)).thenReturn(new TokenResponse("el-token", "Bearer", 7200));

        var respuesta = servicio.login(new LoginRequest(TELEFONO, CLAVE));

        assertThat(respuesta.token()).isEqualTo("el-token");
    }

    @Test
    void loginConClaveIncorrectaSumaUnIntento() {
        existeUsuario(usuario(Usuario.ESTADO_APROBADO, 0, null));

        assertThatThrownBy(() -> servicio.login(new LoginRequest(TELEFONO, "incorrecta1")))
                .isInstanceOf(CredencialesInvalidasException.class);
        verify(usuarios).actualizarIntentos(eq(1L), eq(1), isNull());
    }

    @Test
    void elQuintoFalloBloqueaLaCuenta() {
        existeUsuario(usuario(Usuario.ESTADO_APROBADO, 4, null));

        assertThatThrownBy(() -> servicio.login(new LoginRequest(TELEFONO, "incorrecta1")))
                .isInstanceOf(CuentaBloqueadaException.class);
        verify(usuarios).actualizarIntentos(1L, 0, AHORA.plus(PoliticaBloqueo.DURACION));
    }

    @Test
    void cuentaBloqueadaRechazaInclusoLaClaveCorrecta() {
        existeUsuario(usuario(Usuario.ESTADO_APROBADO, 0, AHORA.plusSeconds(60)));

        assertThatThrownBy(() -> servicio.login(new LoginRequest(TELEFONO, CLAVE)))
                .isInstanceOfSatisfying(CuentaBloqueadaException.class,
                        e -> assertThat(e.segundos()).isEqualTo(60));
        verify(tokens, never()).generar(any());
    }

    @Test
    void cuentaPendienteNoPuedeEntrarAunqueLaClaveSeaCorrecta() {
        existeUsuario(usuario(Usuario.ESTADO_PENDIENTE, 0, null));

        assertThatThrownBy(() -> servicio.login(new LoginRequest(TELEFONO, CLAVE)))
                .isInstanceOf(CuentaNoAprobadaException.class);
        verify(tokens, never()).generar(any());
    }

    @Test
    void cuentaPendienteConClaveIncorrectaNoRevelaSuEstado() {
        existeUsuario(usuario(Usuario.ESTADO_PENDIENTE, 0, null));

        assertThatThrownBy(() -> servicio.login(new LoginRequest(TELEFONO, "incorrecta1")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void telefonoInexistenteDaElMismoErrorQueClaveIncorrecta() {
        assertThatThrownBy(() -> servicio.login(new LoginRequest("900000000", CLAVE)))
                .isInstanceOf(CredencialesInvalidasException.class);
        verify(usuarios, never()).actualizarIntentos(anyLong(), anyInt(), any());
    }

    @Test
    void loginExitosoReiniciaLosIntentos() {
        Usuario conIntentos = usuario(Usuario.ESTADO_APROBADO, 3, null);
        existeUsuario(conIntentos);
        when(tokens.generar(conIntentos)).thenReturn(new TokenResponse("t", "Bearer", 7200));

        servicio.login(new LoginRequest(TELEFONO, CLAVE));

        verify(usuarios).actualizarIntentos(1L, 0, null);
    }

    @Test
    void claveMasLargaQueElLimiteSeRechazaSinTocarLaBase() {
        assertThatThrownBy(() -> servicio.login(new LoginRequest(TELEFONO, "a1" + "x".repeat(100))))
                .isInstanceOf(CredencialesInvalidasException.class);
        verify(usuarios, never()).buscarPorTelefono(any());
    }
}
