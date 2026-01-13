package controlador;

import model.EntradaDiario;
import model.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import dao.IDiarioDAO;
import dao.JDBCTransactionDAO;


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

        setTitle("Nueva entrada");
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(25, 45, 25, 45));

        // ===== TÍTULO =====
        JLabel lblTitulo = new JLabel("Crea tu nueva entrada para el diario");
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        content.add(lblTitulo);
        content.add(Box.createVerticalStrut(35));

        // ===== CANCIÓN (centrado bonito) =====
        JPanel panelCancion = new JPanel();
        panelCancion.setOpaque(false);
        panelCancion.setLayout(new BoxLayout(panelCancion, BoxLayout.Y_AXIS));
        panelCancion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCancion.setMaximumSize(new Dimension(420, 70));

        JLabel lblCancion = new JLabel("Canción");
        lblCancion.setForeground(Color.WHITE);
        lblCancion.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblCancion.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtCancion = new JTextField();
        txtCancion.setMaximumSize(new Dimension(420, 36));

        panelCancion.add(lblCancion);
        panelCancion.add(Box.createVerticalStrut(6));
        panelCancion.add(txtCancion);

        content.add(panelCancion);
        content.add(Box.createVerticalStrut(30));


        

         // ===== MOOD =====
        JLabel lblMood = new JLabel("Selecciona tu mood:");
        lblMood.setForeground(Color.WHITE);
        lblMood.setFont(new Font("SansSerif", Font.PLAIN, 16));
        content.add(lblMood);
        content.add(Box.createVerticalStrut(8));

        comboMood = new JComboBox<>();
        comboMood.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        comboMood.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof model.Mood) {
                    setText(((model.Mood) value).getNombre());
                }
                return this;
            }
        });

        content.add(comboMood);
        content.add(Box.createVerticalStrut(25));

        cargarMoodsDesdeBD();

        // ===== FOTO OPCIONAL =====
        JPanel rowFotoTitle = new JPanel(new BorderLayout());
        rowFotoTitle.setOpaque(false);

        JLabel lblFoto = new JLabel("¿Quieres añadir una foto?");
        lblFoto.setForeground(Color.WHITE);
        lblFoto.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel lblOpcional = new JLabel("OPCIONAL");
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

        // ===== DESAHOGO =====
        JLabel lblDesahogo = new JLabel("Desahógate");
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

        content.add(Box.createVerticalStrut(30));

        // ===== GUARDAR =====
        RoundedButton btnGuardar =
                new RoundedButton("guardar y subir a tu diario", new Color(150, 90, 255));
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.addActionListener(e -> guardarEntrada());
        content.add(btnGuardar);

        background.add(content, BorderLayout.CENTER);
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

    // Mood real desde BD (id real, no índice)
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

    // Guardar en BD con JDBC (real)
    dao.IDiarioDAO diarioDAO = new dao.JDBCTransactionDAO();
    boolean ok = diarioDAO.insert(entrada);

    if (ok) {
        JOptionPane.showMessageDialog(this,
                "Entrada guardada",
                "Guardado",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    } else {
        JOptionPane.showMessageDialog(this,
                "No se pudo guardar la entrada",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
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
