package controlador;

import dao.IDiarioDAO;
import dao.JDBCTransactionDAO;
import model.EntradaDiario;
import model.Mood;
import model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class EditarEntradaDialog extends JDialog {

    private final EntradaDiario entrada;
    private final Runnable onSaved;

    private JTextField txtCancion;
    private JComboBox<Mood> comboMood;
    private JTextArea txtTexto;
    private JLabel lblFoto;
    private String rutaFoto;

    public EditarEntradaDialog(Window owner, EntradaDiario entrada, Runnable onSaved) {
        super(owner, "Editar entrada", ModalityType.APPLICATION_MODAL);
        this.entrada = entrada;
        this.onSaved = onSaved;

        initUI();
        cargarDatos();
    }

    private void initUI() {
        setSize(450, 520);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(content, BorderLayout.CENTER);

        // Canción
        content.add(new JLabel("Canción:"));
        txtCancion = new JTextField();
        content.add(txtCancion);
        content.add(Box.createVerticalStrut(10));

        // Mood
        content.add(new JLabel("Mood:"));
        comboMood = new JComboBox<>();
        content.add(comboMood);
        content.add(Box.createVerticalStrut(10));
        cargarMoods();

        // Texto
        content.add(new JLabel("Texto:"));
        txtTexto = new JTextArea(6, 20);
        txtTexto.setLineWrap(true);
        txtTexto.setWrapStyleWord(true);
        content.add(new JScrollPane(txtTexto));
        content.add(Box.createVerticalStrut(10));

        // Foto
        JPanel fotoRow = new JPanel(new BorderLayout(10, 0));
        JButton btnCambiarFoto = new JButton("Cambiar foto");
        lblFoto = new JLabel("(sin foto)");
        fotoRow.add(btnCambiarFoto, BorderLayout.WEST);
        fotoRow.add(lblFoto, BorderLayout.CENTER);
        content.add(fotoRow);

        btnCambiarFoto.addActionListener(e -> elegirFoto());

        // Botones abajo
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Guardar cambios");
        actions.add(btnCancelar);
        actions.add(btnGuardar);
        add(actions, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarCambios());
    }

    private void cargarMoods() {
        // reutiliza vuestro MoodDAO
        dao.MoodDAO moodDAO = new dao.MoodDAO();
        List<Mood> moods = moodDAO.getAllMoods();

        comboMood.removeAllItems();
        for (Mood m : moods) comboMood.addItem(m);
    }

    private void cargarDatos() {
        txtCancion.setText(entrada.getCancion() == null ? "" : entrada.getCancion());
        txtTexto.setText(entrada.getTextoDiario() == null ? "" : entrada.getTextoDiario());

        rutaFoto = entrada.getRutaFoto();
        lblFoto.setText(rutaFoto == null ? "(sin foto)" : new File(rutaFoto).getName());

        // Seleccionar mood actual
        for (int i = 0; i < comboMood.getItemCount(); i++) {
            Mood m = comboMood.getItemAt(i);
            if (m.getId() == entrada.getMoodId()) {
                comboMood.setSelectedIndex(i);
                break;
            }
        }
    }

    private void elegirFoto() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            rutaFoto = file.getAbsolutePath();
            lblFoto.setText(file.getName());
        }
    }

    private void guardarCambios() {
        String cancion = txtCancion.getText().trim();
        String texto = txtTexto.getText().trim();

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El texto no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (cancion.isEmpty()) {
            // vuestra tabla cancion es NOT NULL
            cancion = "(sin canción)";
        }

        Mood moodSel = (Mood) comboMood.getSelectedItem();
        if (moodSel == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un mood.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Actualizar objeto
        entrada.setCancion(cancion);
        entrada.setTextoDiario(texto);
        entrada.setRutaFoto(rutaFoto);
        entrada.setMoodId(moodSel.getId());
        entrada.setUserId(UserSession.userId); // seguridad

        IDiarioDAO dao = new JDBCTransactionDAO();
        boolean ok = dao.update(entrada);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Cambios guardados");
            dispose();
            if (onSaved != null) onSaved.run();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudieron guardar los cambios", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
