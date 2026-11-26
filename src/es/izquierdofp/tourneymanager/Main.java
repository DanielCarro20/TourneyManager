package es.izquierdofp.tourneymanager;

import es.izquierdofp.tourneymanager.datos.DatabaseManager;
import es.izquierdofp.tourneymanager.vista.VentanaPrincipal;

public class Main {

    public static void main(String[] args) {
        DatabaseManager.inicializar();
        com.formdev.flatlaf.FlatLightLaf.setup();
        java.awt.EventQueue.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}