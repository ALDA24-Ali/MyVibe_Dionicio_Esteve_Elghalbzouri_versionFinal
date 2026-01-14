package controlador;

import dao.IDiarioDAO;
import dao.JDBCTransactionDAO;
import model.EntradaDiario;
import model.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DiarioFrame extends JFrame {

    private JPanel listPanel;

    public DiarioFrame() {
        initUI();
        cargarEntradas();
    }

    private void initUI() {
        setTitle("Ver diario");
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Si tenéis GradientPanel, úsalo. Si no, cambia por new JPanel()
        JPanel root;
        try {
            root = (JPanel) Class.forName("controlador.GradientPanel")
                    .getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            root = new JPanel();
            root.setBackground(new Color(120, 120, 255));
        }
        root.setLayout(new BorderLayout());
        setContentPane(root);

        JLabel title = new JLabel("TU DIARIO", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        root.add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        root.add(scroll, BorderLayout.CENTER);
    }

    private void cargarEntradas() {
        listPanel.removeAll();

        int userId = UserSession.userId;
        if (userId <= 0) {
            JLabel err = new JLabel("No hay sesión iniciada.", SwingConstants.CENTER);
            err.setForeground(Color.WHITE);
            listPanel.add(err);
            refrescar();
            return;
        }

        IDiarioDAO dao = new JDBCTransactionDAO();
        List<EntradaDiario> entradas = dao.getByUserId(userId);

        if (entradas == null || entradas.isEmpty()) {
            JLabel empty = new JLabel("Aún no tienes entradas :(", SwingConstants.CENTER);
            empty.setForeground(Color.WHITE);
            listPanel.add(empty);
            refrescar();
            return;
        }

        for (EntradaDiario e : entradas) {
            listPanel.add(crearTarjetaBasica(e));
            listPanel.add(Box.createVerticalStrut(12));
        }

        refrescar();
    }

    private JPanel crearTarjetaBasica(EntradaDiario e) {
    // 0. LÓGICA DE APOYO PARA MOOD Y TAMAÑO
    boolean tieneFoto = (e.getRutaFoto() != null && !e.getRutaFoto().isEmpty());
    
    // Mapeo manual para evitar el 'null' sin tocar la base de datos
    String nombreEmocion;
    switch (e.getMoodId()) {
        case 1: nombreEmocion = "felicidad"; break;
        case 2: nombreEmocion = "tristeza"; break;
        case 3: nombreEmocion = "ansiedad"; break;
        case 4: nombreEmocion = "ira"; break;
        case 5: nombreEmocion = "enamorad@"; break;
        case 6: nombreEmocion = "miedo"; break;
        case 7: nombreEmocion = "nostalgia"; break;
        default: nombreEmocion = "desconocido"; break;
    }

    // Definimos el tamaño: 500x500 si hay foto, 500x200 (más pequeño) si no hay
    int ancho = 500;
    int alto = tieneFoto ? 500 : 200; 

    JPanel card = new JPanel(new BorderLayout(10, 10));
    card.setBackground(new Color(255, 255, 255, 245));
    card.setPreferredSize(new Dimension(ancho, alto));
    card.setMaximumSize(new Dimension(ancho, alto));
    card.setMinimumSize(new Dimension(ancho, alto));
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 150, 255), 2),
            new EmptyBorder(15, 15, 15, 15)
    ));

    // 1. HEADER (Canción a la izq, Menú ⋮ a la derecha)
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);

    JLabel lblCancion = new JLabel(e.getCancion() == null || e.getCancion().isEmpty() ? 
                                   "Sin canción" : e.getCancion().toUpperCase());
    lblCancion.setFont(new Font("SansSerif", Font.BOLD, 15));

    JButton btnMenu = new JButton("⋮");
    btnMenu.setFont(new Font("SansSerif", Font.BOLD, 18));
    btnMenu.setBorderPainted(false);
    btnMenu.setContentAreaFilled(false);
    
    // El menú desplegable se mantiene igual
    JPopupMenu menu = new JPopupMenu();
    JMenuItem itemEditar = new JMenuItem("Editar");
    JMenuItem itemEliminar = new JMenuItem("Eliminar");
    menu.add(itemEditar); menu.add(itemEliminar);
    btnMenu.addActionListener(ev -> menu.show(btnMenu, 0, btnMenu.getHeight()));
    itemEliminar.addActionListener(ev -> eliminarEntrada(e));
    itemEditar.addActionListener(ev -> editarEntrada(e));

    header.add(lblCancion, BorderLayout.WEST);
    header.add(btnMenu, BorderLayout.EAST);
    card.add(header, BorderLayout.NORTH);

    // 2. CENTER (Mood + Foto + Texto)
    JPanel centerPanel = new JPanel();
    centerPanel.setOpaque(false);
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

    // Mostrar el nombre de la emoción mapeado
    JLabel lblMood = new JLabel("Mood: " + nombreEmocion);
    lblMood.setFont(new Font("SansSerif", Font.ITALIC, 14));
    lblMood.setForeground(new Color(100, 100, 100));
    lblMood.setAlignmentX(Component.LEFT_ALIGNMENT);
    centerPanel.add(lblMood);
    centerPanel.add(Box.createVerticalStrut(10));

    // Foto (Solo si existe)
    if (tieneFoto) {
        JLabel lblFoto = new JLabel();
        lblFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(e.getRutaFoto());
            // Ajustamos la imagen a un tamaño que deje espacio al texto (aprox 200px alto)
            Image img = icon.getImage().getScaledInstance(-1, 200, Image.SCALE_SMOOTH);
            lblFoto.setIcon(new ImageIcon(img));
            lblFoto.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        } catch (Exception ex) {
            lblFoto.setText("[Error al cargar imagen]");
        }
        centerPanel.add(lblFoto);
        centerPanel.add(Box.createVerticalStrut(10));
    }

    // Texto Desahogo
    JTextArea texto = new JTextArea(e.getTextoDiario());
    texto.setEditable(false);
    texto.setLineWrap(true);
    texto.setWrapStyleWord(true);
    texto.setOpaque(false);
    texto.setFont(new Font("SansSerif", Font.PLAIN, 14));
    texto.setAlignmentX(Component.LEFT_ALIGNMENT);
    centerPanel.add(texto);

    card.add(centerPanel, BorderLayout.CENTER);

    // 3. SOUTH (Fecha abajo a la derecha)
    JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    footer.setOpaque(false);
    JLabel lblFecha = new JLabel(String.valueOf(e.getFecha()));
    lblFecha.setFont(new Font("SansSerif", Font.PLAIN, 11));
    lblFecha.setForeground(Color.GRAY);
    footer.add(lblFecha);

    card.add(footer, BorderLayout.SOUTH);

    return card;
}


    private void refrescar() {
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void eliminarEntrada(model.EntradaDiario e) {
    int r = JOptionPane.showConfirmDialog(
            this,
            "¿Eliminar esta entrada?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
    );

    if (r != JOptionPane.YES_OPTION) return;

    dao.IDiarioDAO dao = new dao.JDBCTransactionDAO();
    boolean ok = dao.delete(e.getId()); // requiere que EntradaDiario tenga getId()

    if (ok) {
        JOptionPane.showMessageDialog(this, "Entrada eliminada");
        cargarEntradas(); // recarga lista
    } else {
        JOptionPane.showMessageDialog(this, "No se pudo eliminar", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void editarEntrada(model.EntradaDiario e) {
    EditarEntradaDialog dialog = new EditarEntradaDialog(this, e, this::cargarEntradas);
    dialog.setVisible(true);
}


}
