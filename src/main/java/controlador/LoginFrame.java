package controlador;

//necesitaremos importar las siguientes clases:
import dao.UserDAO;
import model.User;
import model.UserSession;

import javax.swing.*;      // Componentes gráficos de Swing (JFrame, JButton, JLabel, etc.)
import java.awt.*;         // Clases para diseño gráfico (Color, Font, Dimension, Cursor...)
import java.security.MessageDigest; // Para aplicar hash seguro a la contraseña
import java.util.Optional; // Para manejar valores que pueden existir o no (usuario encontrado o no)

public class LoginFrame extends JFrame {
// Esta clase representa la ventana de inicio de sesión
    // y hereda de JFrame, por lo que es una ventana Swing.
    
    private JTextField txtEmail;// Campo de texto para el email del usuario
    private JPasswordField txtPassword; // Campo de texto especial para contraseñas (oculta los caracteres)

    public LoginFrame() {
        // Llamamos al constructor de JFrame y establecemos el título de la ventana
        super("Iniciar sesión — MyVibe");
        initUI();// Inicializamos toda la interfaz gráfica
    }

    private void initUI() {

         // Configuración básica de la ventana:
        setSize(500, 400); // Tamaño de la ventana
        setResizable(false);   // No se puede redimensionar
        setLocationRelativeTo(null);   // Se centra en la pantalla
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);// Al cerrar esta ventana, solo se cierra esta pantalla, no toda la aplicación.


        // Creamos el fondo degradado reutilizando el mismo estilo que en otras pantallas:
        GradientPanel background = new GradientPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));// Usamos BoxLayout en eje Y para colocar los elementos en vertical
        setContentPane(background);// Establecemos este panel como el contenido principal de la ventana

        // Título
        JLabel titulo = new JLabel("Iniciar sesión"); // Texto del título
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28)); // Fuente grande y en negrita
        titulo.setForeground(Color.WHITE);            // Color blanco
        titulo.setAlignmentX(CENTER_ALIGNMENT);       // Centrado horizontal

        // Campos
        txtEmail = new JTextField(20);       // Campo para el email
        txtPassword = new JPasswordField(20);// Campo para la contraseña

        // Definimos un tamaño fijo para los campos:
        Dimension fieldSize = new Dimension(300, 40);
        txtEmail.setMaximumSize(fieldSize);
        txtPassword.setMaximumSize(fieldSize);

        // Centramos los campos horizontalmente
        txtEmail.setAlignmentX(CENTER_ALIGNMENT);
        txtPassword.setAlignmentX(CENTER_ALIGNMENT);

        // Botón de entrar
        JButton btnLogin = new JButton("Entrar"); // Botón para iniciar sesión
        btnLogin.setAlignmentX(CENTER_ALIGNMENT); // Centrado horizontal
        btnLogin.setPreferredSize(new Dimension(160, 45));
        btnLogin.setMaximumSize(new Dimension(160, 45));
        btnLogin.setBackground(new Color(123, 97, 255)); // Color personalizado
        btnLogin.setForeground(Color.WHITE);             // Texto blanco
        btnLogin.setFocusPainted(false);                 // Quita el borde de foco
        btnLogin.setBorderPainted(false);                // Quita el borde por defecto

        btnLogin.addActionListener(e -> login());// Cuando se pulsa el botón, se ejecuta el método login()

        // Añadir componentes al panel:
        background.add(Box.createVerticalStrut(40)); // Espacio superior
        background.add(titulo);                       // Título
        background.add(Box.createVerticalStrut(30)); // Espacio
        background.add(txtEmail);                    // Campo email
        background.add(Box.createVerticalStrut(15)); // Espacio
        background.add(txtPassword);                 // Campo contraseña
        background.add(Box.createVerticalStrut(30)); // Espacio
        background.add(btnLogin);                    // Botón entrar
        background.add(Box.createVerticalGlue());    // Empuja el contenido hacia arriba
    }

    private void login() {
        // Obtenemos el email escrito por el usuario
        // trim() elimina espacios y toLowerCase() evita problemas con mayúsculas
        String email = txtEmail.getText().trim().toLowerCase();

        // Obtenemos la contraseña del campo de contraseña
        // getPassword() devuelve un char[], por eso se convierte a String
        String pass = String.valueOf(txtPassword.getPassword()).trim();

        // Comprobamos que ningún campo esté vacío
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena email y contraseña");
            return; // Se corta el método si falta algún dato!
        }

        // Convertimos la contraseña a un hash SHA-256
        String hash = hashPassword(pass);

        // Creamos el DAO de usuario para consultar la base de datos
        UserDAO dao = new UserDAO();

        // Intentamos hacer login con email y hash
        // Puede devolver un usuario o nada (Optional)
        Optional<User> userOpt = dao.login(email, hash);

        // Si el usuario existe y las credenciales son correctas
        if (userOpt.isPresent()) {

            // Obtenemos el usuario desde el Optional
            User u = userOpt.get();

            // Guardamos los datos del usuario en la sesión
            UserSession.userId = u.getId();
            UserSession.nombre = u.getNombre();

            // Mostramos mensaje de bienvenida
            JOptionPane.showMessageDialog(this, "Bienvenid@, " + u.getNombre());

            // Cerramos la ventana de login
            dispose();

            // Abrimos la pantalla principal del usuario
            new UserHomeFrame(UserSession.nombre).setVisible(true);

        } else {
            // Si no existe el usuario o la contraseña es incorrecta
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
        }
    }

    private String hashPassword(String pass) {
        try {
            // Creamos el algoritmo de hash SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Aplicamos el hash a la contraseña
            byte[] hash = md.digest(pass.getBytes());

            // Convertimos el array de bytes a texto hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            // Devolvemos el hash final
            return sb.toString();

        } catch (Exception e) {
            // Si ocurre un error grave, lanzamos una excepción
            throw new RuntimeException(e);
        }
    }
}