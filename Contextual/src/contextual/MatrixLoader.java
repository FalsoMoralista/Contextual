/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contextual;

import com.mindprod.ledatastream.LEDataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author Luciano Araujo Dourado Filho
 */
public class MatrixLoader {

    
    public MatrixLoader(){        
    }
            
    /**
     * Returns a double matrix.  
     * @param path
     * @param filePattern
     * @param rowNumber
     * @param columNumber
     * @return 
     * @throws java.io.FileNotFoundException
     */
    public static final double[][] matrixIO(String path,String filePattern, int rowNumber,int columNumber) throws FileNotFoundException, IOException{
        
        double[][] matrix = new double[rowNumber][columNumber]; 

        File[] distbins = getFiles(path,filePattern);
        
        int distCounter = 0;
        for(File f : distbins){

            FileInputStream fis = new FileInputStream(f);
            LEDataInputStream inStream = new LEDataInputStream(fis);

            double[] readyDistbin = readDistbin(columNumber, inStream);

            for(int i = 0; i < readyDistbin.length; i++){
                matrix[i][distCounter++] = readyDistbin[i];
            }
            
            fis.close();
            inStream.close();
            
        }
        return matrix;
    }
    
    private static File[] getFiles(String path, String filePattern){

        File fpath = new File(path);

        LinkedList<File> relevant = new LinkedList<>();

        File[]allFiles = fpath.listFiles();
        for(int i = 0; i < allFiles.length; i++){
            if(allFiles[i].getName().contains(filePattern)){
                if(!allFiles[i].getName().contains(".rank")){
                    relevant.add(allFiles[i]);
                }
            }
        }        

        File[] files = new File[relevant.size()];

        for(int i = 0; i < files.length; i++){
            files[i] = relevant.get(i);
        }

        return files;
    }
    
    public static double[] readDistbin(int size,LEDataInputStream inputStream) throws IOException{

        double[] arr = new double[size];

        for(int i =0; i < size; i++){            
            arr[i] = inputStream.readDouble();
            System.out.println(arr[i]);
        }
        
        return arr;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        MatrixLoader.readDistbin(20180, new LEDataInputStream(new FileInputStream(new File("/home/luciano/Desktop/ic/Rodrigo/descriptors/acc/ic08topics/31.jpg.ppm.distbin"))));
    }
}
