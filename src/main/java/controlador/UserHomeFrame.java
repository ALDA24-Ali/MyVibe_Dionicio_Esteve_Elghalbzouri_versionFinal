package controlador;

import model.UserSession;
import javax.swing.*;
import java.awt.*;

public class UserHomeFrame extends JFrame {

    private final String nombreUsuario;

    public UserHomeFrame(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        initUI();
    }

    private void initUI() {

        // Tamaño y configuración base
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Fondo degradado
        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        //  ZONA SUPERIOR (texto)
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Tu diario musical");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel saludo = new JLabel("Bienvenid@, " + nombreUsuario);
        saludo.setForeground(new Color(230, 230, 230));
        saludo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        saludo.setAlignmentX(CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalStrut(25));
        topPanel.add(titulo);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(saludo);
        topPanel.add(Box.createVerticalStrut(25));

        add(topPanel, BorderLayout.NORTH);

        //  ZONA CENTRAL
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        RoundedButton btnCrearEntrada =
                new RoundedButton("Crear nueva entrada", new Color(150, 90, 255));
        btnCrearEntrada.setAlignmentX(CENTER_ALIGNMENT);

        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        RoundedButton btnVerDiario =
                new RoundedButton("Ver diario", new Color(90, 150, 255));
        RoundedButton btnEstadisticas =
                new RoundedButton("Estadísticas", new Color(90, 200, 180));

        rowPanel.add(btnVerDiario);
        rowPanel.add(btnEstadisticas);

        centerPanel.add(Box.createVerticalStrut(60));
        centerPanel.add(btnCrearEntrada);
        centerPanel.add(Box.createVerticalStrut(35));
        centerPanel.add(rowPanel);

        add(centerPanel, BorderLayout.CENTER);

        // -----------------------
        //  BOTÓN ESTRELLA (RA)
        // -----------------------
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        NeonStarButton btnRA = new NeonStarButton();
        bottomPanel.add(btnRA);

        add(bottomPanel, BorderLayout.SOUTH);

        //  ACCIONES (vacías por ahora)

        // Aquí conectararemos las pantallas reales:
       btnCrearEntrada.addActionListener(e -> new NuevaEntradaFrame().setVisible(true));
        // btnVerDiario.addActionListener(e -> new DiarioFrame().setVisible(true));
        // btnEstadisticas.addActionListener(e -> new EstadisticasFrame().setVisible(true));
        // btnRA.addActionListener(e -> new OpcionesRAFrame().setVisible(true));
    }
}
