package es.izquierdofp.tourneymanager.datos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            }

            st.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }
}