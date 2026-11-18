package examen.veterinaria.model;

public class Cliente {
    private final String cedula;
    private final String nombre;
    private final String telefono;
    private final int edad;
    public Cliente(String cedula, String nombre, String telefono, int edad) {
        this.cedula = validar(cedula, "Cédula");
        this.nombre = validar(nombre, "Nombre");
        this.telefono = validar(telefono, "Teléfono");
        if (edad < 18 || edad > 60) throw new IllegalArgumentException("La edad del cliente debe estar entre 18 y 60 años.");
        this.edad = edad;
    }
    private static String validar(String valor, String campo) {
        if (valor == null || valor.isBlank() || valor.contains(";") || valor.contains("\n") || valor.contains("\r"))
            throw new IllegalArgumentException(campo + " es obligatorio y no puede contener ; ni saltos de línea.");
        return valor.trim();
    }
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public int getEdad() { return edad; }
    @Override public String toString() { return cedula + " | " + nombre + " | Tel: " + telefono + " | Edad: " + edad + " años"; }
}
