package it.unifi.micc.homer.model;

import it.unifi.micc.homer.util.DBAccess;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.HomerException;
import it.unifi.micc.homer.util.HomerProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

/**
 * Used to access database with generic (i.e. get statements) and general (i.e. check credentials) functions
 *
 */
public abstract class MediaDAO {
	
	private static Logger logger = Logger.getLogger(MediaDAO.class);
	protected static DBAccess dbAccess = DBAccess.getInstance(); // used by MediaDAO subclasses
	private static Connection conn = null;

	/**
	 * used to log a call (usually a search by a web user) on the logs table
	 * 
	 * @param user_id user identifier as stored on table users
	 * @param command_id command identifier as stored on table commands
	 * @param url the called url (or parameters)
	 * @throws OrioneException
	 */
	public static void logUrl( int user_id, int command_id, String url ) throws HomerException {
		logger.info( "Logging data: " + url );
		HomerProperties vp = HomerProperties.getConnData();

		String host = vp.getProperty( HomerConstants.PROP_DBHOST );
		String db   = vp.getProperty( HomerConstants.PROP_DBNAME );
		String user = vp.getProperty( HomerConstants.PROP_DBUSER );
		String pass = vp.getProperty( HomerConstants.PROP_DBPASS );
		String sql  = "INSERT INTO logs (id_users, id_commands, url) VALUES (?, ?, ?)";
		
		PreparedStatement pstmt = MediaDAO.getConnectionPrepStatement(host, db, user, pass, sql);
		try {
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, command_id);
			pstmt.setString(3, url);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			logger.debug("pem!", e);
			throw new HomerException( e );
		}
	}
	/**
	 * Checks if a user can be authenticated on the orione database
	 * 
	 * @param user username as defined in users table 
	 * @param pass password, clear text, will be encrypted md5
	 * @return
	 * @throws OrioneException 
	 */
	public static boolean checkCredentials( String user, String pass ) throws HomerException {
		HomerProperties vp      = HomerProperties.getConnData();
		
		String         host    = vp.getProperty( HomerConstants.PROP_DBHOST );
		String         db      = vp.getProperty( HomerConstants.PROP_DBNAME );
		String         dbuser  = vp.getProperty( HomerConstants.PROP_DBUSER );
		String         dbpass  = vp.getProperty( HomerConstants.PROP_DBPASS );
		
		String         sql     = "SELECT * FROM users WHERE name='" + user + "'";
		Statement      stmt    = getConnectionStatement(host, db, dbuser, dbpass);
		
		try {
			try{
				ResultSet rs       = stmt.executeQuery(sql);
				String    encPass  = DigestUtils.md5Hex( pass );
				try{
					while( rs.next() ) {
						String dbPass = rs.getString("password");
						if( 0 == encPass.compareToIgnoreCase( dbPass ) ) {
							stmt.close();
							return true;
						}
					}
				} finally {
					rs.close();
				}
			} finally {
				stmt.close();
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}

	/**
	 * Returns a satic sql statement
	 * 
	 * @param host db server
	 * @param db db name
	 * @param user db user
	 * @param pass db pass
	 * @return the sql statement
	 * @throws OrioneException
	 */
	public static Statement getConnectionStatement( String host, String db, String user, String pass ) throws HomerException {	
		try {
			if( conn == null ){
				String connStr = "jdbc:mysql://" + host + "/" + db;
				Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
				conn = DriverManager.getConnection( connStr, user, pass );
			}
			Statement stmt;
			try{
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			} finally {
				//conn.close();
			}
			return stmt;
		} catch( Throwable t ) {
			throw new HomerException( t );
		}
	}
	
	/**
	 * Returns a parametric (prepared) statement
	 * 
	 * @param host db server
	 * @param db db name
	 * @param user db user
	 * @param pass db pass
	 * @param sql the parametric sql query (must contain question marks in order to work!)
	 * @return the sql statement
	 * @throws OrioneException
	 */
	public static PreparedStatement getConnectionPrepStatement( String host, String db, String user, String pass, String sql ) throws HomerException {	
		try {
			if( conn == null ){
				String connStr = "jdbc:mysql://" + host + "/" + db;
				Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
				conn = DriverManager.getConnection( connStr, user, pass );
			}
			return conn.prepareStatement(sql);
		} catch( Throwable t ) {
			throw new HomerException( t );
		}
	}
	
	
	/**
	 * Closes the java.sql Connection object that is instantiated by the getConnection(Prep)Statement methods, e.g. if you need to make a new connection to a different DB
	 * 
	 * @throws OrioneException
	 */
	public static void closeConnection() throws HomerException {
		try{
		conn.close();
		} catch( Throwable t ) {
			throw new HomerException( t );
		}
	}
}