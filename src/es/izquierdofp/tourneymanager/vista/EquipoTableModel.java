package es.izquierdofp.tourneymanager.vista;

import es.izquierdofp.tourneymanager.modelo.Equipo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class EquipoTableModel extends AbstractTableModel {

    private final String[] columnas = {"Nombre", "Ciudad", "Fundación"};
    private List<Equipo> equipos = new ArrayList<>();

    public void cargar(List<Equipo> lista) {
        equipos = lista;
        fireTableDataChanged();
    }

    public Equipo getEquipo(int fila) {
        return equipos.get(fila);
    }

    @Override
    public int getRowCount() {
        return equipos.size();
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
        Equipo e = equipos.get(fila);
        switch (columna) {
            case 0:
                return e.getNombre();
            case 1:
                return e.getCiudad();
            default:
                return e.getFundacion();
        }
    }
}