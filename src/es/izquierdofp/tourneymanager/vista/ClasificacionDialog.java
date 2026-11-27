package es.izquierdofp.tourneymanager.vista;
 
import es.izquierdofp.tourneymanager.datos.EquipoDAO;
import es.izquierdofp.tourneymanager.datos.EstadisticasDAO;
import es.izquierdofp.tourneymanager.datos.InscripcionDAO;
import es.izquierdofp.tourneymanager.datos.TorneoDAO;
import es.izquierdofp.tourneymanager.modelo.Equipo;
import es.izquierdofp.tourneymanager.modelo.Inscripcion;
import es.izquierdofp.tourneymanager.modelo.Torneo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
 
public class ClasificacionDialog extends JDialog {
 
    private final JComboBox<Torneo> cmbTorneo = new JComboBox<>();
    private final DefaultTableModel modeloTorneo = new DefaultTableModel(new String[]{"Posición", "Equipo", "Medalla"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final DefaultTableModel modeloRanking = new DefaultTableModel(new String[]{"Puesto", "Equipo", "Puntos"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JLabel lblEstado = new JLabel(" ");
 
    public ClasificacionDialog(Window propietario) {
        super(propietario, "Clasificación", ModalityType.APPLICATION_MODAL);
 
        for (Torneo t : new TorneoDAO().listar()) {
            cmbTorneo.addItem(t);
        }
        cmbTorneo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> lista, Object valor, int indice, boolean sel, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, sel, foco);
                if (valor instanceof Torneo) {
                    setText(((Torneo) valor).getNombre());
                }
                return this;
            }
        });
        cmbTorneo.addActionListener(e -> cargarTorneo());
 
        JTable tablaTorneo = new JTable(modeloTorneo);
        JTable tablaRanking = new JTable(modeloRanking);
        MedallaRenderer renderer = new MedallaRenderer();
        for (int i = 0; i < 3; i++) {
            tablaTorneo.getColumnModel().getColumn(i).setCellRenderer(renderer);
            tablaRanking.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
 
        JPanel panelTorneo = new JPanel(new BorderLayout(5, 5));
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Torneo:"));
        arriba.add(cmbTorneo);
        panelTorneo.add(arriba, BorderLayout.NORTH);
        panelTorneo.add(new JScrollPane(tablaTorneo), BorderLayout.CENTER);
 
        JPanel panelRanking = new JPanel(new BorderLayout(5, 5));
        panelRanking.add(new JScrollPane(tablaRanking), BorderLayout.CENTER);
        panelRanking.add(lblEstado, BorderLayout.SOUTH);
 
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Clasificación del torneo", panelTorneo);
        pestanas.addTab("Ranking de la temporada", panelRanking);
 
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        abajo.add(btnCerrar);
 
        add(pestanas, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
 
        cargarTorneo();
        cargarRanking();
 
        setPreferredSize(new Dimension(520, 400));
        pack();
        setLocationRelativeTo(propietario);
    }
 
    private void cargarTorneo() {
        modeloTorneo.setRowCount(0);
        Torneo torneo = (Torneo) cmbTorneo.getSelectedItem();
        if (torneo == null) {
            return;
        }
        Map<Integer, String> equipos = new HashMap<>();
        for (Equipo e : new EquipoDAO().listar()) {
            equipos.put(e.getId(), e.getNombre());
        }
        for (Inscripcion i : new InscripcionDAO().listarPorTorneo(torneo.getId())) {
            String medalla = "";
            if (i.getPosicion() != null) {
                if (i.getPosicion() == 1) {
                    medalla = "Oro";
                } else if (i.getPosicion() == 2) {
                    medalla = "Plata";
                } else if (i.getPosicion() == 3) {
                    medalla = "Bronce";
                }
            }
            modeloTorneo.addRow(new Object[]{
                i.getPosicion() == null ? "Sin jugar" : i.getPosicion().toString(),
                equipos.get(i.getEquipoId()),
                medalla});
        }
    }
 
    private void cargarRanking() {
        lblEstado.setText("Calculando ranking...");
        SwingWorker<Map<String, Integer>, Void> trabajador = new SwingWorker<Map<String, Integer>, Void>() {
            @Override
            protected Map<String, Integer> doInBackground() {
                return new EstadisticasDAO().ranking();
            }
 
            @Override
            protected void done() {
                try {
                    int puesto = 1;
                    for (Map.Entry<String, Integer> fila : get().entrySet()) {
                        modeloRanking.addRow(new Object[]{String.valueOf(puesto), fila.getKey(), fila.getValue()});
                        puesto++;
                    }
                    lblEstado.setText("Puntos: 1.º = 10, 2.º = 6, 3.º = 4, resto con posición = 1");
                } catch (Exception ex) {
                    lblEstado.setText("No se ha podido calcular el ranking.");
                }
            }
        };
        trabajador.execute();
    }
 
    private static class MedallaRenderer extends DefaultTableCellRenderer {
 
        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada, boolean foco, int fila, int columna) {
            Component c = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            if (!seleccionada) {
                Object primera = tabla.getValueAt(fila, 0);
                c.setForeground(Color.BLACK);
                if ("1".equals(primera)) {
                    c.setBackground(new Color(255, 215, 0));
                } else if ("2".equals(primera)) {
                    c.setBackground(new Color(192, 192, 192));
                } else if ("3".equals(primera)) {
                    c.setBackground(new Color(205, 127, 50));
                } else {
                    c.setBackground(tabla.getBackground());
                    c.setForeground(tabla.getForeground());
                }
            }
            return c;
        }
    }
}
