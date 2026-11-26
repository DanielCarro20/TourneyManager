package es.izquierdofp.tourneymanager.datos;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
 
public class EstadisticasDAO {
 
    public int equiposInscritos(int torneoId) {
        int total = 0;
        try {
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM inscripcion WHERE torneo_id = ?");
            ps.setInt(1, torneoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
 
    public double premioTotal() {
        double total = 0;
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(premio), 0) FROM torneo");
            if (rs.next()) {
                total = rs.getDouble(1);
            }
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
 
    public Map<String, Integer> ranking() {
        Map<String, Integer> ranking = new LinkedHashMap<>();
        try {
            Connection con = DatabaseManager.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT e.nombre, SUM(CASE i.posicion WHEN 1 THEN 10 WHEN 2 THEN 6 WHEN 3 THEN 4 ELSE 1 END) AS puntos "
                    + "FROM inscripcion i JOIN equipo e ON e.id = i.equipo_id "
                    + "WHERE i.posicion IS NOT NULL "
                    + "GROUP BY e.id ORDER BY puntos DESC, e.nombre");
            while (rs.next()) {
                ranking.put(rs.getString(1), rs.getInt(2));
            }
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ranking;
    }
}
