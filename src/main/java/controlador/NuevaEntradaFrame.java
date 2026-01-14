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
    setResizable(true);

    GradientPanel background = new GradientPanel();
    background.setLayout(new BorderLayout());
    setContentPane(background);

    // Panel principal con padding lateral
    JPanel content = new JPanel();
    content.setOpaque(false);
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    // Reducimos un poco el margen para que se vea más amplio (20px arriba/abajo, 30px lados)
    content.setBorder(new EmptyBorder(25, 30, 25, 30));

    // ===== TÍTULO =====
    JLabel lblTitulo = new JLabel("Crea tu nueva entrada para el diario");
    lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblTitulo.setForeground(Color.WHITE);
    lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
    // Importante: Forzar a que el label pueda ocupar todo el ancho
    lblTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    content.add(lblTitulo);
    content.add(Box.createVerticalStrut(30));

    // ===== CANCIÓN =====
    JLabel lblCancion = new JLabel("Canción");
    lblCancion.setForeground(Color.WHITE);
    lblCancion.setFont(new Font("SansSerif", Font.PLAIN, 16));
    lblCancion.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.add(lblCancion);
    content.add(Box.createVerticalStrut(8));

    txtCancion = new JTextField();
    txtCancion.setAlignmentX(Component.LEFT_ALIGNMENT);
    // Quitamos límites de ancho
    txtCancion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    content.add(txtCancion);
    content.add(Box.createVerticalStrut(25));

    // ===== MOOD =====
    JLabel lblMood = new JLabel("Selecciona tu mood:");
    lblMood.setForeground(Color.WHITE);
    lblMood.setFont(new Font("SansSerif", Font.PLAIN, 16));
    lblMood.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.add(lblMood);
    content.add(Box.createVerticalStrut(8));

    comboMood = new JComboBox<>();
    comboMood.setAlignmentX(Component.LEFT_ALIGNMENT);
    comboMood.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    
    // El renderer se mantiene igual (Lógica interna)
    comboMood.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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
    // Panel para el título y el texto "OPCIONAL" que ocupe todo el ancho
    JPanel panelFotoHeader = new JPanel(new BorderLayout());
    panelFotoHeader.setOpaque(false);
    panelFotoHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
    panelFotoHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

    JLabel lblFoto = new JLabel("¿Quieres añadir una foto?");
    lblFoto.setForeground(Color.WHITE);
    lblFoto.setFont(new Font("SansSerif", Font.PLAIN, 16));

    JLabel lblOpcional = new JLabel("OPCIONAL");
    lblOpcional.setForeground(new Color(230, 230, 230));
    lblOpcional.setFont(new Font("SansSerif", Font.BOLD, 12));

    panelFotoHeader.add(lblFoto, BorderLayout.WEST);
    panelFotoHeader.add(lblOpcional, BorderLayout.EAST);

    content.add(panelFotoHeader);
    content.add(Box.createVerticalStrut(10));

    JButton btnElegirFoto = new JButton("Elegir foto");
    btnElegirFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.add(btnElegirFoto);
    content.add(Box.createVerticalStrut(12));

    lblPreviewFoto = new JLabel("Sin foto", SwingConstants.CENTER);
    lblPreviewFoto.setOpaque(true);
    lblPreviewFoto.setBackground(new Color(255, 255, 255, 230));
    lblPreviewFoto.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
    lblPreviewFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
    // Esto asegura que el recuadro de la foto también se estire
    lblPreviewFoto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    content.add(lblPreviewFoto);

    content.add(Box.createVerticalStrut(25));
    btnElegirFoto.addActionListener(e -> elegirFoto());

    // ===== DESAHOGO =====
    JLabel lblDesahogo = new JLabel("Desahógate");
    lblDesahogo.setForeground(Color.WHITE);
    lblDesahogo.setFont(new Font("SansSerif", Font.PLAIN, 16));
    lblDesahogo.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.add(lblDesahogo);
    content.add(Box.createVerticalStrut(6));

    txtDesahogo = new JTextArea(8, 10);
    txtDesahogo.setLineWrap(true);
    txtDesahogo.setWrapStyleWord(true);
    JScrollPane scroll = new JScrollPane(txtDesahogo);
    scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
    scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    content.add(scroll);

    // USAMOS CENTER para que el panel se expanda a todo el ancho disponible
    background.add(content, BorderLayout.CENTER);


    // Espacio antes del botón
    content.add(Box.createVerticalStrut(30));

        // ===== BOTÓN GUARDAR (CENTRADITO Y COMPLETO) =====
    // Creamos un panel con FlowLayout para que el botón mantenga su tamaño original (el del texto)
    JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panelBoton.setOpaque(false);
    panelBoton.setAlignmentX(Component.LEFT_ALIGNMENT); // Para que el panel ocupe todo el ancho del BoxLayout
    panelBoton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

    RoundedButton btnGuardar = new RoundedButton("Guárdalo y súbelo a tu diario!", new Color(150, 90, 255));
    btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 16));
    btnGuardar.addActionListener(e -> guardarEntrada());
    
    panelBoton.add(btnGuardar);
    content.add(panelBoton);

    // Añadir el contenido al fondo
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
