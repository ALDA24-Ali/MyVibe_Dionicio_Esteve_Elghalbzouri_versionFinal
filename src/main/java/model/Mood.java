package model;

public class Mood {
    private final int id;
    private final String nombre;

    public Mood(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        // Lo que se ve en el JComboBox
        return nombre;
    }
}
    