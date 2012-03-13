package it.unifi.micc.homer.model.topic;

import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.model.TextDocument;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import cc.mallet.topics.ParallelTopicModel;

/**
 * @author bertini
 *
 */

public class TrainedModel {
	
	String url_db;
	String db;
	String driver_db;
	String user_db;
	String password_db;

	File fmodel;
	ParallelTopicModel lda;
	
	
	
	public void setMYSQLparameters(String url_db, String db, String user_db, String password_db){
		this.url_db="jdbc:mysql://" + url_db;
		this.db = db;
		this.driver_db = "com.mysql.jdbc.Driver";
		this.user_db = user_db;
		this.password_db = password_db;
		
	}
	
	
	public List<TextDocument> getTrainingSample(String tag){
		List<TextDocument> texts = new ArrayList<TextDocument>();
		Connection con = null;
		try{
			  Class.forName(driver_db);
			  con = DriverManager.getConnection(url_db+db,user_db,password_db);
			  // Statements allow to issue SQL queries to the database
			  Statement statement = con.createStatement();
			  // Result set get the result of the SQL query
			  String query = "SELECT feeditemcontents.content " +
			  		"FROM feeditemcontents JOIN feeditems ON feeditemcontents.feeditem_id = feeditems.id " +
			  		"JOIN feeds_tags ON feeditems.feed_id = feeds_tags.feed_id " +
			  		"JOIN tags ON feeds_tags.tag_id = tags.id " +
			  		"WHERE tags.name LIKE '" + tag + "'";
			  ResultSet	resultSet = statement.executeQuery(query);
			  while (resultSet.next()) {
				  String line = resultSet.getString("content");
				  line= Jsoup.parse(line).text();
				  AsciiTextDocument textDocument = new AsciiTextDocument(line);
				  texts.add(textDocument);
			  }
			  con.close();
		}
		catch (Exception e){
			  e.printStackTrace();
		}
		return texts;
		
		
	}
	
	
	public String setModelFile(String tag){
		
		String filenameModel="";
		Connection con = null;
		try{
			  Class.forName(driver_db);
			  con = DriverManager.getConnection(url_db+db,user_db,password_db);
			  // Statements allow to issue SQL queries to the database
			  Statement statement = con.createStatement();
			  // Result set get the result of the SQL query
			  ResultSet	resultSet = statement.executeQuery("SELECT URIMODEL FROM tags WHERE name LIKE '"+tag+"'");
			  if (resultSet.next()) {
	                filenameModel= resultSet.getString("URIMODEL");
	                fmodel = new File(filenameModel);
	            }
			  con.close();
		}
		catch (Exception e){
			  e.printStackTrace();
		}
		return filenameModel;
}
	
	
	public ParallelTopicModel getTrainedModel(){
		try {
			lda=lda.read(fmodel);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lda;
	}
	
	public void saveTrainedModelFromFile(ParallelTopicModel ldaModel){
		lda= ldaModel;
		lda.write(fmodel);
	}
	

}