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
}