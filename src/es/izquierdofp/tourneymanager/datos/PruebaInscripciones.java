package es.izquierdofp.tourneymanager.datos;
 
import es.izquierdofp.tourneymanager.modelo.Inscripcion;

public class PruebaInscripciones {
 
    public static void main(String[] args) {
        DatabaseManager.inicializar();
        InscripcionDAO dao = new InscripcionDAO();
        System.out.println("Inscripciones: " + dao.contar());
        System.out.println("Del torneo 1: " + dao.listarPorTorneo(1).size());
        Inscripcion repetida = new Inscripcion();
        repetida.setEquipoId(8);
        repetida.setTorneoId(2);
        repetida.setPosicion(1);
        System.out.println("Posicion 1 repetida en torneo 2 (esperado -1): " + dao.insertar(repetida));
        Inscripcion libre = new Inscripcion();
        libre.setEquipoId(8);
        libre.setTorneoId(2);
        libre.setPosicion(null);
        int id = dao.insertar(libre);
        System.out.println("Sin posicion (esperado un id mayor que 0): " + id);
        System.out.println("Eliminada: " + dao.eliminar(id));
    }
}
