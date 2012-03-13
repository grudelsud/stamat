/**
 * 
 */
package it.unifi.micc.homer.model.namedentity;

/**
 * @author bertini
 *
 */
import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ANNIEConstants;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

public class AnnieNEDAnalyser {
	/** The Corpus Pipeline application to contain ANNIE */
	private SerialAnalyserController annieController;

	/**
	 * Initialise the ANNIE system. This creates a "corpus pipeline" application
	 * that can be used to run sets of documents through the extraction system.
	 */
	public void initAnnie() throws GateException {
		// Out.prln("Initialising ANNIE...");
		// create a serial analyser controller to run ANNIE with
		annieController = (SerialAnalyserController) Factory.createResource(
				"gate.creole.SerialAnalyserController",
				Factory.newFeatureMap(), Factory.newFeatureMap(), 
				"ANNIE_" + Gate.genSym());
		// load each PR as defined in ANNIEConstants
		for (int i = 0; i < ANNIEConstants.PR_NAMES.length; i++) {
			FeatureMap params = Factory.newFeatureMap();	// use default
															// parameters
			ProcessingResource pr = (ProcessingResource) Factory.createResource(ANNIEConstants.PR_NAMES[i], params);

			// add the PR to the pipeline controller
			annieController.add(pr);
		} // for each ANNIE PR

		// Out.prln("...ANNIE loaded");
	} // initAnnie()

	/** Tell ANNIE's controller about the corpus you want to run on */
	public void setCorpus(Corpus corpus) {
		annieController.setCorpus(corpus);
	} // setCorpus

	/** Run ANNIE */
	public void runAnnie() throws GateException {
		// Out.prln("Running ANNIE...");
		annieController.execute();
		// Out.prln("...ANNIE complete");
	} // execute()

}
