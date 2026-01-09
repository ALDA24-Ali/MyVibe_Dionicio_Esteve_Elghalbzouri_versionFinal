package dao; // Paquete donde se encuentra la clase


import model.EntradaDiario; // Importa el modelo EntradaDiario
import org.w3c.dom.Document; // Maneja el documento XML
import org.w3c.dom.Element; // Maneja los elementos del XML


import javax.xml.parsers.DocumentBuilder; // Constructor del documento XML
import javax.xml.parsers.DocumentBuilderFactory; // Fábrica para crear DocumentBuilder
import javax.xml.transform.OutputKeys; // Opciones de salida del XML
import javax.xml.transform.Transformer; // Transforma el documento en archivo
import javax.xml.transform.TransformerFactory; // Fábrica del Transformer
import javax.xml.transform.dom.DOMSource; // Fuente del documento XML
import javax.xml.transform.stream.StreamResult; // Resultado en un archivo
import java.io.File; // Manejo de archivos
import java.util.List; // Uso de listas


public class XMLExporter { // Clase encargada de exportar datos a XML


    public boolean exportToXML(List<EntradaDiario> lista, String rutaArchivo) {
        // Método que recibe una lista de entradas y la ruta del archivo XML
        try {
            // Crear la fábrica del documento XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Crear el constructor del documento
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Crear el documento XML vacío
            Document doc = builder.newDocument();


            // Crear el nodo raíz <diario>
            Element root = doc.createElement("diario");
            // Añadir el nodo raíz al documento
            doc.appendChild(root);


            // Recorrer cada entrada del diario
            for (EntradaDiario e : lista) {


                // Crear el nodo <entrada>
                Element entrada = doc.createElement("entrada");


                // Crear el nodo <id>
                Element id = doc.createElement("id");
                // Asignar el valor del id
                id.appendChild(doc.createTextNode(String.valueOf(e.getId())));
                // Añadir el nodo id a entrada
                entrada.appendChild(id);


                // Crear el nodo <fecha>
                Element fecha = doc.createElement("fecha");
                // Asignar la fecha como texto
                fecha.appendChild(doc.createTextNode(e.getFecha().toString()));
                // Añadir fecha a entrada
                entrada.appendChild(fecha);


                // Crear el nodo <mood_id>
                Element mood = doc.createElement("mood_id");
                // Asignar el mood id
                mood.appendChild(doc.createTextNode(String.valueOf(e.getMoodId())));
                // Añadir mood a entrada
                entrada.appendChild(mood);


                // Crear el nodo <user_id>
                Element user = doc.createElement("user_id");
                // Asignar el user id
                user.appendChild(doc.createTextNode(String.valueOf(e.getUserId())));
                // Añadir user a entrada
                entrada.appendChild(user);


                // Crear el nodo <cancion>
                Element cancion = doc.createElement("cancion");
                // Asignar la canción
                cancion.appendChild(doc.createTextNode(e.getCancion()));
                // Añadir canción a entrada
                entrada.appendChild(cancion);


                // Crear el nodo <texto_diario>
                Element texto = doc.createElement("texto_diario");
                // Asignar el texto del diario o vacío si es null
                texto.appendChild(doc.createTextNode(
                        e.getTextoDiario() == null ? "" : e.getTextoDiario()
                ));
                // Añadir texto a entrada
                entrada.appendChild(texto);


                // Crear el nodo <ruta_foto>
                Element foto = doc.createElement("ruta_foto");
                // Asignar la ruta de la foto o vacío si es null
                foto.appendChild(doc.createTextNode(
                        e.getRutaFoto() == null ? "" : e.getRutaFoto()
                ));
                // Añadir foto a entrada
                entrada.appendChild(foto);


                // Añadir la entrada completa al nodo raíz
                root.appendChild(entrada);
            }


            // Crear el transformer para guardar el XML
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            // Activar indentación
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            // Definir número de espacios de indentación
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");


            // Guardar el documento XML en el archivo indicado
            transformer.transform(new DOMSource(doc), new StreamResult(new File(rutaArchivo)));
            // Si todo va bien, devolver true
            return true;


        } catch (Exception ex) {
            // Mostrar mensaje de error si algo falla
            System.out.println("Error exportando XML: " + ex.getMessage());
            // Devolver false si ocurre un error
            return false;
        }
    }
}
