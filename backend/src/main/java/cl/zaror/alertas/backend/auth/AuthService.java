package cl.zaror.alertas.backend.auth;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.zaror.alertas.backend.auth.AuthExcepciones.CredencialesInvalidasException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.CuentaBloqueadaException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.CuentaNoAprobadaException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.TelefonoYaRegistradoException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.ValidacionException;
import cl.zaror.alertas.backend.seguridad.TokenService;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    private final Clock reloj;

    /** Hash de relleno: se compara cuando el teléfono no existe, para que tarde lo mismo. */
    private final String hashDeRelleno;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, TokenService tokens, Clock reloj) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.tokens = tokens;
        this.reloj = reloj;
        this.hashDeRelleno = encoder.encode("relleno-sin-uso");
    }

    /** Registra un vecino nuevo. Queda PENDIENTE hasta que un administrador lo apruebe. */
    public UsuarioResponse registrar(RegistroRequest solicitud) {
        Map<String, String> errores = ValidacionesAuth.validarRegistro(solicitud);
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }

        String telefono = solicitud.telefono().trim();
        if (usuarios.buscarPorTelefono(telefono).isPresent()) {
            throw new TelefonoYaRegistradoException();
        }

        String hash = encoder.encode(solicitud.clave());
        try {
            long id = usuarios.crear(
                    solicitud.nombre().trim(), telefono, solicitud.direccion().trim(), hash);
            Usuario creado = usuarios.buscarPorId(id).orElseThrow();
            return UsuarioResponse.desde(creado);
        } catch (DuplicateKeyException e) {
            // Otro registro con el mismo teléfono llegó al mismo tiempo.
            throw new TelefonoYaRegistradoException();
        }
    }

    public TokenResponse login(LoginRequest solicitud) {
        String telefono = solicitud.telefono() == null ? "" : solicitud.telefono().trim();
        String clave = solicitud.clave() == null ? "" : solicitud.clave();

        if (clave.getBytes(StandardCharsets.UTF_8).length > ValidacionesAuth.MAX_BYTES_CLAVE) {
            throw new CredencialesInvalidasException();
        }

        Optional<Usuario> encontrado = usuarios.buscarPorTelefono(telefono);
        if (encontrado.isEmpty()) {
            encoder.matches(clave, hashDeRelleno);
            throw new CredencialesInvalidasException();
        }

        Usuario usuario = encontrado.get();
        Instant ahora = reloj.instant();

        if (PoliticaBloqueo.estaBloqueado(usuario.bloqueadoHasta(), ahora)) {
            throw new CuentaBloqueadaException(
                    PoliticaBloqueo.segundosRestantes(usuario.bloqueadoHasta(), ahora));
        }

        if (!encoder.matches(clave, usuario.claveHash())) {
            PoliticaBloqueo.Estado estado = PoliticaBloqueo.tras(usuario.intentosFallidos(), ahora);
            usuarios.actualizarIntentos(usuario.id(), estado.intentosFallidos(), estado.bloqueadoHasta());
            if (estado.bloqueadoHasta() != null) {
                throw new CuentaBloqueadaException(
                        PoliticaBloqueo.segundosRestantes(estado.bloqueadoHasta(), ahora));
            }
            throw new CredencialesInvalidasException();
        }

        if (usuario.intentosFallidos() != 0 || usuario.bloqueadoHasta() != null) {
            usuarios.actualizarIntentos(usuario.id(), 0, null);
        }

        // Solo se informa el estado de la cuenta cuando la clave ya fue correcta.
        if (!Usuario.ESTADO_APROBADO.equals(usuario.estado())) {
            throw new CuentaNoAprobadaException(usuario.estado());
        }

        return tokens.generar(usuario);
    }

    public UsuarioResponse perfil(long usuarioId) {
        return usuarios.buscarPorId(usuarioId)
                .map(UsuarioResponse::desde)
                .orElseThrow(CredencialesInvalidasException::new);
    }
}
