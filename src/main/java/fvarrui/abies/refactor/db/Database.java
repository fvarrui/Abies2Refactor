package fvarrui.abies.refactor.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Database {

	private static final Logger LOG = Logger.getLogger(Database.class.getName());
	private static final ResourceBundle CONFIG = ResourceBundle.getBundle(Database.class.getPackage().getName() + ".db");
	private static Connection conn = null;
	
	private Database() {}

	public static void setConnection(Connection conn) {
		Database.conn = conn;
	}

	public static Connection getConnection() {
		try {
			if (conn == null || conn.isClosed()) {
				conn = connect();
			}
		} catch (SQLException e) {
			LOG.severe("Error al abrir la conexión: " + e.getMessage());
			e.printStackTrace();
		}
		return conn;
	}
	
	private static void registerDriver() {
		LOG.info("Registrando driver: " + CONFIG.getString("db.driver.classname"));
		try {
			Class.forName(CONFIG.getString("db.driver.classname"));
		} catch (ClassNotFoundException e) {
			LOG.severe("Error al cargar el driver JDBC: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static Connection connect(String url, String username, String password) {
		registerDriver();
		Connection connection = null;
		try {
			LOG.info("Abriendo conexión: " + url);
			connection = DriverManager.getConnection(url, username, password);
			LOG.info("Conexión abierta");
		} catch (SQLException e) {
			LOG.severe("No fue posible establecer la conexión: " + e.getMessage());
			e.printStackTrace();
		}
		return connection;
	}
	
	public static Connection connect() {
		return connect(CONFIG.getString("db.url"), CONFIG.getString("db.username"), CONFIG.getString("db.password"));
	}
	
	public static void close(Connection conn) {
		LOG.info("Cerrando conexión");
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
			}
			LOG.info("Conexión cerrada");
		} catch (SQLException e) {
			LOG.severe("No fue posible cerrar la conexión: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static ResultSet executeQuery(String sql, Object ... params) throws SQLException {
		LOG.info("Ejecutando consulta: " + sql);
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		for (int i = 0; i < params.length; i++) {
			stmt.setObject(i + 1, params[i]);
		}
		return stmt.executeQuery();
	}
	
	public static int executeUpdate(String sql, Object ... params) throws SQLException {
		LOG.info("Ejecutando consulta: " + sql);
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		for (int i = 0; i < params.length; i++) {
			stmt.setObject(i + 1, params[i]);
		}
		int count = stmt.executeUpdate();
		stmt.close();
		return count;
	}
	
	public static void close() {
		close(conn);
	}

}
