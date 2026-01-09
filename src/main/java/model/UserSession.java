package model;

//Clase que gestiona el estado de la sesión del usuario en la aplicación. No es persistente, 
//y solo existe en memoria DURANTE la EJECUCION. guarda quien esta usando la app inmediatamente.
public class UserSession {

//static se usa porque en la aplicación solo puede haber una sesión activa, y esa información debe ser accesible desde cualquier parte del programa sin crear objetos.    
    public static int userId;      // ID del usuario logueado actual.
    public static String nombre;   // Nombre (dato auxiliar para mostrar en UI-interfazdeusuario)
    
    //aqui indicamos si hay un usuario con sesion iniciada:
    public static boolean isLogged() {
        return userId > 0;  //si el ID es mayor que 0, hay una sesión activa.
    }

    //aqui se cierra la sesion actual y limpia los datos en memoria.
    public static void logout() {
        userId = 0; //se restablece el ID para indicar que no hay sesión activa
        nombre = null;// se limpia el nombre almacenado en memoria
    }
}
