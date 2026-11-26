package es.izquierdofp.tourneymanager.vista;
 
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
 
public class TorneoRenderer extends DefaultTableCellRenderer {
 
    private static final Color GRIS = new Color(128, 128, 128);
    private static final Color NARANJA = new Color(230, 126, 0);
    private static final Color VERDE = new Color(0, 150, 60);
 
    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada, boolean foco, int fila, int columna) {
        Component c = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
        if (valor instanceof Double) {
            setText(String.format("%.2f", (Double) valor));
        }
        if (!seleccionada) {
            TorneoTableModel modelo = (TorneoTableModel) tabla.getModel();
            String fecha = modelo.getTorneo(tabla.convertRowIndexToModel(fila)).getFecha();
            LocalDate dia = LocalDate.parse(fecha);
            LocalDate hoy = LocalDate.now();
            if (dia.isBefore(hoy)) {
                c.setForeground(GRIS);
            } else if (!dia.isAfter(hoy.plusDays(7))) {
                c.setForeground(NARANJA);
            } else {
                c.setForeground(VERDE);
            }
        }
        return c;
    }
}
