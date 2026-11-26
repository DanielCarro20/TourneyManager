package es.izquierdofp.tourneymanager.vista;

import es.izquierdofp.tourneymanager.datos.EquipoDAO;
import es.izquierdofp.tourneymanager.modelo.Equipo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class EquipoDialog extends JDialog {

    private final Equipo equipo;
    private boolean guardado = false;

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtCiudad = new JTextField(20);
    private final JSpinner spFundacion = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 1990, 2100, 1));
    private final JLabel lblError = new JLabel(" ");

    public EquipoDialog(Window propietario, Equipo equipo) {
        super(propietario, equipo == null ? "Nuevo equipo" : "Editar equipo", ModalityType.APPLICATION_MODAL);
        this.equipo = equipo;

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        anadirFila(formulario, gbc, 0, "Nombre:", txtNombre);
        anadirFila(formulario, gbc, 1, "Ciudad:", txtCiudad);
        anadirFila(formulario, gbc, 2, "Año de fundación:", spFundacion);

        lblError.setForeground(new Color(200, 40, 40));
        gbc.gridx = 0;
        gbc.gridy = 3;
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

        if (equipo != null) {
            txtNombre.setText(equipo.getNombre());
            txtCiudad.setText(equipo.getCiudad());
            spFundacion.setValue(equipo.getFundacion());
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
        String ciudad = txtCiudad.getText().trim();
        if (ciudad.isEmpty()) {
            error("La ciudad es obligatoria.");
            return;
        }
        int fundacion = (Integer) spFundacion.getValue();
        if (fundacion < 1990 || fundacion > 2100) {
            error("El año de fundación debe estar entre 1990 y 2100.");
            return;
        }

        Equipo e = (equipo == null) ? new Equipo() : equipo;
        e.setNombre(nombre);
        e.setCiudad(ciudad);
        e.setFundacion(fundacion);

        EquipoDAO dao = new EquipoDAO();
        boolean ok = (equipo == null) ? dao.insertar(e) > 0 : dao.actualizar(e);
        if (!ok) {
            error("No se ha podido guardar (¿ya existe un equipo con ese nombre?).");
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