package runner;

import input.Dataset;
import input.InputReader;

import net.semanticmetadata.lire.classes.CeddExtractor;

import java.util.HashMap;
import java.util.Map;

import distance.EuclideanDistance;
import distance.linkage.SingleLinkage;


import output.ValidationWriter;
import algorithms.HierarchicalClustering;

/**
 * Starts a run of the kmeans algorithm with specifeid parameters and saves
 * results to a file
 * 
 * @author Markus
 * 
 */
public class HierarchicalClusteringRunnerInputFile {

        public static void main(String[] args) {
                
                Integer numOfClusters = 5;  // uses 2 of default limit value
                // read input file path (passed By stamat)
                String inputFileName;  // cartella che contiene le foto
              //  inputFileName = "D:\\data\\images\\"; 
                inputFileName = "D:\\data\\images\\prova\\"; // per prova
              //  if (args.length == 2) {
                        // read input file path
             //           inputFileName = args[0];
               //         numOfClusters = Integer.valueOf(args[1]);
               //     } else if (args.length == 1) { inputFileName = args[0];}

               //  else{ throw new IllegalArgumentException(); }
                
                CeddExtractor Ceddextr = new CeddExtractor (inputFileName);
                Ceddextr.getImagesFromFolder();
                Ceddextr.extractCedds();
                String inputFile = Ceddextr.createInputFile();
                
                Dataset dataset = InputReader.readFromfile(inputFile);
                boolean printSteps = true; // needed to print the results on a csv file
                EuclideanDistance eucDis = new EuclideanDistance();
                SingleLinkage singLink = new SingleLinkage(eucDis);
                
                
                HierarchicalClustering hc = new HierarchicalClustering(singLink,printSteps);
                hc.setLimit(numOfClusters);
                hc.doClustering(dataset);
                
                // InputReader.writeDatasetToFile(outputFileName, dataset);
                //Map<String, String> params = new HashMap<String,String>();
                //params.put(ValidationWriter.KMEANS_K_LABEL, String.valueOf(numOfClusters));
                //useless since we should write out every step
                //  ValidationWriter.printValidationIndices("Hierarchical", params, dataset);
                //  ValidationWriter.writeValidationIndice(outputFileName, "Hierarchical",params, dataset);
        }

}