package models;

import java.io.File;

public class Constants {

	private static final String threeClassClassifier = "/public/classifiers/english.all.3class.distsim.crf.ser.gz";
	private static final String sevenClassClassifier = "/public/classifiers/english.muc.7class.distsim.crf.ser.gz";
	
	public static String getThreeClassifierPath()
	{
		File classifierPath = play.Play.application().getFile( Constants.threeClassClassifier );
		return classifierPath.getAbsolutePath();
	}
	public static String getSevenClassifierPath()
	{
		File classifierPath = play.Play.application().getFile( Constants.sevenClassClassifier );
		return classifierPath.getAbsolutePath();
	}
}
