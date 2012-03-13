package it.unifi.micc.homer.util;


/**
 * @author bertini
 * 
 * Singleton to access DB (http://en.wikipedia.org/wiki/Singleton_pattern#The_solution_of_Bill_Pugh)
 *
 */
public class DBAccess {
	private static String db, pass, user, host;
 
	// Private constructor prevents instantiation from other classes
	private DBAccess() {}
	
	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder { 
		private static final DBAccess instance = new DBAccess();
	}
 
	/**
	 * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader) than the moment that getInstance() is called. Thus, this solution is thread-safe without requiring special language constructs (i.e. volatile or synchronized).
	 * @return
	 */
	public static DBAccess getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
	 * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader) than the moment that getInstance() is called. Thus, this solution is thread-safe without requiring special language constructs (i.e. volatile or synchronized).
	 * @return
	 */
	public static DBAccess getInstance(String aHost, String aDb, String aUser, String aPass) {
		host = aHost;
		db = aDb;
		user = aUser;
		pass = aPass;
		
		return SingletonHolder.instance;
	}

	/**
	 * @return the db
	 */
	public String getDb() {
		return db;
	}

	/**
	 * @param db the db to set
	 */
	public void setDb(String aDb) {
		db = aDb;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass the pass to set
	 */
	public void setPass(String aPass) {
		pass = aPass;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String aUser) {
		user = aUser;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String aHost) {
		host = aHost;
	}
}
