package cl.zaror.alertas.backend.seguridad;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import cl.zaror.alertas.backend.auth.TokenResponse;
import cl.zaror.alertas.backend.auth.Usuario;

@Service
public class TokenService {

    public static final String EMISOR = "alertas-zaror";

    private final JwtEncoder encoder;
    private final Clock reloj;
    private final Duration duracion;

    public TokenService(
            JwtEncoder encoder,
            Clock reloj,
            @Value("${jwt.duracion-minutos:120}") long duracionMinutos) {
        this.encoder = encoder;
        this.reloj = reloj;
        this.duracion = Duration.ofMinutes(duracionMinutos);
    }

    /** El token lleva el id del usuario (subject) y su rol. No lleva datos personales. */
    public TokenResponse generar(Usuario usuario) {
        Instant ahora = reloj.instant();
        JwtClaimsSet datos = JwtClaimsSet.builder()
                .issuer(EMISOR)
                .issuedAt(ahora)
                .expiresAt(ahora.plus(duracion))
                .subject(String.valueOf(usuario.id()))
                .claim("rol", usuario.rol())
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(cabecera, datos)).getTokenValue();
        return new TokenResponse(token, "Bearer", duracion.toSeconds());
    }
}
