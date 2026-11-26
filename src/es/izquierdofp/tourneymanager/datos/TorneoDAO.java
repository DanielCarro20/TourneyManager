package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Torneo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TorneoDAO {

    private Torneo mapear(ResultSet rs) throws SQLException {
        Torneo t = new Torneo();
        t.setId(rs.getInt("id"));
        t.setNombre(rs.getString("nombre"));
        t.setJuegoId(rs.getInt("juego_id"));
        t.setFecha(rs.getString("fecha"));
        t.setPremio(rs.getDouble("premio"));
        t.setOnline(rs.getInt("online") == 1);
        return t;
    }

    public List<Torneo> listar() {
        List<Torneo> lista = new ArrayList<>();
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM torneo ORDER BY fecha");
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Torneo obtener(int id) {
        Torneo t = null;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM torneo WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                t = mapear(rs);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }
        public List<Torneo> buscar(String texto) {
        List<Torneo> resultado = new ArrayList<>();
        if (texto == null) {
            texto = "";
        }
        String t = texto.toLowerCase();
        for (Torneo torneo : listar()) {
            if (torneo.getNombre().toLowerCase().contains(t)) {
                resultado.add(torneo);
            }
        }
        return resultado;
    }
 
    public int insertar(Torneo t) {
        int id = -1;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO torneo (nombre, juego_id, fecha, premio, online) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, t.getNombre());
            ps.setInt(2, t.getJuegoId());
            ps.setString(3, t.getFecha());
            ps.setDouble(4, t.getPremio());
            ps.setInt(5, t.isOnline() ? 1 : 0);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
                t.setId(id);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            id = -1;
        }
        return id;
    }
 
    public boolean actualizar(Torneo t) {
        boolean ok = false;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE torneo SET nombre = ?, juego_id = ?, fecha = ?, premio = ?, online = ? WHERE id = ?");
            ps.setString(1, t.getNombre());
            ps.setInt(2, t.getJuegoId());
            ps.setString(3, t.getFecha());
            ps.setDouble(4, t.getPremio());
            ps.setInt(5, t.isOnline() ? 1 : 0);
            ps.setInt(6, t.getId());
            ok = ps.executeUpdate() > 0;
            ps.close();
            con.close();
        } catch (SQLException e) {
            ok = false;
        }
        return ok;
    }
 
    public boolean eliminar(int id) {
        boolean ok = false;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM torneo WHERE id = ?");
            ps.setInt(1, id);
            ok = ps.executeUpdate() > 0;
            ps.close();
            con.close();
        } catch (SQLException e) {
            ok = false;
        }
        return ok;
    }
 
    public int contar() {
        int total = 0;
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM torneo");
            if (rs.next()) {
                total = rs.getInt(1);
            }
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
