package es.izquierdofp.tourneymanager.datos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class DatabaseManager {

    private static String rutaBD = System.getProperty("user.home") + "/.tourneymanager/tourneymanager.db";

    public static void setRutaBD(String ruta) {
        rutaBD = ruta;
    }

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:" + rutaBD);
        Statement st = con.createStatement();
        st.execute("PRAGMA foreign_keys = ON");
        st.close();
        return con;
    }

    public static void inicializar() {
        File carpeta = new File(rutaBD).getAbsoluteFile().getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();
            st.execute("""
                CREATE TABLE IF NOT EXISTS juego (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS rol (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS equipo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE,
                    ciudad TEXT NOT NULL,
                    fundacion INTEGER NOT NULL CHECK (fundacion BETWEEN 1990 AND 2100),
                    logo TEXT
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS torneo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    juego_id INTEGER NOT NULL REFERENCES juego(id) ON DELETE RESTRICT,
                    fecha TEXT NOT NULL,
                    premio REAL NOT NULL CHECK (premio >= 0),
                    online INTEGER NOT NULL DEFAULT 0 CHECK (online IN (0,1))
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS inscripcion (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    equipo_id INTEGER NOT NULL REFERENCES equipo(id) ON DELETE CASCADE,
                    torneo_id INTEGER NOT NULL REFERENCES torneo(id) ON DELETE CASCADE,
                    posicion INTEGER CHECK (posicion IS NULL OR posicion >= 1),
                    UNIQUE (equipo_id, torneo_id)
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS jugador (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    equipo_id INTEGER NOT NULL REFERENCES equipo(id) ON DELETE CASCADE,
                    rol_id INTEGER NOT NULL REFERENCES rol(id) ON DELETE RESTRICT,
                    nick TEXT NOT NULL UNIQUE,
                    nombre TEXT NOT NULL,
                    edad INTEGER NOT NULL CHECK (edad BETWEEN 14 AND 60)
                )""");

            ResultSet rs = st.executeQuery("SELECT (SELECT COUNT(*) FROM juego) + (SELECT COUNT(*) FROM rol) + (SELECT COUNT(*) FROM equipo)");
            rs.next();
            int total = rs.getInt(1);
            rs.close();
            if (total == 0) {
                st.execute("INSERT INTO juego (nombre) VALUES ('League of Legends'), ('Valorant'), ('Rocket League'), ('EA Sports FC'), ('Fortnite')");
                st.execute("INSERT INTO rol (nombre) VALUES ('Capitán'), ('Titular'), ('Suplente'), ('Entrenador')");
                st.execute("INSERT INTO equipo (nombre, ciudad, fundacion) VALUES "
                        + "('Cierzo Gaming', 'Zaragoza', 2015), "
                        + "('Moncayo Esports', 'Tarazona', 2018), "
                        + "('Pilar Warriors', 'Zaragoza', 2012), "
                        + "('Ebro Legends', 'Zaragoza', 2020), "
                        + "('Huesca Wolves', 'Huesca', 2016), "
                        + "('Teruel Storm', 'Teruel', 2019), "
                        + "('Calatayud Kings', 'Calatayud', 2014), "
                        + "('Jaca Pirineos', 'Jaca', 2021)");

                st.execute("INSERT INTO jugador (equipo_id, rol_id, nick, nombre, edad) VALUES "
                        + "(1, 1, 'Kaze', 'Pablo Ortiz', 23), "
                        + "(1, 2, 'Nimbus', 'Sara Gil', 21), "
                        + "(1, 2, 'Bruma', 'Hugo Lasa', 20), "
                        + "(1, 3, 'Pixel', 'Marta Ruiz', 19), "
                        + "(1, 4, 'CoachRey', 'Raúl Mena', 35), "
                        + "(2, 1, 'Tarazo', 'Iván Sola', 24), "
                        + "(2, 2, 'Rafaga', 'Lucía Pardo', 22), "
                        + "(2, 2, 'Cumbre', 'Diego Arnal', 21), "
                        + "(2, 3, 'Brisa', 'Elena Moya', 18), "
                        + "(3, 1, 'Zeta', 'Carlos Navas', 26), "
                        + "(3, 2, 'Pilarin', 'Ana Sanz', 22), "
                        + "(3, 2, 'Acero', 'Mario Cano', 23), "
                        + "(3, 2, 'Rayo', 'Nuria Lobo', 20), "
                        + "(3, 3, 'Chispa', 'Álvaro Vera', 19), "
                        + "(3, 4, 'Maestro', 'Jorge Bielsa', 41), "
                        + "(4, 1, 'Rio', 'Daniel Alba', 25), "
                        + "(4, 2, 'Delta', 'Cristina Pons', 22), "
                        + "(4, 3, 'Remo', 'Sergio Lanza', 20), "
                        + "(5, 1, 'Lobo', 'Adrián Casas', 27), "
                        + "(5, 2, 'Aullido', 'Irene Mur', 21), "
                        + "(5, 2, 'Colmillo', 'Rubén Gracia', 22), "
                        + "(5, 3, 'Manada', 'Paula Ibáñez', 19), "
                        + "(5, 4, 'Alfa', 'Víctor Cebollero', 38), "
                        + "(6, 1, 'Tormenta', 'Gonzalo Pérez', 24), "
                        + "(6, 2, 'Trueno', 'Alba Soria', 23), "
                        + "(6, 2, 'Relampago', 'Samuel Gómez', 20), "
                        + "(6, 3, 'Nube', 'Carla Tena', 18), "
                        + "(7, 1, 'Rey', 'Óscar Millán', 28), "
                        + "(7, 2, 'Alfil', 'Beatriz Lacruz', 24), "
                        + "(7, 2, 'Torre', 'Fernando Aznar', 22), "
                        + "(7, 3, 'Peon', 'Silvia Calvo', 19), "
                        + "(7, 4, 'Gambito', 'Tomás Murillo', 44), "
                        + "(8, 1, 'Ibon', 'Marcos Lacasa', 23), "
                        + "(8, 2, 'Aneto', 'Laura Bernad', 21), "
                        + "(8, 2, 'Canfranc', 'Ismael Oliva', 20), "
                        + "(8, 3, 'Pirineo', 'Noelia Sanz', 18)");
                
                LocalDate hoy = LocalDate.now();
                st.execute("INSERT INTO torneo (nombre, juego_id, fecha, premio, online) VALUES "
                        + "('Copa Cierzo de Invierno', 1, '" + hoy.minusDays(60) + "', 500, 0), "
                        + "('Liga Aragonesa de Valorant', 2, '" + hoy.minusDays(20) + "', 300, 1), "
                        + "('Torneo Rocket del Pilar', 3, '" + hoy.plusDays(3) + "', 250, 0), "
                        + "('Copa EA FC Zaragoza', 4, '" + hoy.plusDays(25) + "', 400, 1), "
                        + "('Fortnite Ebro Cup', 5, '" + hoy.plusDays(50) + "', 200, 1), "
                        + "('Gran Final de la Temporada', 1, '" + hoy.plusDays(80) + "', 1000, 0)");
                st.execute("INSERT INTO inscripcion (equipo_id, torneo_id, posicion) VALUES "
                        + "(1, 1, 1), (3, 1, 2), (2, 1, 3), (5, 1, 4), (4, 1, 5), (6, 1, 6), (7, 1, 7), (8, 1, 8), "
                        + "(2, 2, 1), (1, 2, 2), (4, 2, 3), (3, 2, 4), (7, 2, 5), (5, 2, 6), (6, 2, 7), "
                        + "(1, 3, NULL), (2, 3, NULL), (3, 3, NULL)");

            }

            st.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }
}
