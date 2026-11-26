package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Juego;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;

public class JuegoDAO {

    private Juego mapear(ResultSet rs) throws SQLException {
        Juego j = new Juego();
        j.setId(rs.getInt("id"));
        j.setNombre(rs.getString("nombre"));
        return j;
    }

    public List<Juego> listar() {
        List<Juego> lista = new ArrayList<>();
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM juego ORDER BY id");
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
        public Juego obtener(int id) {
        Juego j = null;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM juego WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                j = mapear(rs);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return j;
    }
}