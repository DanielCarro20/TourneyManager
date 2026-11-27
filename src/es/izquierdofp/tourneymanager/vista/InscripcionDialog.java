package es.izquierdofp.tourneymanager.vista;
 
import es.izquierdofp.tourneymanager.datos.EquipoDAO;
import es.izquierdofp.tourneymanager.datos.InscripcionDAO;
import es.izquierdofp.tourneymanager.datos.TorneoDAO;
import es.izquierdofp.tourneymanager.modelo.Equipo;
import es.izquierdofp.tourneymanager.modelo.Inscripcion;
import es.izquierdofp.tourneymanager.modelo.Torneo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
 
public class InscripcionDialog extends JDialog {
 
    private final Inscripcion inscripcion;
    private final InscripcionDAO dao = new InscripcionDAO();
    private boolean guardado = false;
 
    private final JComboBox<Equipo> cmbEquipo = new JComboBox<>();
    private final JComboBox<Torneo> cmbTorneo = new JComboBox<>();
    private final JSpinner spPosicion = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JCheckBox chkSinJugar = new JCheckBox("Sin jugar todavía", true);
    private final JLabel lblError = new JLabel(" ");
 
    public InscripcionDialog(Window propietario, Inscripcion inscripcion) {
        super(propietario, inscripcion == null ? "Inscribir equipo en torneo" : "Editar inscripción", ModalityType.APPLICATION_MODAL);
        this.inscripcion = inscripcion;
 
        for (Equipo e : new EquipoDAO().listar()) {
            cmbEquipo.addItem(e);
        }
        for (Torneo t : new TorneoDAO().listar()) {
            cmbTorneo.addItem(t);
        }
        cmbEquipo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> lista, Object valor, int indice, boolean sel, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, sel, foco);
                if (valor instanceof Equipo) {
                    setText(((Equipo) valor).getNombre());
                }
                return this;
            }
        });
        cmbTorneo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> lista, Object valor, int indice, boolean sel, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, sel, foco);
                if (valor instanceof Torneo) {
                    setText(((Torneo) valor).getNombre());
                }
                return this;
            }
        });
 
        chkSinJugar.addActionListener(e -> spPosicion.setEnabled(!chkSinJugar.isSelected()));
        spPosicion.setEnabled(false);
 
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        anadirFila(formulario, gbc, 0, "Equipo:", cmbEquipo);
        anadirFila(formulario, gbc, 1, "Torneo:", cmbTorneo);
        anadirFila(formulario, gbc, 2, "Posición final:", spPosicion);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formulario.add(chkSinJugar, gbc);
        lblError.setForeground(new Color(200, 40, 40));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formulario.add(lblError, gbc);
 
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnGuardar);
        botones.add(btnCancelar);
 
        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnGuardar);
 
        if (inscripcion != null) {
            for (int i = 0; i < cmbEquipo.getItemCount(); i++) {
                if (cmbEquipo.getItemAt(i).getId() == inscripcion.getEquipoId()) {
                    cmbEquipo.setSelectedIndex(i);
                }
            }
            for (int i = 0; i < cmbTorneo.getItemCount(); i++) {
                if (cmbTorneo.getItemAt(i).getId() == inscripcion.getTorneoId()) {
                    cmbTorneo.setSelectedIndex(i);
                }
            }
            if (inscripcion.getPosicion() != null) {
                chkSinJugar.setSelected(false);
                spPosicion.setEnabled(true);
                spPosicion.setValue(inscripcion.getPosicion());
            }
        }
 
        pack();
        setLocationRelativeTo(propietario);
    }
 
    private void anadirFila(JPanel panel, GridBagConstraints gbc, int fila, String texto, java.awt.Component campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        panel.add(new JLabel(texto), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }
 
    private void guardar() {
        Equipo equipo = (Equipo) cmbEquipo.getSelectedItem();
        Torneo torneo = (Torneo) cmbTorneo.getSelectedItem();
        if (equipo == null || torneo == null) {
            error("Elige un equipo y un torneo.");
            return;
        }
        Integer posicion = chkSinJugar.isSelected() ? null : (Integer) spPosicion.getValue();
 
        Inscripcion i = (inscripcion == null) ? new Inscripcion() : inscripcion;
        i.setEquipoId(equipo.getId());
        i.setTorneoId(torneo.getId());
        i.setPosicion(posicion);
 
        boolean ok = (inscripcion == null) ? dao.insertar(i) > 0 : dao.actualizar(i);
        if (!ok) {
            error(motivo(i));
            return;
        }
        guardado = true;
        dispose();
    }
 
    private String motivo(Inscripcion i) {
        int propia = (inscripcion == null) ? 0 : inscripcion.getId();
        int otras = 0;
        for (Inscripcion otra : dao.listarPorTorneo(i.getTorneoId())) {
            if (otra.getId() == propia) {
                continue;
            }
            otras++;
            if (otra.getEquipoId() == i.getEquipoId()) {
                return "Ese equipo ya está inscrito en este torneo.";
            }
            if (i.getPosicion() != null && i.getPosicion().equals(otra.getPosicion())) {
                return "Ya hay un equipo con la posición " + i.getPosicion() + " en este torneo.";
            }
        }
        if (otras >= 16) {
            return "El torneo está lleno: máximo 16 equipos.";
        }
        return "No se ha podido guardar la inscripción.";
    }
 
    private void error(String mensaje) {
        lblError.setText(mensaje);
        pack();
    }
 
    public boolean isGuardado() {
        return guardado;
    }
}
