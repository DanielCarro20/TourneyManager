package es.izquierdofp.tourneymanager.vista;

import es.izquierdofp.tourneymanager.datos.JugadorDAO;
import es.izquierdofp.tourneymanager.datos.RolDAO;
import es.izquierdofp.tourneymanager.modelo.Jugador;
import es.izquierdofp.tourneymanager.modelo.Rol;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class JugadorDialog extends JDialog {

    private final Jugador jugador;
    private final int equipoId;
    private boolean guardado = false;

    private final JTextField txtNick = new JTextField(20);
    private final JTextField txtNombre = new JTextField(20);
    private final JSpinner spEdad = new JSpinner(new SpinnerNumberModel(18, 14, 60, 1));
    private final JComboBox<Rol> cmbRol = new JComboBox<>();
    private final JLabel lblError = new JLabel(" ");

    public JugadorDialog(Window propietario, Jugador jugador, int equipoId) {
        super(propietario, jugador == null ? "Nuevo jugador" : "Editar jugador", ModalityType.APPLICATION_MODAL);
        this.jugador = jugador;
        this.equipoId = equipoId;

        for (Rol r : new RolDAO().listar()) {
            cmbRol.addItem(r);
        }
        cmbRol.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> lista, Object valor, int indice, boolean sel, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, sel, foco);
                if (valor instanceof Rol) {
                    setText(((Rol) valor).getNombre());
                }
                return this;
            }
        });

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        anadirFila(formulario, gbc, 0, "Nick:", txtNick);
        anadirFila(formulario, gbc, 1, "Nombre:", txtNombre);
        anadirFila(formulario, gbc, 2, "Edad:", spEdad);
        anadirFila(formulario, gbc, 3, "Rol:", cmbRol);

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

        if (jugador != null) {
            txtNick.setText(jugador.getNick());
            txtNombre.setText(jugador.getNombre());
            spEdad.setValue(jugador.getEdad());
            for (int i = 0; i < cmbRol.getItemCount(); i++) {
                if (cmbRol.getItemAt(i).getId() == jugador.getRolId()) {
                    cmbRol.setSelectedIndex(i);
                }
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
        String nick = txtNick.getText().trim();
        if (nick.isEmpty()) {
            error("El nick es obligatorio.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            error("El nombre es obligatorio.");
            return;
        }
        int edad = (Integer) spEdad.getValue();
        if (edad < 14 || edad > 60) {
            error("La edad debe estar entre 14 y 60.");
            return;
        }
        if (cmbRol.getSelectedItem() == null) {
            error("Elige un rol.");
            return;
        }

        JugadorDAO dao = new JugadorDAO();
        if (jugador == null && dao.listarPorEquipo(equipoId).size() >= 7) {
            error("El equipo ya tiene 7 jugadores: no se pueden añadir más.");
            return;
        }

        Jugador j = (jugador == null) ? new Jugador() : jugador;
        j.setEquipoId(equipoId);
        j.setNick(nick);
        j.setNombre(nombre);
        j.setEdad(edad);
        j.setRolId(((Rol) cmbRol.getSelectedItem()).getId());

        boolean ok = (jugador == null) ? dao.insertar(j) > 0 : dao.actualizar(j);
        if (!ok) {
            error("No se ha podido guardar (¿ya existe un jugador con ese nick?).");
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