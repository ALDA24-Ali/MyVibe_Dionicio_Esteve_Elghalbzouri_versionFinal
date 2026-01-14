package model;

import java.time.LocalDateTime;//añadimos esta api que es compatible con jdbc y hibernate.
// se asigna la fecha/hora automaticamente.

//Clase modelo que representa una entrada del diario. Correspone con la tabla entries de la base de datos.
public class EntradaDiario {
//declaración de atributos:
    private int id;
    private LocalDateTime fecha;
    private String cancion;
    private String textoDiario;
    private String rutaFoto;

    //claves foraneas:
    private int moodId;   // NUEVO
    private String nombre; // NUEVO
    private int userId;   // NUEVO


//Constructores:
//Constructor vacío necesario para frameworks ORM como Hibernate:
    public EntradaDiario() {
    }

//Constructor con parámetros para crear una nueva entrada de diario. El ID no se incluye porque es generado automáticamente por la base de datos.
    public EntradaDiario(LocalDateTime fecha, String cancion, String textoDiario,
                         String rutaFoto, int moodId, String nombre, int userId) {
        this.fecha = fecha;
        this.cancion = cancion;
        this.textoDiario = textoDiario;
        this.rutaFoto = rutaFoto;
        this.moodId = moodId;
        this.userId = userId;
        this.nombre = null;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getCancion() { return cancion; }
    public void setCancion(String cancion) { this.cancion = cancion; }

    public String getTextoDiario() { return textoDiario; }
    public void setTextoDiario(String textoDiario) { this.textoDiario = textoDiario; }

    public String getRutaFoto() { return rutaFoto; }
    public void setRutaFoto(String rutaFoto) { this.rutaFoto = rutaFoto; }

    public int getMoodId() { return moodId; }
    public void setMoodId(int moodId) { this.moodId = moodId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
}
