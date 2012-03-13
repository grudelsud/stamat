/**
 * 
 */
package it.unifi.micc.homer.util;

import it.unifi.micc.homer.model.MediaDAO;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.HomerException;
import it.unifi.micc.homer.util.HomerProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author bertini
 *
 */
public class HomerProperties extends Properties {
	
	private static Logger logger = Logger.getLogger(HomerProperties.class);
	
	static final long serialVersionUID = 2L;

	private static String applicationPath = "";
	
	private static HomerProperties connData = null;
	private static boolean connDataLoaded = false;
	
	private static HomerProperties options = null;
	private static boolean optionsLoaded = false;
	
	/**
	 * one call to rule them all, initiates everything and returns the options instance
	 * 
	 * @param path
	 * @return the options instance, including database connection data and absolute path
	 * @throws OrioneException
	 */
	public static HomerProperties getInstance( String path ) throws HomerException {
		int initConn = HomerProperties.initConnData(path);
		int initOpt  = HomerProperties.initOptions();

		if( initConn != 0 || initOpt != 0 ) {
			String msg = "exception while initializing the app - status codes are (init" + initConn + ": opt:" + initOpt + ")";
			if( options == null )
				throw new HomerException( msg );
			else
				logger.error(msg); // FIXME for now we are not using any property stored in DB, so it's OK to go on...
									// change this behaviour as soon as e need those parameters !
		}
		
		return options;
	}
	/**
	 * Use HomerProperties.getInstance! this method is public for configuration purposes only.
	 * 
	 * Load options from database and returns integer status: 0 is OK,
	 * -1 database connection data file (orione.properties) not loaded (call either initConnData or storeConnData first)
	 * -2 SQL exception while retrieving data from database, -3 orioneexception (either orione.properties is containing wrong data
	 * or the application server is unable to connect to database server)
	 * 
	 * @return integer status, see above
	 */
	public static int initOptions() {
		try {
			if( connData != null && connDataLoaded == true ) {
				String host = connData.getProperty( HomerConstants.PROP_DBHOST );
				String db = connData.getProperty( HomerConstants.PROP_DBNAME );
				String user = connData.getProperty( HomerConstants.PROP_DBUSER );
				String pass = connData.getProperty( HomerConstants.PROP_DBPASS );
				options = new HomerProperties();
				options.putAll(connData);
				options.setProperty(HomerConstants.PROP_ABSPATH, HomerProperties.applicationPath);

				Statement stmt = MediaDAO.getConnectionStatement(host, db, user, pass);
				String sql = "SELECT * FROM options";
				try{
					ResultSet rs = stmt.executeQuery( sql );
	
					try{
						while( rs.next() ) {
							String name = rs.getString( "name" );
							String value = rs.getString( "value" );
							options.setProperty( name, value );
						}
					} finally {
						rs.close();
					}
	
					optionsLoaded = true;
				} finally {
					stmt.close();
				}
				return 0;
			} else {
				optionsLoaded = false;
//				options = null;
				return -1;
			}
			
		} catch (SQLException e) {
			optionsLoaded = false;
//			options = null;
			return -2;
		} catch (HomerException e) {
			// MediaDAO.getConnectionStatement(host, db, user, pass);
			optionsLoaded = false;
//			options = null;
			return -3;
		}
	}
	
	/**
	 * Store options in the homer database and returns integer status: 0 is ok,
	 * -1 database connection data file (homer.properties) not loaded (call either initConnData or storeConnData first)
	 * -2 sql exception while saving data to database, -3 homerexception (either homer.properties is containing wrong data
	 * or the application server is unable to connect to database server)
	 * 
	 * @param options
	 * @return integer status, see above
	 */
	public static int storeOptions( HomerProperties options ) {
		try {
			if( connData != null && connDataLoaded == true ) {
				String host = connData.getProperty( HomerConstants.PROP_DBHOST );
				String db = connData.getProperty( HomerConstants.PROP_DBNAME );
				String user = connData.getProperty( HomerConstants.PROP_DBUSER );
				String pass = connData.getProperty( HomerConstants.PROP_DBPASS );
				
				Statement stmt = MediaDAO.getConnectionStatement(host, db, user, pass);				
				String sql = "TRUNCATE options";
				stmt.executeUpdate(sql);
				stmt.close();
				
				sql = "INSERT INTO options (name, value) VALUES (?, ?)";
				PreparedStatement pstmt = MediaDAO.getConnectionPrepStatement(host, db, user, pass, sql);

				for( String key : options.stringPropertyNames() ) {
					String value = options.getProperty(key);
					pstmt.setString(1, key);
					pstmt.setString(2, value);
					pstmt.executeUpdate();
				}
				pstmt.close();
				HomerProperties.options = options;
				optionsLoaded = true;
				return 0;
			} else {
				optionsLoaded = false;
				HomerProperties.options = null;
				return -1;
			}
			
		} catch (SQLException e) {
			optionsLoaded = false;
			options = null;
			return -2;
		} catch (HomerException e) {
			optionsLoaded = false;
			options = null;
			return -3;
		}
	}

