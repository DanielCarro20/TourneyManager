package es.izquierdofp.tourneymanager.vista;

import es.izquierdofp.tourneymanager.datos.RolDAO;
import es.izquierdofp.tourneymanager.modelo.Jugador;
import es.izquierdofp.tourneymanager.modelo.Rol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

public class JugadorTableModel extends AbstractTableModel {

    private final String[] columnas = {"Nick", "Nombre", "Rol", "Edad"};
    private List<Jugador> jugadores = new ArrayList<>();
    private Map<Integer, String> roles = new HashMap<>();

    public void cargar(List<Jugador> lista) {
        roles.clear();
        for (Rol r : new RolDAO().listar()) {
            roles.put(r.getId(), r.getNombre());
        }
        jugadores = lista;
        fireTableDataChanged();
    }

    public Jugador getJugador(int fila) {
        return jugadores.get(fila);
    }

    @Override
    public int getRowCount() {
        return jugadores.size();
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
            return Integer.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Jugador j = jugadores.get(fila);
        switch (columna) {
            case 0:
                return j.getNick();
            case 1:
                return j.getNombre();
            case 2:
                return roles.get(j.getRolId());
            default:
                return j.getEdad();
        }
    }
}