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
        
            public int insertar(Equipo e) {
        int id = -1;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO equipo (nombre, ciudad, fundacion, logo) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getCiudad());
            ps.setInt(3, e.getFundacion());
            ps.setString(4, e.getLogo());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
                e.setId(id);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            id = -1;
        }
        return id;
    }
            
                public boolean actualizar(Equipo e) {
        boolean ok = false;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE equipo SET nombre = ?, ciudad = ?, fundacion = ?, logo = ? WHERE id = ?");
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getCiudad());
            ps.setInt(3, e.getFundacion());
            ps.setString(4, e.getLogo());
            ps.setInt(5, e.getId());
            ok = ps.executeUpdate() > 0;
            ps.close();
            con.close();
        } catch (SQLException ex) {
            ok = false;
        }
        return ok;
    }
                
                    public boolean eliminar(int id) {
        boolean ok = false;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM equipo WHERE id = ?");
            ps.setInt(1, id);
            ok = ps.executeUpdate() > 0;
            ps.close();
            con.close();
        } catch (SQLException ex) {
            ok = false;
        }
        return ok;
    }
                        public int contar() {
        int total = 0;
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM equipo");
            if (rs.next()) {
                total = rs.getInt(1);
            }
            rs.close();
            st.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return total;
    }
                        
                            public List<Equipo> buscar(String texto) {
        List<Equipo> resultado = new ArrayList<>();
        if (texto == null) {
            texto = "";
        }
        String t = texto.toLowerCase();
        for (Equipo e : listar()) {
            if (e.getNombre().toLowerCase().contains(t) || e.getCiudad().toLowerCase().contains(t)) {
                resultado.add(e);
            }
        }
        return resultado;
    }
            
}