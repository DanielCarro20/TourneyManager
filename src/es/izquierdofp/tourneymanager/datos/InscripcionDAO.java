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
 
    
}