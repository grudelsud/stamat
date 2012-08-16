package models;

public class Constants {

	public static class json_fields
	{
		public static final String INDEX_FIELD_INDEX = "index";
		public static final String INDEX_FIELD_IMAGES = "images";
		public static final String INDEX_FIELD_IMAGE_ID = "id";
		public static final String INDEX_FIELD_IMAGE_URL = "url";
	
		public static final String QUERY_FIELD_SOURCE = "source";
		public static final String QUERY_FIELD_FILEID = "fileidentifier";
		public static final String QUERY_FIELD_FEATURE = "feature";
		public static final String QUERY_FIELD_NUMOFRESULT = "numberofresults";
	}

	public static class db_fields 
	{	
		public static final int MEDIA_DOWNLOADED = 1;
		public static final int MEDIA_INVALID = 2;
		public static final int MEDIA_INDEXED = 4;
		public static final int	MEDIA_QUEUEDFORINDEXING = 8;
		public static final int MEDIA_INDEXINGEXCEPTION = 16;
	}

	private static final String threeClassClassifier = "/public/classifiers/english.all.3class.distsim.crf.ser.gz";
	private static final String sevenClassClassifier = "/public/classifiers/english.muc.7class.distsim.crf.ser.gz";
	
	public static String getIndicesFolderPath()
	{
		// maybe the solution is easier than initially thought if paths are relative to main folder...
		return "indices";
//		return play.Play.application().getFile("/public/indices").getAbsolutePath();
	}

	public static String getPublicFolderPath()
	{
		return play.Play.application().getFile("/public").getAbsolutePath();
	}

	public static String getThreeClassifierPath()
	{
		return play.Play.application().getFile( Constants.threeClassClassifier ).getAbsolutePath();
	}
	public static String getSevenClassifierPath()
	{
		return play.Play.application().getFile( Constants.sevenClassClassifier ).getAbsolutePath();
	}
}
