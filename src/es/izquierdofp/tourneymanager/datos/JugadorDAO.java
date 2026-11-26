package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Jugador;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;

public class JugadorDAO {

    private Jugador mapear(ResultSet rs) throws SQLException {
        Jugador j = new Jugador();
        j.setId(rs.getInt("id"));
        j.setEquipoId(rs.getInt("equipo_id"));
        j.setRolId(rs.getInt("rol_id"));
        j.setNick(rs.getString("nick"));
        j.setNombre(rs.getString("nombre"));
        j.setEdad(rs.getInt("edad"));
        return j;
    }

    public List<Jugador> listar() {
        List<Jugador> lista = new ArrayList<>();
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM jugador ORDER BY equipo_id, nick");
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
    
        public Jugador obtener(int id) {
        Jugador j = null;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM jugador WHERE id = ?");
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
        
            public int insertar(Jugador j) {
        int id = -1;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement cuenta = con.prepareStatement("SELECT COUNT(*) FROM jugador WHERE equipo_id = ?");
            cuenta.setInt(1, j.getEquipoId());
            ResultSet rc = cuenta.executeQuery();
            rc.next();
            int total = rc.getInt(1);
            rc.close();
            cuenta.close();
            if (total < 7) {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO jugador (equipo_id, rol_id, nick, nombre, edad) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, j.getEquipoId());
                ps.setInt(2, j.getRolId());
                ps.setString(3, j.getNick());
                ps.setString(4, j.getNombre());
                ps.setInt(5, j.getEdad());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                    j.setId(id);
                }
                rs.close();
                ps.close();
            }
            con.close();
        } catch (SQLException e) {
            id = -1;
        }
        return id;
    }
            
            
                public boolean actualizar(Jugador j) {
        boolean ok = false;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement cuenta = con.prepareStatement("SELECT COUNT(*) FROM jugador WHERE equipo_id = ? AND id <> ?");
            cuenta.setInt(1, j.getEquipoId());
            cuenta.setInt(2, j.getId());
            ResultSet rc = cuenta.executeQuery();
            rc.next();
            int total = rc.getInt(1);
            rc.close();
            cuenta.close();
            if (total < 7) {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE jugador SET equipo_id = ?, rol_id = ?, nick = ?, nombre = ?, edad = ? WHERE id = ?");
                ps.setInt(1, j.getEquipoId());
                ps.setInt(2, j.getRolId());
                ps.setString(3, j.getNick());
                ps.setString(4, j.getNombre());
                ps.setInt(5, j.getEdad());
                ps.setInt(6, j.getId());
                ok = ps.executeUpdate() > 0;
                ps.close();
            }
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
            PreparedStatement ps = con.prepareStatement("DELETE FROM jugador WHERE id = ?");
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
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM jugador");
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
                        
                            public List<Jugador> buscar(String texto) {
        List<Jugador> resultado = new ArrayList<>();
        if (texto == null) {
            texto = "";
        }
        String t = texto.toLowerCase();
        for (Jugador j : listar()) {
            if (j.getNick().toLowerCase().contains(t) || j.getNombre().toLowerCase().contains(t)) {
                resultado.add(j);
            }
        }
        return resultado;
    }
}