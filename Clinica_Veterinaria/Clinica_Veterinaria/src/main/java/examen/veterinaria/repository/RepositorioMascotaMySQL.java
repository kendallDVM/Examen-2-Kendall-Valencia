package examen.veterinaria.repository;

import examen.veterinaria.model.Especie;
import examen.veterinaria.model.Mascota;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioMascotaMySQL implements RepositorioMascota {
    private static final String DUPLICADO = "Ya existe ese código de mascota.";
    private final ConexionMySQL conexion;

    public RepositorioMascotaMySQL(ConexionMySQL conexion) { this.conexion = conexion; }

    @Override public List<Mascota> cargarTodos() {
        List<Mascota> lista = new ArrayList<>();
        try (Connection cn = conexion.abrir();
             CallableStatement cs = cn.prepareCall("{CALL sp_listar_mascotas()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) lista.add(aMascota(rs));
            return lista;
        } catch (SQLException e) {
            throw ConexionMySQL.traducir(e, "cargar las mascotas", DUPLICADO);
        }
    }

    @Override public void insertar(Mascota mascota) {
        try (Connection cn = conexion.abrir();
             CallableStatement cs = cn.prepareCall("{CALL sp_insertar_mascota(?, ?, ?, ?, ?)}")) {
            asignar(cs, mascota);
            cs.execute();
        } catch (SQLException e) {
            throw ConexionMySQL.traducir(e, "registrar la mascota", DUPLICADO);
        }
    }

    @Override public void actualizar(Mascota mascota) {
        try (Connection cn = conexion.abrir();
             CallableStatement cs = cn.prepareCall("{CALL sp_actualizar_mascota(?, ?, ?, ?, ?)}")) {
            asignar(cs, mascota);
            cs.execute();
        } catch (SQLException e) {
            throw ConexionMySQL.traducir(e, "actualizar la mascota", DUPLICADO);
        }
    }

    @Override public void eliminar(String codigo) {
        try (Connection cn = conexion.abrir();
             CallableStatement cs = cn.prepareCall("{CALL sp_eliminar_mascota(?)}")) {
            cs.setString(1, codigo.trim());
            cs.execute();
        } catch (SQLException e) {
            throw ConexionMySQL.traducir(e, "eliminar la mascota", DUPLICADO);
        }
    }


    @Override public List<String> listarConDueno() {
        List<String> filas = new ArrayList<>();
        try (Connection cn = conexion.abrir();
             CallableStatement cs = cn.prepareCall("{CALL sp_listar_mascotas_con_dueno()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) filas.add(aMascota(rs) + " | Cliente: " + rs.getString("nombre_cliente").trim());
            return filas;
        } catch (SQLException e) {
            throw ConexionMySQL.traducir(e, "listar las mascotas con dueño", DUPLICADO);
        }
    }

    private static void asignar(CallableStatement cs, Mascota m) throws SQLException {
        cs.setString(1, m.getCodigo());
        cs.setString(2, m.getNombre());
        cs.setString(3, m.getEspecie().name());
        cs.setInt(4, m.getEdad());
        cs.setString(5, m.getCedulaCliente());
    }

    private static Mascota aMascota(ResultSet rs) throws SQLException {
        return new Mascota(
                rs.getString("codigo").trim(),
                rs.getString("nombre").trim(),
                Especie.valueOf(rs.getString("especie").trim()),
                rs.getInt("edad"),
                rs.getString("cedula_cliente").trim()
        );
    }
}

