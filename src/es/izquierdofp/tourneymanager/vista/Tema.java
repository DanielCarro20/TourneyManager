package es.izquierdofp.tourneymanager.vista;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class Tema {

    private static boolean oscuro = false;

    public static void iniciar() {
        FlatLightLaf.setup();
        oscuro = false;
    }

    public static void cambiar(boolean oscuro) {
        Tema.oscuro = oscuro;
        if (oscuro) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        FlatLaf.updateUI();
    }

    public static void alternar() {
        cambiar(!oscuro);
    }

    public static boolean isOscuro() {
        return oscuro;
    }
}