	/**
	 * utility function used to get a default set of options and optionally store it to
	 * the orione database
	 * 
	 * @return default options
	 */
	public static HomerProperties getDefaultOptions() {
		HomerProperties vp = new HomerProperties();
		vp.setProperty(HomerConstants.PROP_VERBOSE_LOG, "true");
		vp.setProperty(HomerConstants.PROP_ENTITIES_REL_PATH, ".");
		return vp;
	}

	/**
	 * Use HomerProperties.getInstance! this method is public for configuration purposes only.
	 * 
	 * Loads DB connection data properties and returns integer status: 0 is OK,
	 * -1 file not found (data should be stored before), -2 I/O exception 
	 * (write permissions in path?)
	 * 
	 * @param path of web application
	 * @return integer status, see above
	 */
	public static int initConnData( String path ) {
		try {
			HomerProperties connData = new HomerProperties();
			connData.loadConnData(path);
			applicationPath = path;
			HomerProperties.connData = connData;
			connDataLoaded = true;
			return 0;
		} catch( FileNotFoundException e ) {
			applicationPath = "";
			connData = null;
			connDataLoaded = false;
			return -1;
		} catch( IOException e ) {
			applicationPath = "";
			connData = null;
			connDataLoaded = false;
			return -2;
		}
	}

    /**
     * Public method for database connection data storage
     * 
     * @param application path
     * @param host
     * @param db
     * @param user
     * @param pass
     * @throws OrioneException
     */
    public static void storeConnData( String path, String host, String db, String user, String pass) throws HomerException {
    	HomerProperties vp = new HomerProperties();
    	vp.setProperty(HomerConstants.PROP_DBHOST, host);
    	vp.setProperty(HomerConstants.PROP_DBNAME, db);
    	vp.setProperty(HomerConstants.PROP_DBUSER, user);
    	vp.setProperty(HomerConstants.PROP_DBPASS, pass);
    	vp.storeConnData(path);
    	applicationPath = path;
    	connData = vp;
    	connDataLoaded = true;
    }

    /**
	 * Stores database connection data to orione.properties file in the application folder
	 * 
	 * @param path
	 * @throws HomerException
	 */
	private void storeConnData( String path ) throws HomerException {
		try {
			FileOutputStream oStream = new FileOutputStream( path + HomerConstants.PROPFILE_NAME );
			super.store( oStream, HomerConstants.PROPFILE_COMMENT );
		} catch( IOException ioe ) {
			throw new HomerException( "could not create/write " + HomerConstants.PROPFILE_NAME + " file" );
		}
	}

    /**
     * Loads the orione.properties file located in the webapp absolute path
     * 
     * @param path set by servlet init
     * @throws HomerException
     * @throws FileNotFoundException 
     */
    private void loadConnData( String path ) throws FileNotFoundException, IOException {
		File file = new File( path + HomerConstants.PROPFILE_NAME );
		FileInputStream inStream = new FileInputStream( file );
		super.load( inStream );
	}

	/**
	 * @return the applicationPath
	 */
	public static String getApplicationPath() {
		return applicationPath;
	}

	/**
	 * @return the connData
	 */
	public static HomerProperties getConnData() {
		return connData;
	}

	/**
	 * @return the connDataLoaded
	 */
	public static boolean isConnDataLoaded() {
		return connDataLoaded;
	}

	/**
	 * @return the options
	 */
	public static HomerProperties getOptions() {
		return options;
	}

	/**
	 * @return the optionsLoaded
	 */
	public static boolean isOptionsLoaded() {
		return optionsLoaded;
	}

}
