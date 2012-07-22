/**
 * 
 */
package algorithms;

import input.Dataset;
import input.FeatureVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import output.Cluster;
import output.ValidationWriter;
import distance.linkage.Linkage;

/**
 * @author Markus
 * 
 */
public class HierarchicalClustering implements ClusteringAlgorithm {

        private Linkage linkage;
        private Integer limit;
        private Boolean output = false;

        /**
         * 
         */
        public HierarchicalClustering(Linkage linkage) {
                this.linkage = linkage;
        }
        
        /**
         * 
         * @param linkage measures the distance between 2 clusters
         * @param output  output every clustering result optained
         */
        public HierarchicalClustering(Linkage linkage, Boolean output) {
                this.linkage = linkage;
                this.output = output;
        }


        /*
         * (non-Javadoc)
         * 
         * @see algorithms.ClusteringAlgorithm#doClustering(input.Dataset)
         */
        @Override
        public void doClustering(Dataset dataset) {
                if (this.limit == null){
                        this.limit = 2;
                }
                List<Cluster[]> clusterpairs;
                
                // assegna a ciascun fv un proprio cluster
                for (int i = 0; i < dataset.size(); i++) {
                        dataset.get(i).setCalculatedClusternumber(i);  
                }
                // start the clustering:
                // we merge clusters until there is only one left (limit is 2)
                for (int i = 0; i < dataset.size() && ((dataset.size()-i) >this.limit); i++) {
                        // create the linked  hash map like the dataset (cluster's) linked hash map
                        LinkedHashMap<Integer, Cluster> clustermap = new LinkedHashMap<Integer, Cluster>();
                        clustermap.putAll(dataset.getClustermap());
                        // create a list of all the keys inside the linked hash map
                        ArrayList<Integer> clusterKeys = new ArrayList<Integer>(clustermap.keySet());
                        // create Red-Black tree for managing the distances
                        TreeMap<Float, List<Cluster[]>> clusterDist = new TreeMap<Float, List<Cluster[]>>();
                        for (int j = 0; j < clusterKeys.size(); j++) {
                                Cluster cluster1 = clustermap.get(clusterKeys.get(j));
                                
                                for (int k = j + 1; k < clusterKeys.size() ; k++) {
                                        // analize 2 clusters for time
                                        Cluster cluster2 = clustermap.get(clusterKeys.get(k));
                                        Cluster[] clusterpair = new Cluster[2];
                                        clusterpair[0] = cluster1;
                                        clusterpair[1] = cluster2;
                                        
                                        // calculate distance between 2 clusters
                                        float dist = this.linkage.calculateClusterdistance(cluster1, cluster2);
                                        clusterpairs = clusterDist.get(dist);
                                        // add this pair to the list of clusterdistances
                                        if (clusterpairs == null) {
                                                clusterpairs = new LinkedList<Cluster[]>();
                                                clusterpairs.add(clusterpair);
                                                clusterDist.put(dist, clusterpairs); // add the clusterpair
                                        }

                                }
                                
                        }
                        if (clustermap.size() >1 ) {// if there is only one left do nothing, else merge two cluster with smallest distance
                                List<Cluster[]> closestClusters = clusterDist.firstEntry().getValue(); //the two clusters with the smallest distance
                                Cluster[] clustersToMerge = closestClusters.get(0); //randomly pick the first pair with the min dist
                                //merge clusters 
                                //TODO special action when distance is equal for more than one pair?
                                mergeClusters(clustersToMerge[0], clustersToMerge[1]);
                        }
                        if (output == true){
                                Map<String,String> params = new HashMap<String,String>();
                                ValidationWriter.writeToCSV("Hierarchical.csv", Algorithms.Hierarchical, dataset, params);
                        }

                }

        }
        
        /**
         * This method should take two clusters and merge them to one 
         * @param cluster1
         * @param cluster2
         */
        private void mergeClusters (Cluster cluster1, Cluster cluster2){
                cluster2.setClusterid(cluster1.getClusterid());
                List<FeatureVector> elements = cluster2.getClusterelements();
                for (FeatureVector featureVector : elements) {
                        featureVector.setCalculatedClusternumber(cluster1.getClusterid());
                }
        }

        public Linkage getLinkage() {
                return linkage;
        }

        public void setLinkage(Linkage linkage) {
                this.linkage = linkage;
        }

        public String toString() {
                return "Hierarchical Clustering";
        }
        
        /**
         * @return the number of clusters you want. the algorithm works its way up form
         * dataset.size() clusters to a number of clusters that equals limit. if not specified
         * the algorithm assumes 2 as the limit
         */
        public int getLimit() {
                return limit;
        }

        public void setLimit(Integer limit) {
                this.limit = limit;
        }

}