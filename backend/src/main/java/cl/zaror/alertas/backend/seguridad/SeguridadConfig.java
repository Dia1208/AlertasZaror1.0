package cl.zaror.alertas.backend.seguridad;

import java.nio.charset.StandardCharsets;
import java.time.Clock;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class SeguridadConfig {

    static final int MIN_BYTES_SECRETO = 32;

    /** Falla al arrancar si JWT_SECRET falta o es demasiado corta. */
    public static SecretKey claveDesde(String secreto) {
        byte[] bytes = secreto == null ? new byte[0] : secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < MIN_BYTES_SECRETO) {
            throw new IllegalStateException(
                    "Falta la variable de entorno JWT_SECRET o tiene menos de 32 caracteres");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(@Value("${jwt.secret:}") String secreto) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(claveDesde(secreto)));
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${jwt.secret:}") String secreto) {
        NimbusJwtDecoder decodificador = NimbusJwtDecoder.withSecretKey(claveDesde(secreto))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decodificador.setJwtValidator(JwtValidators.createDefaultWithIssuer(TokenService.EMISOR));
        return decodificador;
    }

    /** Delegating: guarda el algoritmo dentro del hash, así se puede cambiar más adelante. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    Clock reloj() {
        return Clock.systemUTC();
    }

    @Bean
    SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/error", "/api/salud", "/api/auth/registro", "/api/auth/login")
                        .permitAll()
                        .requestMatchers("/api/salud/bd").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(convertidor())));
        return http.build();
    }

    /** El claim "rol" del token pasa a ser la autoridad ROLE_VECINO o ROLE_ADMINISTRADOR. */
    private JwtAuthenticationConverter convertidor() {
        JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
        autoridades.setAuthoritiesClaimName("rol");
        autoridades.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(autoridades);
        return convertidor;
    }
}
