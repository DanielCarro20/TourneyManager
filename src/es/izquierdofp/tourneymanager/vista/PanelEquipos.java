package es.izquierdofp.tourneymanager.vista;

import es.izquierdofp.tourneymanager.datos.EquipoDAO;
import es.izquierdofp.tourneymanager.modelo.Equipo;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

public class PanelEquipos extends JPanel implements PanelDatos {

    private final EquipoDAO dao = new EquipoDAO();
    private final EquipoTableModel modelo = new EquipoTableModel();
    private final JTable tabla = new JTable(modelo);
    private final JTextField txtBuscar = new JTextField(20);
    private Runnable alCambiar = () -> {
    };

    public PanelEquipos() {
        super(new BorderLayout(5, 5));

        tabla.setRowSorter(new TableRowSorter<>(modelo));

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Buscar:"));
        arriba.add(txtBuscar);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refrescar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refrescar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refrescar();
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

    private Equipo seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo de la tabla.");
            return null;
        }
        return modelo.getEquipo(tabla.convertRowIndexToModel(fila));
    }

    @Override
    public void refrescar() {
        modelo.cargar(dao.buscar(txtBuscar.getText().trim()));
    }

    @Override
    public void nuevo() {
        EquipoDialog dialogo = new EquipoDialog(SwingUtilities.getWindowAncestor(this), null);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            refrescar();
            alCambiar.run();
        }
    }

    @Override
    public void editar() {
        Equipo e = seleccionado();
        if (e == null) {
            return;
        }
        EquipoDialog dialogo = new EquipoDialog(SwingUtilities.getWindowAncestor(this), e);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            refrescar();
            alCambiar.run();
        }
    }

    @Override
    public void eliminar() {
        Equipo e = seleccionado();
        if (e == null) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el equipo \"" + e.getNombre() + "\"?\nSe borrarán también sus jugadores e inscripciones.",
                "Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            if (dao.eliminar(e.getId())) {
                refrescar();
                alCambiar.run();
            } else {
                JOptionPane.showMessageDialog(this, "No se ha podido eliminar el equipo.");
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