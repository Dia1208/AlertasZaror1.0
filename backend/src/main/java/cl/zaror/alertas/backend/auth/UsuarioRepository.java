package cl.zaror.alertas.backend.auth;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {

    private static final String COLUMNAS =
            "ID, NOMBRE, TELEFONO, DIRECCION, CLAVE_HASH, ROL, ESTADO, INTENTOS_FALLIDOS, BLOQUEADO_HASTA";

    private static final RowMapper<Usuario> MAPEADOR = (rs, fila) -> {
        Timestamp bloqueo = rs.getTimestamp("BLOQUEADO_HASTA");
        return new Usuario(
                rs.getLong("ID"),
                rs.getString("NOMBRE"),
                rs.getString("TELEFONO"),
                rs.getString("DIRECCION"),
                rs.getString("CLAVE_HASH"),
                rs.getString("ROL"),
                rs.getString("ESTADO"),
                rs.getInt("INTENTOS_FALLIDOS"),
                bloqueo == null ? null : bloqueo.toInstant());
    };

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Usuario> buscarPorTelefono(String telefono) {
        List<Usuario> lista = jdbc.query(
                "SELECT " + COLUMNAS + " FROM USUARIOS WHERE TELEFONO = ?", MAPEADOR, telefono);
        return lista.stream().findFirst();
    }

    public Optional<Usuario> buscarPorId(long id) {
        List<Usuario> lista = jdbc.query(
                "SELECT " + COLUMNAS + " FROM USUARIOS WHERE ID = ?", MAPEADOR, id);
        return lista.stream().findFirst();
    }

    /** Crea un vecino PENDIENTE (los valores por defecto los pone la base) y devuelve su id. */
    public long crear(String nombre, String telefono, String direccion, String claveHash) {
        KeyHolder llaves = new GeneratedKeyHolder();
        jdbc.update(conexion -> {
            PreparedStatement ps = conexion.prepareStatement(
                    "INSERT INTO USUARIOS (NOMBRE, TELEFONO, DIRECCION, CLAVE_HASH) VALUES (?, ?, ?, ?)",
                    new String[] {"ID"});
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, direccion);
            ps.setString(4, claveHash);
            return ps;
        }, llaves);
        return llaves.getKey().longValue();
    }

    public void actualizarIntentos(long id, int intentosFallidos, Instant bloqueadoHasta) {
        jdbc.update(
                "UPDATE USUARIOS SET INTENTOS_FALLIDOS = ?, BLOQUEADO_HASTA = ? WHERE ID = ?",
                ps -> {
                    ps.setInt(1, intentosFallidos);
                    if (bloqueadoHasta == null) {
                        ps.setNull(2, Types.TIMESTAMP);
                    } else {
                        ps.setTimestamp(2, Timestamp.from(bloqueadoHasta));
                    }
                    ps.setLong(3, id);
                });
    }
}
