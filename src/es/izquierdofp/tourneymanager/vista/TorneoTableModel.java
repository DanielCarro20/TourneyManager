package es.izquierdofp.tourneymanager.vista;
 
import es.izquierdofp.tourneymanager.datos.JuegoDAO;
import es.izquierdofp.tourneymanager.modelo.Juego;
import es.izquierdofp.tourneymanager.modelo.Torneo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
 
public class TorneoTableModel extends AbstractTableModel {
 
    private final String[] columnas = {"Nombre", "Juego", "Fecha", "Premio (€)", "Online"};
    private List<Torneo> torneos = new ArrayList<>();
    private Map<Integer, String> juegos = new HashMap<>();
 
    public void cargar(List<Torneo> lista) {
        juegos.clear();
        for (Juego j : new JuegoDAO().listar()) {
            juegos.put(j.getId(), j.getNombre());
        }
        torneos = lista;
        fireTableDataChanged();
    }
 
    public Torneo getTorneo(int fila) {
        return torneos.get(fila);
    }
 
    @Override
    public int getRowCount() {
        return torneos.size();
    }
 
    @Override
    public int getColumnCount() {
        return columnas.length;
    }
 
    @Override
    public String getColumnName(int columna) {
        return columnas[columna];
    }
 
    @Override
    public Class<?> getColumnClass(int columna) {
        if (columna == 3) {
            return Double.class;
        }
        return String.class;
    }
 
    @Override
    public Object getValueAt(int fila, int columna) {
        Torneo t = torneos.get(fila);
        switch (columna) {
            case 0:
                return t.getNombre();
            case 1:
                return juegos.get(t.getJuegoId());
            case 2:
                return t.getFecha();
            case 3:
                return t.getPremio();
            default:
                return t.isOnline() ? "Sí" : "No";
        }
    }
}
