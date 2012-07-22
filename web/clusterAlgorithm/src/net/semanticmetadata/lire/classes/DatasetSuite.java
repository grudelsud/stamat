
package net.semanticmetadata.lire.classes;

import distance.EuclideanDistance;
import distance.linkage.SingleLinkage;

import input.Dataset;
import input.FeatureVector;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Alex
 */
public class DatasetSuite {
    private Dataset dataset;
    private ArrayList<String> CeddDescriptors;
    
    // constructor
    public DatasetSuite(ArrayList<String> CeddDescriptors){
        this.dataset = new Dataset();
        this.CeddDescriptors = CeddDescriptors;
    }
    
    public void CreateDataset() {
        for (Iterator<String> ceddIterator = CeddDescriptors.iterator(); ceddIterator.hasNext(); ){
                    String singleCedd = ceddIterator.next(); 
                    String[] splitLine = singleCedd.split(" "); // tokenized Cedd Line
                    // creating single Feature Vector from tokenized Cedd line
                    FeatureVector featureVector = new FeatureVector(splitLine,false); 
                    this.dataset.add(featureVector);
                }          
    }
    
    // to retrieve the dataset
    public Dataset getDataset(){
        return this.dataset;
    }
    
}
