package es.izquierdofp.tourneymanager.vista;
 
import es.izquierdofp.tourneymanager.datos.TorneoDAO;
import es.izquierdofp.tourneymanager.modelo.Torneo;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
 
public class PanelTorneos extends JPanel implements PanelDatos {
 
    private final TorneoDAO dao = new TorneoDAO();
    private final TorneoTableModel modelo = new TorneoTableModel();
    private final JTable tabla = new JTable(modelo);
    private final TableRowSorter<TorneoTableModel> ordenador = new TableRowSorter<>(modelo);
    private final JTextField txtBuscar = new JTextField(20);
    private Runnable alCambiar = () -> {
    };
 
    public PanelTorneos() {
        super(new BorderLayout(5, 5));
 
        tabla.setRowSorter(ordenador);
        TorneoRenderer renderer = new TorneoRenderer();
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
 
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Buscar:"));
        arriba.add(txtBuscar);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }
 
            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }
 
            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
 
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        abajo.add(btnNuevo);
        abajo.add(btnEditar);
        abajo.add(btnEliminar);
 
        JPopupMenu menu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("Editar");
        JMenuItem itemEliminar = new JMenuItem("Eliminar");
        itemEditar.addActionListener(e -> editar());
        itemEliminar.addActionListener(e -> eliminar());
        menu.add(itemEditar);
        menu.add(itemEliminar);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mostrarMenu(e, menu);
            }
 
            @Override
            public void mouseReleased(MouseEvent e) {
                mostrarMenu(e, menu);
            }
        });
 
        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
 
        refrescar();
    }
 
    private void mostrarMenu(MouseEvent e, JPopupMenu menu) {
        if (e.isPopupTrigger()) {
            int fila = tabla.rowAtPoint(e.getPoint());
            if (fila >= 0) {
                tabla.setRowSelectionInterval(fila, fila);
                menu.show(tabla, e.getX(), e.getY());
            }
        }
    }
 
    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
        } else {
            ordenador.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
        }
    }
 
    private Torneo seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un torneo de la tabla.");
            return null;
        }
        return modelo.getTorneo(tabla.convertRowIndexToModel(fila));
    }
 
    @Override
    public void refrescar() {
        modelo.cargar(dao.listar());
    }
 
    @Override
    public void nuevo() {
        TorneoDialog dialogo = new TorneoDialog(SwingUtilities.getWindowAncestor(this), null);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            refrescar();
            alCambiar.run();
        }
    }
 
    @Override
    public void editar() {
        Torneo t = seleccionado();
        if (t == null) {
            return;
        }
        TorneoDialog dialogo = new TorneoDialog(SwingUtilities.getWindowAncestor(this), t);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            refrescar();
            alCambiar.run();
        }
    }
 
    @Override
    public void eliminar() {
        Torneo t = seleccionado();
        if (t == null) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el torneo \"" + t.getNombre() + "\"?\nSe borrarán también sus inscripciones.",
                "Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            if (dao.eliminar(t.getId())) {
                refrescar();
                alCambiar.run();
            } else {
                JOptionPane.showMessageDialog(this, "No se ha podido eliminar el torneo.");
            }
        }
    }
 
    @Override
    public JTable getTabla() {
        return tabla;
    }
 
    @Override
    public void setAlCambiar(Runnable alCambiar) {
        this.alCambiar = alCambiar;
    }
}
