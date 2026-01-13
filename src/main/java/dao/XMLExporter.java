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
    try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("diario");
        doc.appendChild(root);

        for (EntradaDiario e : lista) {
            Element entrada = doc.createElement("entrada");

            Element id = doc.createElement("id");
            id.appendChild(doc.createTextNode(String.valueOf(e.getId())));
            entrada.appendChild(id);

            Element fecha = doc.createElement("fecha");
            fecha.appendChild(doc.createTextNode(
                    e.getFecha() == null ? "" : e.getFecha().toString()
            ));
            entrada.appendChild(fecha);

            Element mood = doc.createElement("mood_id");
            mood.appendChild(doc.createTextNode(String.valueOf(e.getMoodId())));
            entrada.appendChild(mood);

            Element user = doc.createElement("user_id");
            user.appendChild(doc.createTextNode(String.valueOf(e.getUserId())));
            entrada.appendChild(user);

            Element cancion = doc.createElement("cancion");
            cancion.appendChild(doc.createTextNode(
                    e.getCancion() == null ? "" : e.getCancion()
            ));
            entrada.appendChild(cancion);

            Element texto = doc.createElement("texto_diario");
            texto.appendChild(doc.createTextNode(
                    e.getTextoDiario() == null ? "" : e.getTextoDiario()
            ));
            entrada.appendChild(texto);

            Element foto = doc.createElement("ruta_foto");
            foto.appendChild(doc.createTextNode(
                    e.getRutaFoto() == null ? "" : e.getRutaFoto()
            ));
            entrada.appendChild(foto);

            root.appendChild(entrada);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        transformer.transform(new DOMSource(doc), new StreamResult(new File(rutaArchivo)));
        return true;

    } catch (Exception ex) {
        System.out.println("Error exportando XML: " + ex.getMessage());
        return false;
    }
}

}
