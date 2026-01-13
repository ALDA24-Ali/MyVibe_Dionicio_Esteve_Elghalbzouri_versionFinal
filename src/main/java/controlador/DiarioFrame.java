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
    JPanel card = new JPanel(new BorderLayout(10, 10));
    card.setBackground(new Color(255, 255, 255, 235));
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 150, 255), 2),
            new EmptyBorder(12, 12, 12, 12)
    ));

    // ---- HEADER: fecha (centro/derecha) + menú ⋮ (derecha) ----
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);

    JLabel fecha = new JLabel(String.valueOf(e.getFecha()));
    fecha.setFont(new Font("SansSerif", Font.PLAIN, 12));
    fecha.setForeground(new Color(70, 70, 70));

    JButton btnMenu = new JButton("⋮"); // tres puntitos
    btnMenu.setFont(new Font("SansSerif", Font.BOLD, 16));
    btnMenu.setFocusPainted(false);
    btnMenu.setBorderPainted(false);
    btnMenu.setContentAreaFilled(false);
    btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Popup menu
    JPopupMenu menu = new JPopupMenu();
    JMenuItem itemEditar = new JMenuItem("Editar");
    JMenuItem itemEliminar = new JMenuItem("Eliminar");
    menu.add(itemEditar);
    menu.add(itemEliminar);

    // Mostrar menú
    btnMenu.addActionListener(ev -> menu.show(btnMenu, 0, btnMenu.getHeight()));

    // Acciones
    itemEliminar.addActionListener(ev -> eliminarEntrada(e));
    itemEditar.addActionListener(ev -> editarEntrada(e)); // por ahora placeholder

    header.add(fecha, BorderLayout.CENTER);
    header.add(btnMenu, BorderLayout.EAST);

    card.add(header, BorderLayout.NORTH);

    // ---- BODY ----
    JPanel body = new JPanel();
    body.setOpaque(false);
    body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

    JLabel cancion = new JLabel("CANCIÓN: " + (e.getCancion() == null ? "(sin canción)" : e.getCancion()));
    cancion.setFont(new Font("SansSerif", Font.BOLD, 14));
    body.add(cancion);
    body.add(Box.createVerticalStrut(6));

    JLabel mood = new JLabel("MOOD ID: " + e.getMoodId());
    mood.setFont(new Font("SansSerif", Font.PLAIN, 13));
    body.add(mood);
    body.add(Box.createVerticalStrut(10));

    JTextArea texto = new JTextArea(e.getTextoDiario());
    texto.setEditable(false);
    texto.setLineWrap(true);
    texto.setWrapStyleWord(true);
    texto.setOpaque(false);
    texto.setFont(new Font("SansSerif", Font.PLAIN, 13));
    body.add(texto);

    card.add(body, BorderLayout.CENTER);

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
