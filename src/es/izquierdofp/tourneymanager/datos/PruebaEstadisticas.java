package es.izquierdofp.tourneymanager.datos;
 
public class PruebaEstadisticas {
 
    public static void main(String[] args) {
        DatabaseManager.inicializar();
        EstadisticasDAO dao = new EstadisticasDAO();
        System.out.println("Equipos en el torneo 1: " + dao.equiposInscritos(1));
        System.out.println("Premio total: " + dao.premioTotal());
        System.out.println("Ranking: " + dao.ranking());
    }
}
