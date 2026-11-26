package es.izquierdofp.tourneymanager.datos;
 
import es.izquierdofp.tourneymanager.modelo.Torneo;
 
public class PruebaTorneos {
 
    public static void main(String[] args) {
        DatabaseManager.inicializar();
        TorneoDAO dao = new TorneoDAO();
        System.out.println("Torneos: " + dao.contar());
        System.out.println("Buscar copa: " + dao.buscar("copa").size());
        Torneo t = new Torneo();
        t.setNombre("Torneo de prueba");
        t.setJuegoId(1);
        t.setFecha("2027-01-01");
        t.setPremio(100);
        t.setOnline(true);
        int id = dao.insertar(t);
        System.out.println("Insertado con id: " + id);
        t.setId(id);
        t.setPremio(250);
        System.out.println("Actualizado: " + dao.actualizar(t));
        System.out.println("Premio ahora: " + dao.obtener(id).getPremio());
        System.out.println("Eliminado: " + dao.eliminar(id));
    }
}
