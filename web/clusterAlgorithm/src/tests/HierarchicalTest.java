package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import input.Dataset;
import input.FeatureVector;
import input.InputReader;

import org.junit.Before;
import org.junit.Test;

import algorithms.HierarchicalClustering;
import distance.EuclideanDistance;

import distance.linkage.CompleteLinkage;
/**
 * @author Markus
 *
 */
public class HierarchicalTest {
        
        private Dataset testset;
        @Before
        public void setUp(){
                        InputReader inputReader = new InputReader();
                        this.testset =inputReader.readFromfile("D:\\data\\cluster\\input\\90.valid");
                       
        }
        
        
        
        @Test
        public void testHierarchicalClustering(){
                for (FeatureVector featureVector : testset) {
                        assertEquals(FeatureVector.UNCLASSIFIED ,featureVector.getCalculatedClusternumber() );
                }
                HierarchicalClustering htClusterer = new HierarchicalClustering(new CompleteLinkage(new EuclideanDistance()));
                htClusterer.setLimit(745);
                htClusterer.doClustering(testset);
                
                for (FeatureVector featureVector : testset) {
                         assertFalse(FeatureVector.UNCLASSIFIED == featureVector.getCalculatedClusternumber());
                }
                System.out.print(testset.toString());
        }
}