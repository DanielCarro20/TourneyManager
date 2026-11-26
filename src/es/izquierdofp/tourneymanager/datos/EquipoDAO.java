package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Equipo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;



public class EquipoDAO {

    private Equipo mapear(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setCiudad(rs.getString("ciudad"));
        e.setFundacion(rs.getInt("fundacion"));
        e.setLogo(rs.getString("logo"));
        return e;
    }

    public List<Equipo> listar() {
        List<Equipo> lista = new ArrayList<>();
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM equipo ORDER BY nombre");
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            rs.close();
            st.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
    
        public Equipo obtener(int id) {
        Equipo e = null;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM equipo WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                e = mapear(rs);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return e;
    }
}