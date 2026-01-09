package controlador;

import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {//extiende de jframe, esto significa que ES una ventana.
//jframe es la clase base de swing para crear ventanas.


    public HomeFrame() {
        super("MyVibe — Diario Musical");//llamamos al consturctor de jframe. estre es el titulo de la ventana (arriba del todo).
        initUI();//llama a un metodo privado donde se construye toda la interfaz.
    }

    private void initUI() {

        // Tamaño de la ventana
        setSize(600, 800);//ancho x alto
        setLocationRelativeTo(null);//centra la ventana en la pantalla
        setDefaultCloseOperation(EXIT_ON_CLOSE);//cuando el usuario cierra esta ventana -> se cierra la app. (es perfecto para la pantalla princ.)
        setResizable(false);//evita que el usuario redimensione la ventana. (mantiene fijo).

        // Fondo degradado
        GradientPanel background = new GradientPanel();//GP es una clase nuestra que extiende de Jpanel y dibuja un fondo personalizado.
        background.setLayout(new BorderLayout());
        setContentPane(background);//todo lo que añada a esta ventana ira encima de este panel.

        //  TÍTULO SUPERIOR
        JPanel topPanel = new JPanel();//este es el contenedor.
        topPanel.setOpaque(false);//transparente (se ve el degradado)
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));//coloca los elementos en vertical.

        // Cargar icono original
        ImageIcon originalIcon = new ImageIcon(HomeFrame.class.getResource("/images/ICONOMYVIBEDEF.png"));
        //esto carga una imagen desde la ruta especificada, getresource busca deltro del proyecto empaquetado.


        // A partir del icono original (ImageIcon), obtenemos la imagen real que contiene.
        // Esto es necesario porque para cambiar el tamaño no se trabaja directamente con ImageIcon,
        // sino con el tipo Image.
        Image scaledImg = originalIcon.getImage()
                // Se crea una NUEVA imagen redimensionada a 80x100 píxeles.
                // SCALE_SMOOTH indica que el reescalado se hace priorizando la calidad visual
                // (mejor para logos e iconos visibles en la interfaz).
                .getScaledInstance(80, 100, Image.SCALE_SMOOTH);

        // Una vez la imagen ya está redimensionada, se vuelve a convertir en ImageIcon,
        // ya que Swing necesita un ImageIcon para poder mostrar imágenes en un JLabel.
        ImageIcon logoTitulo = new ImageIcon(scaledImg);

        // Se crea un JLabel que mostrará el icono redimensionado.
        JLabel lblLogo = new JLabel(logoTitulo);

        // Se centra el JLabel horizontalmente dentro de un contenedor con BoxLayout en eje Y.
        // Esto asegura que el logo quede visualmente centrado en la interfaz.
        lblLogo.setAlignmentX(CENTER_ALIGNMENT);

        //NOTA:En BoxLayout, el eje (X_AXIS o Y_AXIS) define cómo se colocan los componentes, y la alineación (AlignmentX o AlignmentY) define cómo se posicionan respecto al eje contrario.

        //SUBTITULO:
        JLabel lblSubtitulo = new JLabel("Diario Musical & Emocional");//se introduce el texto del sub.
        lblSubtitulo.setForeground(Color.WHITE);// que sera de color blanco
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));//con este tamaño y fuente.
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);//y centrado.

        //TEXTO DE BIENVENIDA:
        JLabel lblBienvenida = new JLabel("Bienvenid@ a MyVibe");//se introduce la bienvenida (texto)
        lblBienvenida.setForeground(new Color(230, 230, 255));//definimos un color personalizado uisando RGB (blanco mas suave que el blanco puro.)
        lblBienvenida.setFont(new Font("SansSerif", Font.BOLD, 28));//definimos fuente tamaño y en negrita.
        lblBienvenida.setAlignmentX(CENTER_ALIGNMENT);//y centrado.

        //Espaciado y orden:
        topPanel.add(Box.createVerticalStrut(35));//Añade un espacio vertical vacío de 35 píxeles.Sirve para separar visualmente los elementos.
        topPanel.add(lblLogo);//añade el logo al panel superior.
        topPanel.add(Box.createVerticalStrut(10));//Añade un pequeño espacio debajo del logo.
        topPanel.add(lblSubtitulo);//Añade el subtítulo al panel.
        topPanel.add(Box.createVerticalStrut(30));//Añade un espacio mayor para separar bloques.
        topPanel.add(lblBienvenida);//Añade el texto de bienvenida.
        topPanel.add(Box.createVerticalStrut(30));//Añade espacio inferior para que no quede pegado al siguiente bloque.

        add(topPanel, BorderLayout.NORTH);//Coloca el topPanel en la parte superior de la ventana.BorderLayout.NORTH significa “arriba”.


        //  PANEL CENTRAL (BOTONES):
        JPanel centerPanel = new JPanel();//Se crea un nuevo panel que contendrá los botones.
        centerPanel.setOpaque(false);//Hace el panel transparente para que se vea el fondo degradado.
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));//Los componentes del panel se colocan uno debajo de otro.

        RoundedButton btnRegistro = new RoundedButton("Crear cuenta",new Color(180, 90, 255));// se crea un boton redondeado personalizado, con texto y color de fondo morado.
        RoundedButton btnLogin = new RoundedButton("Iniciar sesión",new Color(90, 160, 255));//lo mismo aqui.

        //Centra ambos botones horizontalmente dentro del panel vertical:
        btnRegistro.setAlignmentX(CENTER_ALIGNMENT);
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(80));//Espacio superior grande antes de los botones.
        centerPanel.add(btnRegistro);//Añade el botón “Crear cuenta”.
        centerPanel.add(Box.createVerticalStrut(20));//Espacio entre los dos botones.
        centerPanel.add(btnLogin);//Añade el botón “Iniciar sesión”.

        add(centerPanel, BorderLayout.CENTER);//Coloca el panel de botones en el centro de la ventana.


        //  ACCIONES DE LOS BOTONES:
        btnLogin.addActionListener(e -> new LoginFrame().setVisible(true));//Cuando el usuario pulsa “Iniciar sesión”:se crea la ventana LoginFrame y se muestra en pantalla
        btnRegistro.addActionListener(e -> new RegisterFrame().setVisible(true));//Cuando pulsa “Crear cuenta”: se abre RegisterFrame
    }
}


