package controlador;
// Este paquete contiene las clases que controlan el flujo de la aplicación
// (qué pantalla se abre, cuándo se abre otra, etc.)

import javax.swing.SwingUtilities;
// SwingUtilities es una clase de ayuda de Swing.
// Se usa para ejecutar código de interfaz gráfica de forma segura.

public class AppMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // invokeLater significa literalmente:
            // "Ejecuta este código MÁS TARDE, cuando Swing esté listo".

            // Swing tiene un hilo especial llamado Event Dispatch Thread (EDT).
            // TODAS las ventanas, botones y componentes gráficos
            // deben crearse y mostrarse dentro de ese hilo.

            // invokeLater se encarga de:
            // 1) crear el hilo gráfico (si aún no existe)
            // 2) poner este código en la cola del hilo gráfico
            // 3) ejecutarlo en el momento correcto

            new HomeFrame().setVisible(true);
            // new HomeFrame():
            // - crea un objeto de la clase HomeFrame
            // - HomeFrame es una clase que representa una ventana (ya la hemos creado)

            // .setVisible(true):
            // - indica que la ventana debe mostrarse en pantalla
            // - si fuera false, la ventana EXISTIRÍA, pero NO se vería
        });
    }
}
