package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Jugador;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
}