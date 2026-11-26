package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Rol;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;

public class RolDAO {

    private Rol mapear(ResultSet rs) throws SQLException {
        Rol r = new Rol();
        r.setId(rs.getInt("id"));
        r.setNombre(rs.getString("nombre"));
        return r;
    }

    public List<Rol> listar() {
        List<Rol> lista = new ArrayList<>();
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM rol ORDER BY id");
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

    public Rol obtener(int id) {
        Rol r = null;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM rol WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                r = mapear(rs);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    public int insertar(Rol r) {
        int id = -1;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO rol (nombre) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, r.getNombre());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
                r.setId(id);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            id = -1;
        }
        return id;
    }

    public boolean actualizar(Rol r) {
        boolean ok = false;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE rol SET nombre = ? WHERE id = ?");
            ps.setString(1, r.getNombre());
            ps.setInt(2, r.getId());
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
            PreparedStatement ps = con.prepareStatement("DELETE FROM rol WHERE id = ?");
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
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM rol");
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