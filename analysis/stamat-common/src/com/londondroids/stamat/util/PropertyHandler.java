package com.londondroids.stamat.util;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyHandler {
	private Properties properties = null;
	private FileInputStream fis = null;
	private static PropertyHandler instance = null;
	
	private PropertyHandler() {
		try {
			this.fis = new FileInputStream("stamat.properties");
			this.properties = new Properties();
			this.properties.load(fis);
		} catch(Throwable t) {
			System.err.println( "exception caught - " + t.getMessage() );
		} finally {
			try {
				this.fis.close();
			} catch(Throwable t) {
				System.err.println( "exception caught - " + t.getMessage() );
			}
		}
	}
	
	public static PropertyHandler getInstance() {
		if( PropertyHandler.instance == null ) {
			PropertyHandler.instance = new PropertyHandler();
		}
		return PropertyHandler.instance;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}
}
