package examen.veterinaria.repository;

import examen.veterinaria.model.Cliente;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioClienteMySQL implements RepositorioCliente {
    private final ConexionMySQL conexion;

    public RepositorioClienteMySQL(ConexionMySQL conexion) { this.conexion = conexion; }

    @Override public List<Cliente> cargarTodos() {
        List<Cliente> lista = new ArrayList<>();
        try (Connection cn = conexion.abrir();
             CallableStatement cs = cn.prepareCall("{CALL sp_listar_clientes()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next())
                lista.add(new Cliente(
                        rs.getString("cedula").trim(),
                        rs.getString("nombre").trim(),
                        rs.getString("telefono").trim(),
                        rs.getInt("edad")
                ));
            return lista;
        } catch (SQLException e) {
            throw ConexionMySQL.traducir(e, "cargar los clientes", "Cliente duplicado.");
        }
    }

    @Override public void insertar(Cliente cliente) {
        try (Connection cn = conexion.abrir();
             CallableStatement cs = cn.prepareCall("{CALL sp_insertar_cliente(?, ?, ?, ?)}")) {
            cs.setString(1, cliente.getCedula());
            cs.setString(2, cliente.getNombre());
            cs.setString(3, cliente.getTelefono());
            cs.setInt(4, cliente.getEdad());
            cs.execute();
        } catch (SQLException e) {
            throw ConexionMySQL.traducir(e, "registrar el cliente", "Ya existe un cliente con esa cédula.");
        }
    }
}

