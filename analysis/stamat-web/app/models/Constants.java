package models;

public class Constants {

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
