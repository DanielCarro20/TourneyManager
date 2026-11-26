package es.izquierdofp.tourneymanager.vista;
 
import es.izquierdofp.tourneymanager.datos.DatabaseManager;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
 
public class PruebaVentana1 {
 
    public static void main(String[] args) {
        DatabaseManager.inicializar();
        SwingUtilities.invokeLater(() -> {
            Tema.iniciar();
            JFrame ventana = new JFrame("Prueba - Torneos");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.add(new PanelTorneos());
            ventana.setSize(800, 450);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
