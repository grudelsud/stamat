package net.semanticmetadata.lire.classes;

/**
 *
 * @author Alex
 */

import net.semanticmetadata.lire.imageanalysis.CEDD;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class CeddExtractor {
   
      private ArrayList<String> images = new ArrayList<String>();
      private ArrayList<String> CeddDescriptors = new ArrayList<String>();
   //   private ArrayList<Byte[]> CeddDescriptors = new ArrayList<Byte[]>();

      private String imagesPath;
      private String outputFileName = "cedd.txt";
        // Getting all images from a directory and its sub directories.
       
      public CeddExtractor ( String imagesPath){
            this.imagesPath = imagesPath;
      }
        
      public void getImagesFromFolder() {
         String currentFile;
         File folder = new File(imagesPath);
         File[] listOfFiles = folder.listFiles();
 
         for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()){
                    currentFile = listOfFiles[i].getName();
                    images.add(currentFile);
            }
         }
      }
      // extract Cedds from images and and create CeddDescriptors ArrayList  
      public void extractCedds () {
             // Iterating through images
            int i=0; // togliere
            for (Iterator<String> iterator = images.iterator(); iterator.hasNext(); ){
                String currentImageName = iterator.next();
                String imageFilePath = imagesPath + currentImageName;
                try {
                    BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
                    CEDD ceddObj = new CEDD();
                    ceddObj.extract(img);
                    String strCedd = ceddObj.getStringRepresentation();
                          
                    strCedd = i + " " + strCedd;
                    i++;
                    // replace string "cedd" with an appropriate label
                    strCedd = strCedd.replaceFirst("cedd ", ""); 
                    // append image name to the strCedd (using this name like an element ID)
                    strCedd = strCedd + " " + currentImageName;
                    
                    // erase the number 144 (given by Cedd extraction) and the space carachter
                    strCedd = strCedd.replaceFirst("144 ", "");
                    // add current cedd to the arraylist
                    CeddDescriptors.add(strCedd);
                    }
                 catch (Exception e) {
                    System.err.println("Error reading image or extracting CEDD.");
                    e.printStackTrace();
                 }
            }
      }
          
      // creating a txt file for the algorithm input      
        public String createInputFile (){          
            try{ 
                FileWriter fstream = new FileWriter(outputFileName);
                BufferedWriter out = new BufferedWriter(fstream);
            
                for (Iterator<String> ceddIterator = CeddDescriptors.iterator(); ceddIterator.hasNext(); ){
                    String singleCedd = ceddIterator.next(); 
                    out.write(singleCedd);
                    out.newLine();
                }          
                //Close the output stream
                out.close();
                
            }
            catch (Exception e){
                System.err.println("Error: " + e.getMessage());
            }
            return outputFileName;
        }
        // for dataset creation
        public ArrayList<String> getCeddDescriptors(){
            return this.CeddDescriptors;
        }
        
    }


