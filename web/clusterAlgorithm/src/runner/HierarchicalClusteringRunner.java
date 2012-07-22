
package runner;

import algorithms.HierarchicalClustering;
import distance.EuclideanDistance;
import distance.linkage.SingleLinkage;
import input.Dataset;
import java.util.ArrayList;
import net.semanticmetadata.lire.classes.CeddExtractor;
import net.semanticmetadata.lire.classes.DatasetSuite;


/**
 *
 * @author Alex
 */
public class HierarchicalClusteringRunner {
     public static void main(String[] args) {
          // setting limit value (number of remaining clusters before stop algorithm
          Integer numOfClusters = 5;  // uses 2 of default limit value
          String inputFileName;  // cartella che contiene le foto
              //  inputFileName = "D:\\data\\images\\"; 
          inputFileName = "D:\\data\\images\\prova\\"; // per prova
         
          CeddExtractor ceddextr = new CeddExtractor (inputFileName);
          ceddextr.getImagesFromFolder();
          ceddextr.extractCedds();
          ArrayList<String> ceddDescr = ceddextr.getCeddDescriptors();
          
          // creating Dataset without text file
          DatasetSuite dsSuite = new DatasetSuite(ceddDescr);
          dsSuite.CreateDataset();
          Dataset dataset = dsSuite.getDataset();
          
          boolean printSteps = true; // needed to print the results on a csv file
          EuclideanDistance eucDis = new EuclideanDistance();
          SingleLinkage singLink = new SingleLinkage(eucDis);
                
                
          HierarchicalClustering hc = new HierarchicalClustering(singLink,printSteps);
          hc.setLimit(numOfClusters);
          hc.doClustering(dataset);
          Dataset dat = dataset;
                
          
     }
}
