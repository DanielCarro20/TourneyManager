package es.izquierdofp.tourneymanager.vista;
 
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
 
public class InscripcionRenderer extends DefaultTableCellRenderer {
 
    private static final Color ORO = new Color(255, 215, 0);
    private static final Color PLATA = new Color(192, 192, 192);
    private static final Color BRONCE = new Color(205, 127, 50);
 
    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada, boolean foco, int fila, int columna) {
        Component c = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
        if (columna == 2 || tabla.convertColumnIndexToModel(columna) == 2) {
            setText(valor == null ? "Sin jugar" : valor.toString());
        }
        if (!seleccionada) {
            InscripcionTableModel modelo = (InscripcionTableModel) tabla.getModel();
            Integer posicion = modelo.getInscripcion(tabla.convertRowIndexToModel(fila)).getPosicion();
            c.setForeground(Color.BLACK);
            if (posicion != null && posicion == 1) {
                c.setBackground(ORO);
            } else if (posicion != null && posicion == 2) {
                c.setBackground(PLATA);
            } else if (posicion != null && posicion == 3) {
                c.setBackground(BRONCE);
            } else {
                c.setBackground(tabla.getBackground());
                c.setForeground(tabla.getForeground());
            }
        }
        return c;
    }
}