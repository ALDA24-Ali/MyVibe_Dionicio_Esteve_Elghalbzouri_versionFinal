package dao;

import model.User;

import java.sql.*;
import java.util.Optional;

public class UserDAO {

    private static final String URL =
            "jdbc:mysql://localhost:3306/myvibe?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "myvibe";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public boolean register(User user) {

        String sql = "INSERT INTO users (nombre, apellido, email, password_hash) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, user.getNombre());
            st.setString(2, user.getApellido());
            st.setString(3, user.getEmail());
            st.setString(4, user.getPasswordHash());

            return st.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public Optional<User> login(String email, String passwordHash) {

    String sql = "SELECT * FROM users WHERE LOWER(email)=? AND password_hash=?";

    try (Connection conn = getConnection();
         PreparedStatement st = conn.prepareStatement(sql)) {

        st.setString(1, email.toLowerCase());
        st.setString(2, passwordHash);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {

            User user = new User(
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("password_hash")
            );

            user.setId(rs.getInt("user_id")); 

            return Optional.of(user);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return Optional.empty();
}
}