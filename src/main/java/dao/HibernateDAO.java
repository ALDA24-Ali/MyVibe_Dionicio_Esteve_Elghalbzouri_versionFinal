package dao; // Paquete donde se encuentra la clase DAO


import model.EntradaDiario; // Importa la entidad EntradaDiario
import org.hibernate.HibernateException; // Excepciones propias de Hibernate
import org.hibernate.Session; // Maneja sesiones con la base de datos
import org.hibernate.SessionFactory; // Fábrica de sesiones
import org.hibernate.Transaction; // Manejo de transacciones
import org.hibernate.cfg.Configuration; // Configuración de Hibernate


import java.io.InputStream; // Para leer archivos de configuración
import java.util.List; // Manejo de listas
import java.util.Properties; // Manejo de propiedades


public class HibernateDAO implements IDiarioDAO { // DAO que usa Hibernate

    // SessionFactory única para toda la aplicación
    private static final SessionFactory sessionFactory = buildSessionFactory();
   
    // Construye y configura la SessionFactory
    private static SessionFactory buildSessionFactory() {
        try {
            // Crear objeto para cargar propiedades de la base de datos
            Properties dbProps = new Properties();
            // Intentar cargar el archivo db.properties desde el classpath
            try (InputStream in = HibernateDAO.class.getResourceAsStream("/db.properties")) {
                if (in != null) {
                    // Cargar propiedades si el archivo existe
                    dbProps.load(in);
                } else {
                    // Aviso si no se encuentra el archivo
                    System.out.println("db.properties no encontrado en classpath; usando valores por defecto.");
                }
            } catch (Exception e) {
                // Error al leer el archivo de propiedades
                System.out.println("Error leyendo db.properties: " + e.getMessage());
            }


            // Crear la configuración de Hibernate
            Configuration cfg = new Configuration();


            // Obtener la URL de conexión o usar valor por defecto
            String url = dbProps.getProperty(
                    "db.url",
                    "jdbc:mysql://localhost:3306/myvibe?useSSL=false&serverTimezone=UTC"
            );
            // Obtener usuario de la BD o usar root
            String user = dbProps.getProperty("db.user", "root");
            // Obtener contraseña o dejarla vacía
            String pass = dbProps.getProperty("db.password", "");


            // Propiedades específicas de Hibernate
            Properties hibProps = new Properties();
            // Driver JDBC de MySQL
            hibProps.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
            // URL de conexión
            hibProps.put("hibernate.connection.url", url);
            // Usuario de la base de datos
            hibProps.put("hibernate.connection.username", user);
            // Contraseña de la base de datos
            hibProps.put("hibernate.connection.password", pass);


            // Dialecto de MySQL 8
            hibProps.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
            // No mostrar SQL en consola
            hibProps.put("hibernate.show_sql", "false");
            // Formatear SQL si se muestra
            hibProps.put("hibernate.format_sql", "true");


            // No crear ni modificar tablas, solo validar el esquema
            hibProps.put("hibernate.hbm2ddl.auto", "validate");


            // Asignar las propiedades a la configuración
            cfg.setProperties(hibProps);


            // Registrar la clase EntradaDiario como entidad
            cfg.addAnnotatedClass(EntradaDiario.class);


            // Crear y devolver la SessionFactory
            return cfg.buildSessionFactory();


        } catch (HibernateException ex) {
            // Error grave al crear la SessionFactory
            System.err.println("Error creando SessionFactory: " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    //           CRUD HIBERNATE
    @Override
    public boolean insert(EntradaDiario e) {
        Transaction tx = null; // Transacción de Hibernate
        try (Session session = sessionFactory.openSession()) {
            // Abrir sesión y comenzar transacción
            tx = session.beginTransaction();
            // Insertar la entidad en la base de datos
            session.persist(e);
            // Confirmar los cambios
            tx.commit();
            // Indicar que la operación fue correcta
            return true;
        } catch (Exception ex) {
            // Revertir cambios si ocurre un error
            if (tx != null) tx.rollback();
            // Mostrar mensaje de error
            System.out.println("Hibernate insert error: " + ex.getMessage());
            // Indicar que falló la operación
            return false;
        }
    }


    @Override
    public boolean update(EntradaDiario e) {
        Transaction tx = null; // Transacción de Hibernate
        try (Session session = sessionFactory.openSession()) {
            // Abrir sesión y comenzar transacción
            tx = session.beginTransaction();
            // Actualizar la entidad (seguro para objetos detached)
            session.merge(e);
            // Confirmar cambios
            tx.commit();
            // Indicar éxito
            return true;
        } catch (Exception ex) {
            // Revertir cambios si hay error
            if (tx != null) tx.rollback();
            // Mostrar mensaje de error
            System.out.println("Hibernate update error: " + ex.getMessage());
            // Indicar fallo
            return false;
        }
    }


    @Override
    public boolean delete(int id) {
        Transaction tx = null; // Transacción de Hibernate
        try (Session session = sessionFactory.openSession()) {


            // Obtener la entrada por su id
            EntradaDiario e = session.get(EntradaDiario.class, id);
            // Si no existe, no se elimina nada
            if (e == null) return false;


            // Comenzar transacción
            tx = session.beginTransaction();
            // Eliminar la entidad
            session.remove(e);
            // Confirmar cambios
            tx.commit();


            // Indicar éxito
            return true;


        } catch (Exception ex) {
            // Revertir cambios si ocurre un error
            if (tx != null) tx.rollback();
            // Mostrar mensaje de error
            System.out.println("Hibernate delete error: " + ex.getMessage());
            // Indicar fallo
            return false;
        }
    }


    @Override
    public EntradaDiario getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            // Obtener una entrada por su id
            return session.get(EntradaDiario.class, id);
        } catch (Exception ex) {
            // Mostrar mensaje de error
            System.out.println("Hibernate getById error: " + ex.getMessage());
            // Devolver null si falla
            return null;
        }
    }


    @Override
    public List<EntradaDiario> getAll() {
        try (Session session = sessionFactory.openSession()) {
            // Obtener todas las entradas ordenadas por fecha descendente
            return session.createQuery(
                    "FROM EntradaDiario e ORDER BY e.fecha DESC",
                    EntradaDiario.class
            ).list();
        } catch (Exception ex) {
            // Mostrar mensaje de error
            System.out.println("Hibernate getAll error: " + ex.getMessage());
            // Devolver lista vacía si falla
            return List.of();
        }
    }


    // Cierra la SessionFactory al finalizar la aplicación
    public static void shutdown() {
        try {
            sessionFactory.close(); // Cerrar recursos de Hibernate
        } catch (Exception ignored) {} // Ignorar errores al cerrar
    }
}
