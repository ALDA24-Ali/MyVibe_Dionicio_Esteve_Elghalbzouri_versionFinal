
package dao; // Paquete donde se encuentra la clase

import java.io.*; // Clases para manejo de streams y serialización


// Clase que permite agregar objetos a un ObjectOutputStream sin sobrescribir el encabezado
public class AppendableObjectOutputStream extends ObjectOutputStream {


    // Constructor que recibe un OutputStream y lo pasa al constructor de ObjectOutputStream
    public AppendableObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }


    @Override
    protected void writeStreamHeader() throws IOException {
        // Sobrescribe el encabezado normal para evitar errores al anexar objetos
        // reset() reinicia el stream sin escribir un nuevo encabezado
        reset();
    }
}