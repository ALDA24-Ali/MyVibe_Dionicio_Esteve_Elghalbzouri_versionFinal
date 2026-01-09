package model;
//Clase modelo que representa la entidad Usuario de la aplicación.Corresponde con la tabla 'users' de la base de datos.. No contiene lógica de persistencia ni de interfaz.
public class User {
 // Atributos del usuario:
    private int id;// Identificador único del usuario (clave primaria en BD)
    private String nombre;// Nombre del usuario
    private String apellido; // Apellido del usuario
    private String email;// Correo electrónico del usuario (único)
    private String passwordHash;// Hash de la contraseña (no se almacena la contraseña en texto plano)


//Constructores:

//Constructor vacío necesario para frameworks de persistenci como Hibernate, que instancian objetos mediante reflexión:
public User() {
    }

//Constructor con parámetros para crear un nuevo usuario. El ID no se incluye porque es generado automáticamente por la base de datos.
    public User(String nombre, String apellido, String email, String passwordHash) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // GETTERS & SETTERS

    public int getId() { return id; }//NOTA: El ID se asigna después de persistir el usuario en la base de datos.
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }

    public String getApellido() { return apellido; }

    public String getEmail() { return email; }

    public String getPasswordHash() { return passwordHash; }
}
