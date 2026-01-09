package dao;

import model.Mood;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoodDAO {

    public List<Mood> getAllMoods() {
        List<Mood> moods = new ArrayList<>();

        String sql = "SELECT mood_id, nombre FROM moods ORDER BY mood_id";

        // USAMOS la MISMA conexión que todo el proyecto
        try (Connection conn = new JDBCTransactionDAO().getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                moods.add(new Mood(
                        rs.getInt("mood_id"),
                        rs.getString("nombre")
                ));
            }

        } catch (Exception ex) {
    System.err.println("ERROR cargando moods desde BD");
    ex.printStackTrace(); // ← ESTO ES CLAVE
}


        return moods;
    }
}
