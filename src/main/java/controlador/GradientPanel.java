package controlador;
// Este panel personalizado pertenece al paquete controlador
// porque se usa directamente en la interfaz gráfica (Swing).

import javax.swing.*; // Importamos JPanel
import java.awt.*;    // Importamos Graphics, Color, Graphics2D, GradientPaint

// Esta clase define un panel con fondo degradado
// Se reutiliza en todas las pantallas para mantener el mismo estilo visual
public class GradientPanel extends JPanel {

    // Primer color del degradado (parte superior)
    private final Color color1 = new Color(120, 80, 255);   
    // Segundo color del degradado (parte inferior)
    private final Color color2 = new Color(80, 180, 255);   

    // Este método se ejecuta automáticamente cada vez que el panel se dibuja
    // o se redimensiona
    @Override
    protected void paintComponent(Graphics g) {

        // Llamamos al método original para que Swing pinte correctamente el panel
        super.paintComponent(g);

        // Convertimos Graphics a Graphics2D para poder usar funciones avanzadas
        Graphics2D g2d = (Graphics2D) g;

        // Obtenemos el ancho actual del panel
        int width = getWidth();

        // Obtenemos la altura actual del panel
        int height = getHeight();

        // Creamos un degradado vertical:
        // empieza en (0,0) con color1
        // termina en (0,height) con color2
        GradientPaint gp =
                new GradientPaint(0, 0, color1, 0, height, color2);

        // Indicamos que vamos a pintar usando ese degradado
        g2d.setPaint(gp);

        // Pintamos un rectángulo que cubre todo el panel
        g2d.fillRect(0, 0, width, height);
    }
}
