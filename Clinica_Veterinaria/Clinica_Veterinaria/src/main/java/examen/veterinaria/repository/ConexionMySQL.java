package examen.veterinaria.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {
    private final String url;
    private final String usuario;
    private final String password;

    public ConexionMySQL() {
        this.url = leer("VET_DB_URL");
        this.usuario = leer("VET_DB_USER");
        this.password = leer("VET_DB_PASSWORD");
        if (usuario.equalsIgnoreCase("root"))
            throw new PersistenciaException("Por seguridad la aplicación no se conecta como root, use vet_app.", null);
    }

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    private static String leer(String variable) {
        String valor = System.getenv(variable);
        if (valor == null || valor.isBlank())
            throw new PersistenciaException("Falta la variable de entorno " + variable
                    + ". Configúrela en la ejecución de IntelliJ (Run > Edit Configurations > Environment variables).", null);
        return valor.trim();
    }

    static RuntimeException traducir(SQLException e, String operacion, String mensajeDuplicado) {
        if ("45000".equals(e.getSQLState())) return new IllegalArgumentException(e.getMessage());
        return switch (e.getErrorCode()) {
            case 1062 -> new IllegalArgumentException(mensajeDuplicado);
            case 1452 -> new IllegalArgumentException("Registre primero al cliente dueño de la mascota.");
            case 1451 -> new IllegalArgumentException("No se puede eliminar: tiene registros relacionados.");
            case 3819 -> new IllegalArgumentException("Los datos no cumplen las reglas de la base de datos.");
            case 1142, 1370 -> new PersistenciaException("Permiso denegado en MySQL al " + operacion + ".", e);
            case 1045 -> new PersistenciaException("Usuario o contraseña de MySQL incorrectos.", e);
            default -> new PersistenciaException("No se pudo " + operacion + " en MySQL: " + e.getMessage(), e);
        };
    }
}
