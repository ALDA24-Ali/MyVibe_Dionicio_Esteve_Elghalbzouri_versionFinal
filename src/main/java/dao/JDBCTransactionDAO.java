package dao;

//imports conectados a las clases de entradadiario y usersession:
import model.EntradaDiario;
import model.UserSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//En esta clase se implementan TODO los metodos que decia IDiarioDAO: Es la encargada de hablar con la base de datos mysql.
//guarda, modifica, borra, y lee entradas del diario.
//siempre lo hace para el usuario que está logueado.


// Esta clase es la versión JDBC del DAO.
// Aquí es donde REALMENTE se habla con MySQL.
public class JDBCTransactionDAO implements IDiarioDAO {

    //Ubicamos primero donde esta la abse de datos:
    private static final String URL = "jdbc:mysql://localhost:3306/myvibe?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    //La URL JDBC especifica el tipo de base de datos, su ubicación, el puerto y el nombre de la base de datos, así como parámetros adicionales necesarios para asegurar una conexión correcta con MySQL desde Java.
   
    // En el entorno de desarrollo local, la conexión JDBC se realiza como 'root@localhost',
    // que no requiere contraseña. Por ello, el campo de contraseña se deja vacío.
    private static final String USER = "root";//se inicia sesion con el usuario ROOT.
    private static final String PASS = "myvibe"; //y con la contrseña.

    //Cada vez que se necesite hablar con la base de datos, se abre una conexión JDBC.
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);//
    }

    // INSERTAR ENTRADA del diario en la base de datos:
    @Override
    public boolean insert(EntradaDiario e) {
    // Sentencia SQL para insertar una nueva fila en la tabla 'entries'
    // Los signos ? son marcadores que se rellenan después con datos reales
        String sql = "INSERT INTO entries (fecha, cancion, texto_diario, ruta_foto, mood_id, user_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";


    // try-with-resources:
    // abre la conexión, prepara la consulta y lo cierra todo automáticamente al final
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        st.setTimestamp(1, Timestamp.valueOf(e.getFecha())); // fecha de la entrada
        st.setString(2, e.getCancion());                     // canción asociada
        st.setString(3, e.getTextoDiario());                 // texto del diario
        st.setString(4, e.getRutaFoto());                    // ruta de la imagen
        st.setInt(5, e.getMoodId());                         // estado de ánimo

        // ID del usuario logueado actualmente.
        // Se obtiene desde UserSession, no desde el objeto EntradaDiario.
        st.setInt(6, UserSession.userId);

         // Ejecuta el INSERT y devuelve cuántas filas se han afectado
        int rows = st.executeUpdate();

        // Si se ha insertado al menos una fila
        if (rows > 0) {

            // Se recupera el ID generado automáticamente por la base de datos
            ResultSet rs = st.getGeneratedKeys();

            // Si hay un ID generado, se guarda dentro del objeto EntradaDiario
            if (rs.next()) {
                e.setId(rs.getInt(1));
            }
            // Inserción correcta
            return true;
        }

    } catch (Exception ex) {
        ex.printStackTrace();
    }

    // Si algo falla, se devuelve false
    return false;
}

    // BORRAR ENTRADA (solo del usuario)
    @Override
    public boolean delete(int id) {//recibe el id de la entrada a borrar.
    // Sentencia SQL para borrar una entrada concreta
    // Solo se borra si pertenece al usuario logueado
        String sql = "DELETE FROM entries WHERE id=? AND user_id=?";

        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);  // Indicamos qué entrada se quiere borrar
            st.setInt(2, UserSession.userId);// Indicamos que debe ser del usuario logueado

        // Ejecuta el DELETE
        // Devuelve true si se ha borrado alguna fila
            return st.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // OBTENER UNA ENTRADA CONCRETA POR SU ID
    @Override
    public EntradaDiario getById(int id) {
        String sql = "SELECT * FROM entries WHERE id=? AND user_id=?";

        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            // Indicamos el ID de la entrada que buscamos
        st.setInt(1, id);

        // Indicamos que debe ser del usuario logueado
        st.setInt(2, UserSession.userId);
            ResultSet rs = st.executeQuery();// Ejecuta la consulta SELECT

            if (rs.next()) {// Si existe una entrada con ese ID... significa que existe una fila.
                EntradaDiario e = new EntradaDiario(
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getString("cancion"),
                        rs.getString("texto_diario"),
                        rs.getString("ruta_foto"),
                        rs.getInt("mood_id"),
                        rs.getInt("user_id")
                );
                            // Asignamos el ID de la entrada
            e.setId(rs.getInt("id"));

            // Devolvemos la entrada encontrada
            return e;

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;// Si no se encuentra la entrada, devolvemos null
    }


    // OBTENER TODAS LAS ENTRADAS DEL USUARIO LOGUEADO

    @Override
    public List<EntradaDiario> getAll() {
        List<EntradaDiario> lista = new ArrayList<>();// Creamos una lista vacía donde guardar las entradas

        String sql = "SELECT * FROM entries WHERE user_id=? ORDER BY fecha DESC";// Consulta para obtener todas las entradas del usuario
    // ordenadas por fecha (la más reciente primero)

        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, UserSession.userId);// Indicamos el usuario logueado
            ResultSet rs = st.executeQuery();// Ejecutamos la consulta

            while (rs.next()) {// Recorremos todas las filas devueltas
             // Creamos un objeto EntradaDiario por cada fila
                EntradaDiario e = new EntradaDiario(
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getString("cancion"),
                        rs.getString("texto_diario"),
                        rs.getString("ruta_foto"),
                        rs.getInt("mood_id"),
                        rs.getInt("user_id")
                );

                e.setId(rs.getInt("id")); // Asignamos el ID
                lista.add(e);// Añadimos la entrada a la lista
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;// Devolvemos la lista con todas las entradas
    }

    @Override
public List<EntradaDiario> getByUserId(int userId) {
    List<EntradaDiario> entradas = new ArrayList<>();

    String sql = """
        SELECT id, fecha, cancion, texto_diario, ruta_foto, mood_id, user_id
        FROM entries
        WHERE user_id = ?
        ORDER BY fecha DESC
    """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            EntradaDiario e = new EntradaDiario(
                    rs.getTimestamp("fecha").toLocalDateTime(),
                    rs.getString("cancion"),
                    rs.getString("texto_diario"),
                    rs.getString("ruta_foto"),
                    rs.getInt("mood_id"),
                    rs.getInt("user_id")
            );

            e.setId(rs.getInt("id"));
            entradas.add(e);
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return entradas;
}

    @Override
    public boolean update(EntradaDiario e) {
        String sql = """
            UPDATE entries
            SET cancion = ?, texto_diario = ?, ruta_foto = ?, mood_id = ?
            WHERE id = ? AND user_id = ?
        """;

        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getCancion());
            ps.setString(2, e.getTextoDiario());
            ps.setString(3, e.getRutaFoto());
            ps.setInt(4, e.getMoodId());
            ps.setInt(5, e.getId());
            ps.setInt(6, e.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
