package it.unifi.micc.homer.model.topic;

import java.io.File;

import cc.mallet.topics.ParallelTopicModel;

/**
 * @author bertini
 *
 */

public class TrainedModel {
	
	File modelFile = null;
	ParallelTopicModel model = null;

	private TrainedModel() {
	}

	public static TrainedModel getInstance(String path) throws Exception {
		TrainedModel tm = new TrainedModel();
		tm.modelFile = new File(path);
		tm.model = tm.model.read(tm.modelFile);
		return tm;
	}

	public ParallelTopicModel getModel(){
		return this.model;
	}
	
	public void saveModelFile(ParallelTopicModel ldaModel){
		this.model= ldaModel;
		this.model.write(this.modelFile);
	}
}