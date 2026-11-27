package es.izquierdofp.tourneymanager.vista;
 
import es.izquierdofp.tourneymanager.datos.DatabaseManager;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
 
public class PruebaVentana2 {
 
    public static void main(String[] args) {
        DatabaseManager.inicializar();
        SwingUtilities.invokeLater(() -> {
            Tema.iniciar();
            JFrame ventana = new JFrame("Prueba - Torneos e inscripciones");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            javax.swing.JTabbedPane pestanas = new javax.swing.JTabbedPane();
            pestanas.addTab("Torneos", new PanelTorneos());
            pestanas.addTab("Inscripciones", new PanelInscripciones());
            ventana.add(pestanas);
            ventana.setSize(800, 450);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
