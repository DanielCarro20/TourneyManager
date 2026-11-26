package es.izquierdofp.tourneymanager.vista;

import es.izquierdofp.tourneymanager.datos.JuegoDAO;
import es.izquierdofp.tourneymanager.datos.RolDAO;
import es.izquierdofp.tourneymanager.modelo.Juego;
import es.izquierdofp.tourneymanager.modelo.Rol;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

public class CatalogosDialog extends JDialog {

    private static class Elemento {

        final int id;
        final String nombre;

        Elemento(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private interface Acceso {

        List<Elemento> listar();

        int insertar(String nombre);

        boolean renombrar(int id, String nombre);

        boolean eliminar(int id);
    }

    private class PanelCatalogo extends JPanel {

        private final Acceso acceso;
        private final DefaultListModel<Elemento> modelo = new DefaultListModel<>();
        private final JList<Elemento> lista = new JList<>(modelo);

        PanelCatalogo(Acceso acceso) {
            super(new BorderLayout(5, 5));
            this.acceso = acceso;
            lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JButton btnAnadir = new JButton("Añadir");
            JButton btnRenombrar = new JButton("Renombrar");
            JButton btnBorrar = new JButton("Borrar");
            btnAnadir.addActionListener(e -> anadir());
            btnRenombrar.addActionListener(e -> renombrar());
            btnBorrar.addActionListener(e -> borrar());
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            botones.add(btnAnadir);
            botones.add(btnRenombrar);
            botones.add(btnBorrar);

            add(new JScrollPane(lista), BorderLayout.CENTER);
            add(botones, BorderLayout.SOUTH);
            cargar();
        }

        private void cargar() {
            modelo.clear();
            for (Elemento e : acceso.listar()) {
                modelo.addElement(e);
            }
        }

        String guardarNuevo(String nombre) {
            nombre = nombre.trim();
            if (nombre.isEmpty()) {
                return "El nombre no puede estar vacío.";
            }
            if (acceso.insertar(nombre) < 0) {
                return "No se ha podido añadir (¿ya existe ese nombre?).";
            }
            cargar();
            return null;
        }

        String guardarRenombrado(Elemento e, String nombre) {
            nombre = nombre.trim();
            if (nombre.isEmpty()) {
                return "El nombre no puede estar vacío.";
            }
            if (!acceso.renombrar(e.id, nombre)) {
                return "No se ha podido renombrar (¿ya existe ese nombre?).";
            }
            cargar();
            return null;
        }

        String borrar(Elemento e) {
            if (!acceso.eliminar(e.id)) {
                return "No se puede borrar \"" + e.nombre + "\": está en uso.";
            }
            cargar();
            return null;
        }

        private void anadir() {
            String nombre = JOptionPane.showInputDialog(this, "Nombre nuevo:");
            if (nombre == null) {
                return;
            }
            mostrarError(guardarNuevo(nombre));
        }

        private void renombrar() {
            Elemento e = lista.getSelectedValue();
            if (e == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un valor de la lista.");
                return;
            }
            String nombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", e.nombre);
            if (nombre == null) {
                return;
            }
            mostrarError(guardarRenombrado(e, nombre));
        }

        private void borrar() {
            Elemento e = lista.getSelectedValue();
            if (e == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un valor de la lista.");
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Borrar \"" + e.nombre + "\"?", "Confirmar borrado",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (respuesta == JOptionPane.YES_OPTION) {
                mostrarError(borrar(e));
            }
        }

        private void mostrarError(String mensaje) {
            if (mensaje != null) {
                JOptionPane.showMessageDialog(this, mensaje, "Catálogos", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public CatalogosDialog(Window propietario) {
        super(propietario, "Catálogos", ModalityType.APPLICATION_MODAL);

        JuegoDAO juegoDao = new JuegoDAO();
        RolDAO rolDao = new RolDAO();

        PanelCatalogo juegos = new PanelCatalogo(new Acceso() {
            @Override
            public List<Elemento> listar() {
                List<Elemento> lista = new ArrayList<>();
                for (Juego j : juegoDao.listar()) {
                    lista.add(new Elemento(j.getId(), j.getNombre()));
                }
                return lista;
            }

            @Override
            public int insertar(String nombre) {
                Juego j = new Juego();
                j.setNombre(nombre);
                return juegoDao.insertar(j);
            }

            @Override
            public boolean renombrar(int id, String nombre) {
                Juego j = new Juego();
                j.setId(id);
                j.setNombre(nombre);
                return juegoDao.actualizar(j);
            }

            @Override
            public boolean eliminar(int id) {
                return juegoDao.eliminar(id);
            }
        });

        PanelCatalogo roles = new PanelCatalogo(new Acceso() {
            @Override
            public List<Elemento> listar() {
                List<Elemento> lista = new ArrayList<>();
                for (Rol r : rolDao.listar()) {
                    lista.add(new Elemento(r.getId(), r.getNombre()));
                }
                return lista;
            }

            @Override
            public int insertar(String nombre) {
                Rol r = new Rol();
                r.setNombre(nombre);
                return rolDao.insertar(r);
            }

            @Override
            public boolean renombrar(int id, String nombre) {
                Rol r = new Rol();
                r.setId(id);
                r.setNombre(nombre);
                return rolDao.actualizar(r);
            }

            @Override
            public boolean eliminar(int id) {
                return rolDao.eliminar(id);
            }
        });

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Juegos", juegos);
        pestanas.addTab("Roles", roles);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        abajo.add(btnCerrar);

        add(pestanas, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
        setSize(420, 320);
        setLocationRelativeTo(propietario);
    }
}