/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.mindprod.ledatastream.LEDataInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Used to load distances matrixes.
 * @author Luciano Araujo Dourado Filho
 */
public class MatrixLoader {

    public MatrixLoader() {
    }

    /**
     * Returns a double matrix.
     *
     * @param path
     * @param filePattern
     * @param rowNumber
     * @param columNumber
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static final double[][] matrixIO(String path, String filePattern, int rowNumber, int columNumber) throws FileNotFoundException, IOException {

        double[][] matrix = new double[rowNumber][columNumber];

        File[] distbins = getFiles(path, filePattern); // get all files from the path

        int distCounter = 0;

        for (File f : distbins) { // for each file

            FileInputStream fis = new FileInputStream(f);
            LEDataInputStream inStream = new LEDataInputStream(fis); // create a stream to it

            double[] readyDistbin = readDistbin(rowNumber, inStream); // input its values

            for (int i = 0; i < readyDistbin.length; i++) {
                matrix[i][distCounter] = readyDistbin[i];
            }
            
            distCounter++;

            fis.close();
            inStream.close();
        }

        return matrix;
    }

    /**
     * Return all files from a specific path, given a filter.
     *
     * @param path
     * @param filePattern
     * @return
     */
    public static File[] getFiles(String path, String filePattern) {

        File fpath = new File(path);

        LinkedList<File> relevant = new LinkedList<>();

        File[] allFiles = fpath.listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i].getName().contains(filePattern)) {
                relevant.add(allFiles[i]);
            }
        }

        File[] files = new File[relevant.size()];

        for (int i = 0; i < files.length; i++) {
            files[i] = relevant.get(i);
        }

        return files;
    }

    /**
     * Read as little ending.
     *
     * @param size
     * @param inputStream
     * @return
     * @throws java.io.IOException
     */
    public static double[] readDistbin(int size, LEDataInputStream inputStream) throws IOException {

        double[] arr = new double[size];

        for (int i = 0; i < size; i++) {
            arr[i] = inputStream.readDouble();
        }

        return arr;
    }

    /**
     * Read as big ending.
     *
     * @param size
     * @param inputStream
     * @return
     * @throws java.io.IOException
     */
    public static double[] readDistbin(int size, BufferedReader inputStream) throws IOException {

        double[] arr = new double[size];

        for (int i = 0; i < size; i++) {
            arr[i] = Double.parseDouble(inputStream.readLine());
        }

        return arr;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Example:");
        double[][] d = MatrixLoader.matrixIO("/home/luciano/Desktop/ic/Rodrigo/descriptors/acc/ic08topics", "jpg.ppm.distbin", 20180, 180);
        for (int i = 0; i < 20180; i++) {
            for (int j = 0; j < 180; j++) {
                System.out.printf("[" + "%f" + "] ", d[i][j]);
            }
            System.out.println("");
        }
    }
}
