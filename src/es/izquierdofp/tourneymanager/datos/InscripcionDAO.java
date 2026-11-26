package es.izquierdofp.tourneymanager.datos;
 
import es.izquierdofp.tourneymanager.modelo.Inscripcion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
 
public class InscripcionDAO {
 
    private Inscripcion mapear(ResultSet rs) throws SQLException {
        Inscripcion i = new Inscripcion();
        i.setId(rs.getInt("id"));
        i.setEquipoId(rs.getInt("equipo_id"));
        i.setTorneoId(rs.getInt("torneo_id"));
        int pos = rs.getInt("posicion");
        if (rs.wasNull()) {
            i.setPosicion(null);
        } else {
            i.setPosicion(pos);
        }
        return i;
    }
 
    public List<Inscripcion> listar() {
        List<Inscripcion> lista = new ArrayList<>();
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM inscripcion ORDER BY torneo_id, posicion IS NULL, posicion");
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
 
    public Inscripcion obtener(int id) {
        Inscripcion i = null;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM inscripcion WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                i = mapear(rs);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }
 
        public List<Inscripcion> listarPorEquipo(int equipoId) {
        List<Inscripcion> lista = new ArrayList<>();
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM inscripcion WHERE equipo_id = ? ORDER BY torneo_id");
            ps.setInt(1, equipoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
 
    public List<Inscripcion> listarPorTorneo(int torneoId) {
        List<Inscripcion> lista = new ArrayList<>();
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM inscripcion WHERE torneo_id = ? ORDER BY posicion IS NULL, posicion");
            ps.setInt(1, torneoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
     private int contarPorTorneo(Connection con, int torneoId, int excluirId) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM inscripcion WHERE torneo_id = ? AND id <> ?");
        ps.setInt(1, torneoId);
        ps.setInt(2, excluirId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int total = rs.getInt(1);
        rs.close();
        ps.close();
        return total;
    }
 
    private boolean posicionOcupada(Connection con, int torneoId, int posicion, int excluirId) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM inscripcion WHERE torneo_id = ? AND posicion = ? AND id <> ?");
        ps.setInt(1, torneoId);
        ps.setInt(2, posicion);
        ps.setInt(3, excluirId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        boolean ocupada = rs.getInt(1) > 0;
        rs.close();
        ps.close();
        return ocupada;
    }
 
    public int insertar(Inscripcion i) {
        int id = -1;
        try {
            Connection con = DatabaseManager.getConnection();
            boolean permitido = true;
            if (contarPorTorneo(con, i.getTorneoId(), 0) >= 16) {
                permitido = false;
            }
            if (permitido && i.getPosicion() != null && posicionOcupada(con, i.getTorneoId(), i.getPosicion(), 0)) {
                permitido = false;
            }
            if (permitido) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO inscripcion (equipo_id, torneo_id, posicion) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, i.getEquipoId());
                ps.setInt(2, i.getTorneoId());
                if (i.getPosicion() == null) {
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(3, i.getPosicion());
                }
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                    i.setId(id);
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

    public boolean actualizar(Inscripcion i) {
        boolean ok = false;
        try {
            Connection con = DatabaseManager.getConnection();
            boolean permitido = true;
            if (contarPorTorneo(con, i.getTorneoId(), i.getId()) >= 16) {
                permitido = false;
            }
            if (permitido && i.getPosicion() != null && posicionOcupada(con, i.getTorneoId(), i.getPosicion(), i.getId())) {
                permitido = false;
            }
            if (permitido) {
                PreparedStatement ps = con.prepareStatement("UPDATE inscripcion SET equipo_id = ?, torneo_id = ?, posicion = ? WHERE id = ?");
                ps.setInt(1, i.getEquipoId());
                ps.setInt(2, i.getTorneoId());
                if (i.getPosicion() == null) {
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(3, i.getPosicion());
                }
                ps.setInt(4, i.getId());
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
            PreparedStatement ps = con.prepareStatement("DELETE FROM inscripcion WHERE id = ?");
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
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM inscripcion");
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
