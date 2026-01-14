package dao;

//imports conectados a las clases de entradadiario y usersession:
import model.EntradaDiario;
import model.UserSession;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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

    String sql = """
        SELECT e.id, e.fecha, e.cancion, e.texto_diario, e.ruta_foto, e.mood_id, e.user_id,
               m.nombre AS mood_nombre
        FROM entries e
        JOIN moods m ON e.mood_id = m.mood_id
        WHERE e.id = ? AND e.user_id = ?
    """;

    try (Connection conn = getConnection();
         PreparedStatement st = conn.prepareStatement(sql)) {

        st.setInt(1, id);
        st.setInt(2, UserSession.userId);

        try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {

                LocalDateTime fecha = null;
                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) fecha = ts.toLocalDateTime();

                EntradaDiario e = new EntradaDiario(
                        fecha,
                        rs.getString("cancion"),
                        rs.getString("texto_diario"),
                        rs.getString("ruta_foto"),
                        rs.getInt("mood_id"),
                        rs.getString("mood_nombre"),   // <-- nombre del mood
                        rs.getInt("user_id")
                );

                e.setId(rs.getInt("id"));
                return e;
            }
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return null;
}


    // OBTENER TODAS LAS ENTRADAS DEL USUARIO LOGUEADO

    @Override
public List<EntradaDiario> getAll() {
    List<EntradaDiario> lista = new ArrayList<>();

    String sql = """
        SELECT e.id, e.fecha, e.cancion, e.texto_diario, e.ruta_foto, e.mood_id, e.user_id,
               m.nombre AS mood_nombre
        FROM entries e
        JOIN moods m ON e.mood_id = m.mood_id
        WHERE e.user_id = ?
        ORDER BY e.fecha DESC
    """;

    try (Connection conn = getConnection();
         PreparedStatement st = conn.prepareStatement(sql)) {

        st.setInt(1, UserSession.userId);

        try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {

                LocalDateTime fecha = null;
                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) fecha = ts.toLocalDateTime();

                EntradaDiario e = new EntradaDiario(
                        fecha,
                        rs.getString("cancion"),
                        rs.getString("texto_diario"),
                        rs.getString("ruta_foto"),
                        rs.getInt("mood_id"),
                        rs.getString("mood_nombre"),  // <-- nombre del mood
                        rs.getInt("user_id")
                );

                e.setId(rs.getInt("id"));
                lista.add(e);
            }
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return lista;
}


@Override
public List<EntradaDiario> getByUserId(int userId) {
    List<EntradaDiario> entradas = new ArrayList<>();

    String sql = """
        SELECT e.id, e.fecha, e.cancion, e.texto_diario, e.ruta_foto, e.mood_id, e.user_id,
               m.nombre AS mood_nombre
        FROM entries e
        JOIN moods m ON e.mood_id = m.mood_id
        WHERE e.user_id = ?
        ORDER BY e.fecha DESC
    """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {

                LocalDateTime fecha = null;
                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) fecha = ts.toLocalDateTime();

                EntradaDiario e = new EntradaDiario(
                        fecha,
                        rs.getString("cancion"),
                        rs.getString("texto_diario"),
                        rs.getString("ruta_foto"),
                        rs.getInt("mood_id"),
                        rs.getString("mood_nombre"), // <-- nombre del mood
                        rs.getInt("user_id")
                );

                e.setId(rs.getInt("id"));
                entradas.add(e);
            }
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
            ps.setInt(6, UserSession.userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> getMoodCountsByMonth(int year, int month) {
    String sql = """
        SELECT m.nombre AS mood, COUNT(*) AS total
        FROM entries e
        JOIN moods m ON e.mood_id = m.mood_id
        WHERE e.user_id = ?
          AND YEAR(e.fecha) = ?
          AND MONTH(e.fecha) = ?
        GROUP BY m.nombre
        ORDER BY m.mood_id
    """;

    Map<String, Integer> result = new LinkedHashMap<>();

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, UserSession.userId);
        ps.setInt(2, year);
        ps.setInt(3, month);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String mood = rs.getString("mood");
                int total = rs.getInt("total");
                result.put(mood, total);
            }
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return result;
}


}
