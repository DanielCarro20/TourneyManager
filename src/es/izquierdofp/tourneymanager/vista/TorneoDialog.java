package es.izquierdofp.tourneymanager.vista;
 
import es.izquierdofp.tourneymanager.datos.JuegoDAO;
import es.izquierdofp.tourneymanager.datos.TorneoDAO;
import es.izquierdofp.tourneymanager.modelo.Juego;
import es.izquierdofp.tourneymanager.modelo.Torneo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
 
public class TorneoDialog extends JDialog {
 
    private final Torneo torneo;
    private boolean guardado = false;
 
    private final JTextField txtNombre = new JTextField(20);
    private final JComboBox<Juego> cmbJuego = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(10);
    private final JSpinner spPremio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 50.0));
    private final JCheckBox chkOnline = new JCheckBox("Torneo online");
    private final JLabel lblError = new JLabel(" ");
 
    public TorneoDialog(Window propietario, Torneo torneo) {
        super(propietario, torneo == null ? "Nuevo torneo" : "Editar torneo", ModalityType.APPLICATION_MODAL);
        this.torneo = torneo;
 
        for (Juego j : new JuegoDAO().listar()) {
            cmbJuego.addItem(j);
        }
        cmbJuego.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> lista, Object valor, int indice, boolean sel, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, sel, foco);
                if (valor instanceof Juego) {
                    setText(((Juego) valor).getNombre());
                }
                return this;
            }
        });
 
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        anadirFila(formulario, gbc, 0, "Nombre:", txtNombre);
        anadirFila(formulario, gbc, 1, "Juego:", cmbJuego);
        anadirFila(formulario, gbc, 2, "Fecha (AAAA-MM-DD):", txtFecha);
        anadirFila(formulario, gbc, 3, "Premio (€):", spPremio);
        gbc.gridx = 1;
        gbc.gridy = 4;
        formulario.add(chkOnline, gbc);
 
        lblError.setForeground(new Color(200, 40, 40));
        gbc.gridx = 0;
        gbc.gridy = 5;
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
 
        if (torneo != null) {
            txtNombre.setText(torneo.getNombre());
            for (int i = 0; i < cmbJuego.getItemCount(); i++) {
                if (cmbJuego.getItemAt(i).getId() == torneo.getJuegoId()) {
                    cmbJuego.setSelectedIndex(i);
                }
            }
            txtFecha.setText(torneo.getFecha());
            spPremio.setValue(torneo.getPremio());
            chkOnline.setSelected(torneo.isOnline());
        } else {
            txtFecha.setText(LocalDate.now().plusDays(30).toString());
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
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            error("El nombre es obligatorio.");
            return;
        }
        if (cmbJuego.getSelectedItem() == null) {
            error("Elige un juego.");
            return;
        }
        String fecha = txtFecha.getText().trim();
        try {
            if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new DateTimeParseException("formato", fecha, 0);
            }
            LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            error("La fecha debe ser real y con formato AAAA-MM-DD.");
            return;
        }
        double premio = ((Number) spPremio.getValue()).doubleValue();
        if (premio < 0) {
            error("El premio no puede ser negativo.");
            return;
        }
 
        Torneo t = (torneo == null) ? new Torneo() : torneo;
        t.setNombre(nombre);
        t.setJuegoId(((Juego) cmbJuego.getSelectedItem()).getId());
        t.setFecha(fecha);
        t.setPremio(premio);
        t.setOnline(chkOnline.isSelected());
 
        TorneoDAO dao = new TorneoDAO();
        boolean ok = (torneo == null) ? dao.insertar(t) > 0 : dao.actualizar(t);
        if (!ok) {
            error("No se ha podido guardar el torneo.");
            return;
        }
        guardado = true;
        dispose();
    }
 
    private void error(String mensaje) {
        lblError.setText(mensaje);
        pack();
    }
 
    public boolean isGuardado() {
        return guardado;
    }
}

