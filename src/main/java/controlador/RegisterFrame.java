package controlador;

import dao.UserDAO;
import model.User;

import javax.swing.*;          // Componentes gráficos de Swing
import java.awt.*;             // Clases de diseño (Color, Font, Dimension, etc.)
import java.security.MessageDigest; // Para aplicar hash seguro a la contraseña

public class RegisterFrame extends JFrame {
 // Esta clase representa la ventana de registro de usuarios
    // y hereda de JFrame, por lo que es una ventana Swing.
 
     // Campos de texto para los datos del usuario
    private JTextField txtNombre, txtApellido, txtEmail;

    // Campo especial para la contraseña (oculta caracteres)
    private JPasswordField txtPassword;

    public RegisterFrame() {
        // Llamamos al constructor de JFrame y definimos el título de la ventana
        super("Crear cuenta — MyVibe");

        // Inicializamos toda la interfaz gráfica
        initUI();
    }

    private void initUI() {

         // Configuración básica de la ventana
        setSize(500, 650);           // Tamaño de la ventana
        setResizable(false);         // No se puede redimensionar
        setLocationRelativeTo(null); // Centrada en pantalla
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Al cerrar esta ventana no se cierra toda la app

         // Panel de fondo con degradado (estilo común de la app)
        GradientPanel background = new GradientPanel();

        // Usamos BoxLayout en eje Y para colocar los elementos en vertical
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));

        // Establecemos este panel como contenido principal
        setContentPane(background);

        //TITULO:
        JLabel titulo = new JLabel("Crear cuenta"); // Texto del título
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28)); // Fuente grande
        titulo.setForeground(Color.WHITE);           // Color blanco
        titulo.setAlignmentX(CENTER_ALIGNMENT);      // Centrado horizontal

        //CAMPOS DE TEXTO:
        // Creamos los campos usando un método auxiliar que añade placeholder
        txtNombre = createField("Nombre");
        txtApellido = createField("Apellido");
        txtEmail = createField("Correo");

         // Campo de contraseña
        txtPassword = new JPasswordField();
        styleField(txtPassword); // Aplicamos estilo común
        addPlaceholderPassword(txtPassword, "Contraseña"); // Placeholder especial

        //BOTON DE REGISTRAR:
        RoundedButton btnRegistrar = new RoundedButton("Registrar", new Color(150, 90, 255));
        btnRegistrar.setAlignmentX(CENTER_ALIGNMENT);

        // Al pulsar el botón se ejecuta el registro
        btnRegistrar.addActionListener(e -> registrarUsuario());

        //Añadiir componentes:
        background.add(Box.createVerticalStrut(40));
        background.add(titulo);
        background.add(Box.createVerticalStrut(30));

        background.add(txtNombre);
        background.add(Box.createVerticalStrut(15));
        background.add(txtApellido);
        background.add(Box.createVerticalStrut(15));
        background.add(txtEmail);
        background.add(Box.createVerticalStrut(15));
        background.add(txtPassword);
        background.add(Box.createVerticalStrut(25));

        background.add(btnRegistrar);
    }


    //METODO PARA CREAR CAMPOS DE TEXTO CON PLACEHOLDER:
    private JTextField createField(String placeholder) {
        JTextField field = new JTextField(20);
        styleField(field);// Aplicamos estilo común

        // Mostramos el placeholder inicialmente
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new java.awt.event.FocusAdapter() {// Listener para detectar cuando el campo gana o pierde el foco
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {// Si el texto es el placeholder, lo borramos
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isBlank()) {// Si el usuario no ha escrito nada, volvemos a poner el placeholder
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }


    // FUNCIÓN DE PLACEHOLDER PARA CONTRASEÑA
    private void addPlaceholderPassword(JPasswordField field, String placeholder) {
        field.setEchoChar((char) 0); // Muestra texto normal
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {// Al empezar a escribir, activamos los puntos
                    field.setText("");
                    field.setEchoChar('•'); // Activa puntos
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getPassword().length == 0) {// Si no se ha escrito nada, volvemos al placeholder
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
    // ESTILO COMUN PARA CAMPOS DE TEXTO:

    private void styleField(JComponent comp) {
        comp.setMaximumSize(new Dimension(300, 40));// Tamaño fijo
        comp.setFont(new Font("SansSerif", Font.PLAIN, 16));
        comp.setForeground(Color.DARK_GRAY);
        comp.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }


    //REGISTRO DE USUARIO:
    private void registrarUsuario() {
        // Obtenemos los datos introducidosS
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText().trim().toLowerCase();
        String pass = new String(txtPassword.getPassword());

        // Evitar placeholders enviados como datos reales
        if (nombre.equals("Nombre") || email.equals("Correo") || pass.equals("Contraseña")) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.");
            return;
        }
        // Comprobamos que los campos obligatorios no estén vacíos
        if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos obligatorios");
            return;
        }
        // Convertimos la contraseña en un hash seguro
        String hash = hashPassword(pass);

        User u = new User(nombre, apellido, email, hash);// Creamos el usuario
        UserDAO dao = new UserDAO();// DAO para registrar el usuario en la base de datos

        if (dao.register(u)) { // Intentamos registrar el usuario
            JOptionPane.showMessageDialog(this, "Registro exitoso");
            dispose();// Cerramos la ventana
        } else {
            JOptionPane.showMessageDialog(this, "Error: el email ya existe");
        }
    }

    //HASH DE CONTRASEÑA:
    private String hashPassword(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pass.getBytes());

            // Convertimos el hash a texto hexadecimal
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