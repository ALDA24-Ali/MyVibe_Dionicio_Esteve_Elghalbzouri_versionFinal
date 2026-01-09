package controlador;

import model.EntradaDiario;
import model.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import javax.swing.DefaultListCellRenderer;


public class NuevaEntradaFrame extends JFrame {

    // Componentes (los guardamos como atributos para leerlos al guardar)
    private JTextField txtCancion;
    private JComboBox<model.Mood> comboMood; 
    private JTextArea txtDesahogo;

    private JLabel lblPreviewFoto;
    private String rutaFotoSeleccionada = null;

    public NuevaEntradaFrame() {
        initUI();
    }

    private void initUI() {
        // Ventana base
        setTitle("Nueva entrada");
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Fondo degradado (igual que vuestras pantallas)
        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Panel principal centrado con padding
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(25, 45, 25, 45));

        // ---------- TÍTULO (CUENTA) ----------
        JLabel lblTitulo = new JLabel("CUENTA");
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        content.add(lblTitulo);
        content.add(Box.createVerticalStrut(30));

        // ---------- 1) ENCUENTRA TU CANCIÓN ----------
        JLabel lblCancion = new JLabel("Encuentra tu canción!");
        lblCancion.setForeground(Color.WHITE);
        lblCancion.setFont(new Font("SansSerif", Font.PLAIN, 16));
        content.add(lblCancion);
        content.add(Box.createVerticalStrut(8));

        JPanel rowCancion = new JPanel(new BorderLayout(10, 0));
        rowCancion.setOpaque(false);

        txtCancion = new JTextField();
        txtCancion.setEditable(false); // se rellena al seleccionar canción
        txtCancion.setPreferredSize(new Dimension(10, 36));

        JButton btnBuscarCancion = new JButton("Buscar");
        btnBuscarCancion.setFocusPainted(false);

        rowCancion.add(txtCancion, BorderLayout.CENTER);
        rowCancion.add(btnBuscarCancion, BorderLayout.EAST);

        content.add(rowCancion);
        content.add(Box.createVerticalStrut(25));

        // Acción temporal (luego lo cambiáis por el buscador con iTunes)
        btnBuscarCancion.addActionListener(e -> buscarCancionTemporal());

        // ---------- 2) MOOD ----------
        JLabel lblMood = new JLabel("Selecciona tu mood:");
        lblMood.setForeground(Color.WHITE);
        lblMood.setFont(new Font("SansSerif", Font.PLAIN, 16));
        content.add(lblMood);
        content.add(Box.createVerticalStrut(8));

       comboMood = new JComboBox<>();

