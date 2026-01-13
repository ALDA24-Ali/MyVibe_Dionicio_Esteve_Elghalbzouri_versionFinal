package controlador;

// necesitaremos importar las siguientes clases:
import dao.UserDAO;
import model.User;
import model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;

    public LoginFrame() {
        super("Iniciar sesión — MyVibe");
        initUI();
    }

    private void initUI() {

        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GradientPanel background = new GradientPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        setContentPane(background);

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("Iniciar sesión");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        // ===== LABEL EMAIL =====
        JLabel lblEmail = new JLabel("Correo electrónico");
        lblEmail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblEmail.setForeground(Color.WHITE);
        lblEmail.setAlignmentX(CENTER_ALIGNMENT);

        txtEmail = new JTextField(20);

        // ===== LABEL PASSWORD =====
        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setAlignmentX(CENTER_ALIGNMENT);

        txtPassword = new JPasswordField(20);

        // Tamaño de campos
        Dimension fieldSize = new Dimension(300, 40);
        txtEmail.setMaximumSize(fieldSize);
        txtPassword.setMaximumSize(fieldSize);

        txtEmail.setAlignmentX(CENTER_ALIGNMENT);
        txtPassword.setAlignmentX(CENTER_ALIGNMENT);

        // ===== BOTÓN =====
        JButton btnLogin = new JButton("Entrar");
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnLogin.setPreferredSize(new Dimension(160, 45));
        btnLogin.setMaximumSize(new Dimension(160, 45));
        btnLogin.setBackground(new Color(123, 97, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);

        btnLogin.addActionListener(e -> login());

        // ===== AÑADIR COMPONENTES =====
        background.add(Box.createVerticalStrut(35));
        background.add(titulo);

        background.add(Box.createVerticalStrut(30));
        background.add(lblEmail);
        background.add(Box.createVerticalStrut(5));
        background.add(txtEmail);

        background.add(Box.createVerticalStrut(20));
        background.add(lblPassword);
        background.add(Box.createVerticalStrut(5));
        background.add(txtPassword);

        background.add(Box.createVerticalStrut(30));
        background.add(btnLogin);

        background.add(Box.createVerticalGlue());
    }

    private void login() {

        String email = txtEmail.getText().trim().toLowerCase();
        String pass = String.valueOf(txtPassword.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena email y contraseña");
            return;
        }

        String hash = hashPassword(pass);
        UserDAO dao = new UserDAO();
        Optional<User> userOpt = dao.login(email, hash);

        if (userOpt.isPresent()) {

            User u = userOpt.get();
            UserSession.userId = u.getId();
            UserSession.nombre = u.getNombre();

            JOptionPane.showMessageDialog(this, "Bienvenid@, " + u.getNombre());
            dispose();
            new UserHomeFrame(UserSession.nombre).setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
        }
    }

    private String hashPassword(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pass.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
