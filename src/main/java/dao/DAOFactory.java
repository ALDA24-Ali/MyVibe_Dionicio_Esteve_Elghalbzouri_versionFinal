package dao;

public class DAOFactory {
//DAOfactory es una clase que elige cual DAO usar.
    public enum Tipo {//se utiliza enum para enumerar SOLO esos DAO's, y lo llamamos Tipo
        JDBC,
        HIBERNATE,
        SERIALIZACION
    }

    public static IDiarioDAO getDAO(Tipo tipo) {// Método que devuelve el DAO según el tipo indicado
        return switch (tipo) {// Selecciona la implementación concreta del DAO
            //son en total 3 tipos:
            case JDBC -> new JDBCTransactionDAO();
            case HIBERNATE -> new HibernateDAO();
            case SERIALIZACION -> new SerializacionDAO();
        };
    }
}
