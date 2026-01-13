package dao; // Paquete donde se encuentra la clase DAO


import model.EntradaDiario; // Importa la entidad EntradaDiario


import java.io.*; // Clases para manejo de archivos y streams
import java.util.ArrayList; // Lista dinámica
import java.util.List; // Interfaz de listas


public class SerializacionDAO implements IDiarioDAO { // DAO que usa serialización de objetos


    private static final String FILE = "entradas.dat"; // Archivo donde se guardan las entradas

    //      MÉTODOS INTERNOS

    // Carga todas las entradas desde el archivo .dat
    private List<EntradaDiario> loadAllInternal() {
        File archivo = new File(FILE); // Representa el archivo físico


        // Si el archivo no existe, devolver lista vacía
        if (!archivo.exists()) return new ArrayList<>();


        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            // Leer lista de objetos desde el archivo
            return (List<EntradaDiario>) in.readObject();
        } catch (EOFException e) {
            // Archivo vacío → lista vacía
            return new ArrayList<>();
        } catch (Exception ex) {
            // Error al leer el archivo
            System.out.println("Error leyendo archivo .dat: " + ex.getMessage());
            return new ArrayList<>();
        }
    }


    // Guarda todas las entradas en el archivo .dat
    private boolean saveAllInternal(List<EntradaDiario> lista) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            // Escribir la lista completa en el archivo
            out.writeObject(lista);
            return true;
        } catch (Exception ex) {
            // Error al guardar el archivo
            System.out.println("Error guardando archivo .dat: " + ex.getMessage());
            return false;
        }
    }

    //            CRUD
    @Override
    public boolean insert(EntradaDiario e) {
        // Cargar lista existente
        List<EntradaDiario> lista = loadAllInternal();
        // Añadir nueva entrada
        lista.add(e);
        // Guardar lista actualizada
        return saveAllInternal(lista);
    }


    @Override
    public boolean update(EntradaDiario e) {
        // Cargar lista existente
        List<EntradaDiario> lista = loadAllInternal();


        // Buscar la entrada por id y actualizarla
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == e.getId()) {
                lista.set(i, e); // Reemplazar la entrada existente
                return saveAllInternal(lista); // Guardar cambios
            }
        }
        return false; // No se encontró la entrada
    }


    @Override
    public boolean delete(int id) {
        // Cargar lista existente
        List<EntradaDiario> lista = loadAllInternal();
        // Eliminar la entrada con el id especificado
        boolean removed = lista.removeIf(ent -> ent.getId() == id);
        // Guardar cambios si se eliminó alguna entrada
        return removed && saveAllInternal(lista);
    }


    @Override
    public EntradaDiario getById(int id) {
        // Cargar lista existente
        List<EntradaDiario> lista = loadAllInternal();
        // Buscar la entrada por id y devolverla o null si no existe
        return lista.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }


    @Override
    public List<EntradaDiario> getAll() {
        // Devolver todas las entradas
        return loadAllInternal();
    }


    //     EXTRA PARA RA4

    // Borra el archivo de datos completo
    public boolean clear() {
        try {
            File archivo = new File(FILE); // Representa el archivo físico
            if (archivo.exists()) {
                return archivo.delete(); // Borrar el archivo si existe
            }
            return true; // Si no existe, ya está "limpio"
        } catch (Exception ex) {
            // Error al borrar archivo
            System.out.println("Error al borrar archivo .dat: " + ex.getMessage());
            return false;
        }
    }

    @Override
public List<EntradaDiario> getByUserId(int userId) {
    // No implementado en serialización por ahora
    return List.of();
}
}