//  CLASE DEL BOTÓN REDONDEADO (FUERA DE HomeFrame)
class RoundedButton extends JButton {
//Se crea una clase personalizada que hereda de JButton. Permite reutilizar el mismo estilo de botón.

    public RoundedButton(String text, Color bg) {
        super(text);//Llama al constructor de JButton con el texto del botón.
        
        //Elimina el borde y el efecto de foco por defecto:
        setFocusPainted(false);
        setBorderPainted(false);

        setForeground(Color.WHITE);//texto blanco
        setBackground(bg);//fodo del color pasado por parametro.
        setOpaque(false);//permite dibujar el fondo manualmente.

        setFont(new Font("SansSerif", Font.BOLD, 15));//Define la fuente del texto del botón.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//Cambia el cursor a una mano cuando pasas el ratón. E Indica que es clicable.
        
        //Define el tamaño del botón y evita que se estire:
        setPreferredSize(new Dimension(220, 50));
        setMaximumSize(new Dimension(220, 50));

        // Hover
        Color base = bg;//Guarda el color original del botón.
        addMouseListener(new java.awt.event.MouseAdapter() {//Detecta cuando el ratón entra o sale del botón.
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(base.brighter());//Al pasar el ratón, el botón se aclara.
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(base);//Al salir, vuelve al color original.
            }
        });
    }

    //DIBUJO PERSONALIZADO:
    @Override
    protected void paintComponent(Graphics g) {//Método que se encarga de dibujar el botón.
        Graphics2D g2 = (Graphics2D) g.create();//Se usa Graphics2D para dibujo avanzado.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//Activa suavizado de bordes (se ve más bonito).

        //Dibuja el fondo del botón como un rectángulo redondeado:
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);

        super.paintComponent(g2);//Dibuja el texto del botón encima del fondo.
        g2.dispose();//Libera recursos gráficos.
    }
}
