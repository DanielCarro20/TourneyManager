package es.izquierdofp.tourneymanager.vista;
 
import javax.swing.JTable;
 
public interface PanelDatos {
 
    void refrescar();
 
    void nuevo();
 
    void editar();
 
    void eliminar();
 
    JTable getTabla();
 
    void setAlCambiar(Runnable alCambiar);
}
