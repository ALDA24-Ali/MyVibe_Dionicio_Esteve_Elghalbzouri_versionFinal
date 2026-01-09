package controlador;
// Este botón personalizado pertenece al paquete controlador
// porque forma parte directa de la interfaz gráfica (Swing).

import javax.swing.*; // JButton
import java.awt.*;    // Color, Font, Graphics, Graphics2D, Dimension, Cursor

// Botón personalizado con forma circular y efecto neón
// Se utiliza en la aplicación como botón especial (estrella),
// por ejemplo para exportar datos a XML.
public class NeonStarButton extends JButton {

    public NeonStarButton() {

        // Llamamos al constructor de JButton y usamos el símbolo de estrella
        super("★");

        // Fuente grande y en negrita para que la estrella sea visible
        setFont(new Font("SansSerif", Font.BOLD, 26));

        // Color del texto (estrella)
        setForeground(Color.WHITE);

        // Desactivamos el fondo por defecto del botón
        // porque lo vamos a pintar manualmente
        setContentAreaFilled(false);

        // Quitamos el borde estándar del botón
        setBorderPainted(false);

        // Quitamos el borde de foco (cuando se hace click)
        setFocusPainted(false);

        // Tamaño fijo del botón (forma casi circular)
        setPreferredSize(new Dimension(70, 70));

        // Cambia el cursor al pasar por encima (mano)
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Método que se encarga de dibujar el botón manualmente
    @Override
    protected void paintComponent(Graphics g) {

        // Creamos una copia del objeto Graphics y lo convertimos a Graphics2D
        // Graphics2D permite usar antialiasing y formas avanzadas
        Graphics2D g2 = (Graphics2D) g.create();

        // Activamos antialiasing para bordes suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Obtenemos el ancho y alto actuales del botón
        int w = getWidth();
        int h = getHeight();

        // Definimos los colores del efecto neón
        Color base = new Color(180, 100, 255);           // Morado principal
        Color glow = new Color(220, 150, 255, 120);      // Brillo exterior (con transparencia)

        // GLOW EXTERIOR
        // Dibujamos un círculo grande con color semitransparente
        g2.setColor(glow);
        g2.fillOval(4, 4, w - 8, h - 8);

        // CÍRCULO INTERIOR 
        // Dibujamos un círculo más pequeño encima
        g2.setColor(base);
        g2.fillOval(10, 10, w - 20, h - 20);

        // TEXTO 
        // Dibujamos el texto del botón (la estrella ★)
        // usando el comportamiento estándar de JButton
        super.paintComponent(g2);

        // Liberamos recursos gráficos
        g2.dispose();
    }
}
