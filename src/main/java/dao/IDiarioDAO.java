package dao;

import model.EntradaDiario;//IMPORTAMOS LA CLASE ENTRADADIARIO del model.
import java.util.List;


//interfaz DAO que define las operaciones de acceso a datos para la entidad EntradaDiario.
//actua como contrato que deben implementar las distintas estrategias de persistencia.
public interface IDiarioDAO {
    // Inserta una nueva entrada de diario
    boolean insert(EntradaDiario e);
    // Actualiza una entrada existente
    boolean update(EntradaDiario e);
    // Elimina una entrada por su identificador
    boolean delete(int id);
    // Obtiene una entrada concreta por ID
    EntradaDiario getById(int id);
    // Obtiene todas las entradas de diario
    List<EntradaDiario> getAll();
    List<EntradaDiario> getByUserId(int userId);
    
}