        comboMood.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof model.Mood) {
                    setText(((model.Mood) value).getNombre());
                } else {
                    setText("");
                }
                return this;
            }
        });

        comboMood.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        content.add(comboMood);
        content.add(Box.createVerticalStrut(25));

        // y DESPUÉS llamas a cargar los moods
        cargarMoodsDesdeBD();

        // ---------- 3) FOTO OPCIONAL ----------
        JPanel rowFotoTitle = new JPanel(new BorderLayout());
        rowFotoTitle.setOpaque(false);

        JLabel lblFoto = new JLabel("tienes ganas de insertar una foto?:");
        lblFoto.setForeground(Color.WHITE);
        lblFoto.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel lblOpcional = new JLabel("OPCIONAL!");
        lblOpcional.setForeground(new Color(230, 230, 230));
        lblOpcional.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblOpcional.setHorizontalAlignment(SwingConstants.RIGHT);

        rowFotoTitle.add(lblFoto, BorderLayout.WEST);
        rowFotoTitle.add(lblOpcional, BorderLayout.EAST);

        content.add(rowFotoTitle);
        content.add(Box.createVerticalStrut(10));

        JButton btnElegirFoto = new JButton("Elegir foto");
        btnElegirFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnElegirFoto.setFocusPainted(false);
        content.add(btnElegirFoto);
        content.add(Box.createVerticalStrut(12));

        // “Óvalo/recuadro” grande del esbozo (simulado con label + borde)
        lblPreviewFoto = new JLabel("Sin foto", SwingConstants.CENTER);
        lblPreviewFoto.setOpaque(true);
        lblPreviewFoto.setBackground(new Color(255, 255, 255, 230));
        lblPreviewFoto.setForeground(Color.DARK_GRAY);
        lblPreviewFoto.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        lblPreviewFoto.setPreferredSize(new Dimension(400, 140));
        lblPreviewFoto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        content.add(lblPreviewFoto);

        content.add(Box.createVerticalStrut(25));

        btnElegirFoto.addActionListener(e -> elegirFoto());

        // ---------- 4) DESAHÓGATE ----------
        JLabel lblDesahogo = new JLabel("Desahogate!");
        lblDesahogo.setForeground(Color.WHITE);
        lblDesahogo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        content.add(lblDesahogo);
        content.add(Box.createVerticalStrut(8));

        txtDesahogo = new JTextArea(6, 20);
        txtDesahogo.setLineWrap(true);
        txtDesahogo.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(txtDesahogo);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        content.add(scroll);

        content.add(Box.createVerticalStrut(25));

        // ---------- 5) GUARDAR ----------
        RoundedButton btnGuardar =
                new RoundedButton("guardar y subir a tu diario", new Color(150, 90, 255));
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(btnGuardar);

        btnGuardar.addActionListener(e -> guardarEntrada());

        // Meter todo al centro
        background.add(content, BorderLayout.CENTER);
    }

    /**
     * Temporal: simula el buscador con un input simple.
     * Luego esto lo cambiáis por el buscador con iTunes.
     */
    private void buscarCancionTemporal() {
        String input = JOptionPane.showInputDialog(
                this,
                "Escribe la canción (ej: Vámonos - Kidd Keo):",
                "Buscar canción (temporal)",
                JOptionPane.QUESTION_MESSAGE
        );
        if (input != null && !input.trim().isEmpty()) {
            txtCancion.setText(input.trim());
        }
    }

    private void elegirFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecciona una imagen");

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            rutaFotoSeleccionada = file.getAbsolutePath();

            // Mostrar nombre o mini preview (simple)
            lblPreviewFoto.setText(file.getName());
        }
    }

    private void guardarEntrada() {
        // Validaciones básicas
        String cancion = txtCancion.getText().trim();
        String texto = txtDesahogo.getText().trim();

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El texto del diario no puede estar vacío.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Mood real desde BD (id real, no índice)
        model.Mood moodSel = (model.Mood) comboMood.getSelectedItem();
        if (moodSel == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un mood.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int moodId = moodSel.getId();

        // UserId: de la sesión
        int userId = UserSession.userId; // si no hay login, quedará 0
        if (userId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay sesión iniciada (userId). Inicia sesión primero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Creamos la entrada
        EntradaDiario entrada = new EntradaDiario(
                LocalDateTime.now(),
                cancion.isEmpty() ? null : cancion,
                texto,
                rutaFotoSeleccionada,
                moodId,
                userId
        );

        // Por ahora solo confirmamos (más adelante conectamos con DAO)
        JOptionPane.showMessageDialog(this,
                "Entrada creada \n" +
                        "Canción: " + (entrada.getCancion() == null ? "(sin canción)" : entrada.getCancion()) + "\n" +
                        "MoodId: " + entrada.getMoodId() + "\n" +
                        "Foto: " + (entrada.getRutaFoto() == null ? "(sin foto)" : "OK") + "\n",
                "Guardado (temporal)",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    private void cargarMoodsDesdeBD() {
        dao.MoodDAO moodDAO = new dao.MoodDAO();
        java.util.List<model.Mood> moods = moodDAO.getAllMoods();

        comboMood.removeAllItems();

        if (moods.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los moods desde la base de datos.\n" +
                            "Revisa que la tabla 'moods' tenga datos y que MySQL esté encendido.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (model.Mood m : moods) {
    comboMood.addItem(m);
}
if (comboMood.getItemCount() > 0) {
    comboMood.setSelectedIndex(0);
}

    }
}
