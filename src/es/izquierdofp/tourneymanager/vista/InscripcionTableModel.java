package es.izquierdofp.tourneymanager.vista;
 
import es.izquierdofp.tourneymanager.datos.EquipoDAO;
import es.izquierdofp.tourneymanager.datos.TorneoDAO;
import es.izquierdofp.tourneymanager.modelo.Equipo;
import es.izquierdofp.tourneymanager.modelo.Inscripcion;
import es.izquierdofp.tourneymanager.modelo.Torneo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
 
public class InscripcionTableModel extends AbstractTableModel {
 
    private final String[] columnas = {"Torneo", "Equipo", "Posición"};
    private List<Inscripcion> inscripciones = new ArrayList<>();
    private Map<Integer, String> torneos = new HashMap<>();
    private Map<Integer, String> equipos = new HashMap<>();
 
    public void cargar(List<Inscripcion> lista) {
        torneos.clear();
        equipos.clear();
        for (Torneo t : new TorneoDAO().listar()) {
            torneos.put(t.getId(), t.getNombre());
        }
        for (Equipo e : new EquipoDAO().listar()) {
            equipos.put(e.getId(), e.getNombre());
        }
        inscripciones = lista;
        fireTableDataChanged();
    }
 
    public Inscripcion getInscripcion(int fila) {
        return inscripciones.get(fila);
    }
 
    @Override
    public int getRowCount() {
        return inscripciones.size();
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
        if (columna == 2) {
            return Integer.class;
        }
        return String.class;
    }
 
    @Override
    public Object getValueAt(int fila, int columna) {
        Inscripcion i = inscripciones.get(fila);
        switch (columna) {
            case 0:
                return torneos.get(i.getTorneoId());
            case 1:
                return equipos.get(i.getEquipoId());
            default:
                return i.getPosicion();
        }
    }
}
