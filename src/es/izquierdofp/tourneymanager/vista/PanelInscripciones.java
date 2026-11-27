package es.izquierdofp.tourneymanager.vista;
 
import es.izquierdofp.tourneymanager.datos.InscripcionDAO;
import es.izquierdofp.tourneymanager.modelo.Inscripcion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;
 
public class PanelInscripciones extends JPanel implements PanelDatos {
 
    private final InscripcionDAO dao = new InscripcionDAO();
    private final InscripcionTableModel modelo = new InscripcionTableModel();
    private final JTable tabla = new JTable(modelo);
    private final TableRowSorter<InscripcionTableModel> ordenador = new TableRowSorter<>(modelo);
    private Runnable alCambiar = () -> {
    };
 
    public PanelInscripciones() {
        super(new BorderLayout(5, 5));
 
        tabla.setRowSorter(ordenador);
        InscripcionRenderer renderer = new InscripcionRenderer();
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
 
        JButton btnNuevo = new JButton("Inscribir equipo");
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
 
    private Inscripcion seleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una inscripción de la tabla.");
            return null;
        }
        return modelo.getInscripcion(tabla.convertRowIndexToModel(fila));
    }
 
    @Override
    public void refrescar() {
        modelo.cargar(dao.listar());
    }
 
    @Override
    public void nuevo() {
        InscripcionDialog dialogo = new InscripcionDialog(SwingUtilities.getWindowAncestor(this), null);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            refrescar();
            alCambiar.run();
        }
    }
 
    @Override
    public void editar() {
        Inscripcion i = seleccionada();
        if (i == null) {
            return;
        }
        InscripcionDialog dialogo = new InscripcionDialog(SwingUtilities.getWindowAncestor(this), i);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            refrescar();
            alCambiar.run();
        }
    }
 
    @Override
    public void eliminar() {
        Inscripcion i = seleccionada();
        if (i == null) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar esta inscripción?",
                "Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            if (dao.eliminar(i.getId())) {
                refrescar();
                alCambiar.run();
            } else {
                JOptionPane.showMessageDialog(this, "No se ha podido eliminar la inscripción.");
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
