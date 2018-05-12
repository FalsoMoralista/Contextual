/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.mindprod.ledatastream.LEDataInputStream;
import com.mindprod.ledatastream.LEDataOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Used to load distances matrixes.
 *
 * @author Luciano Araujo Dourado Filho
 */
public class MatrixLoader {

    public MatrixLoader() {
    }

    /**
     * Returns a double matrix (Read as little-endian).
     *
     * @param descriptorPath
     * @param filter
     * @param rowNumber
     * @param columNumber
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static final double[][] LEMatrixIO(String descriptorPath, String filter, int rowNumber, int columNumber) throws FileNotFoundException, IOException {

        double[][] matrix = new double[rowNumber][columNumber];

        File[] distbins = getFiles(descriptorPath, filter); // get all files from the path

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
     * Returns a double matrix (Read normally).
     *
     * @param descriptorPath
     * @param filter
     * @param rowNumber
     * @param columNumber
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static final double[][] NormMatrixIO(String descriptorPath, String filter, int rowNumber, int columNumber) throws FileNotFoundException, IOException {

        double[][] matrix = new double[rowNumber][columNumber];

        File[] distbins = getFiles(descriptorPath, filter); // get all files from the path

        int distCounter = 0;

        for (File f : distbins) { // for each file
            double[] readyDistbin = readDistbin(f); // input its values

            for (int i = 0; i < readyDistbin.length; i++) {
                matrix[i][distCounter] = readyDistbin[i];
            }

            distCounter++;
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

    public static File[] getFilesFromMap(String descriptorPath, String mapPath) throws FileNotFoundException, IOException {

        Properties map = new Properties();

        File f = new File(mapPath);

        FileInputStream getMap = new FileInputStream(f);

        map.load(getMap);

        File[] files = new File[map.size()];

        for (int i = 0; i < map.size(); i++) {
            String CAT = descriptorPath + map.getProperty(Integer.toString(i));
            File file = new File(CAT);
            files[i] = file;
        }

        getMap.close();

        return files;
    }

    /**
     * Read as little-endian.
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
     * Read normally.
     *
     * @param f
     * @return
     * @throws java.io.IOException
     */
    public static double[] readDistbin(File f) throws IOException {

        Path path = Paths.get(f.getPath());

        Stream<String> lines = Files.lines(path);

        Object[] allLines = lines.toArray();

        double[] arr = new double[allLines.length];

        for (int i = 0; i < allLines.length; i++) {
            arr[i] = Double.parseDouble((String) allLines[i]);
        }

        return arr;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Example:");
//        double[] dist = readDistbin(20000, new LEDataInputStream(new FileInputStream(new File("/home/luciano/Desktop/ic/Rodrigo/descriptors/acc/ic08topics_cs2/31.jpg.ppm.distbin"))));
//        for(int i = 0; i < dist.length; i++){
//            System.out.println(dist[i]);
//        }
//
//        double[] distbin = readDistbin(new File("/home/luciano/Desktop/ic/Rodrigo/descriptors/acc/ic08topics_cs2/31.jpg.cs_2"));
//        LEDataOutputStream out = new LEDataOutputStream(new DataOutputStream(new FileOutputStream(new File("/home/luciano/Desktop/ic/Rodrigo/descriptors/acc/ic08topics_cs2/31.jpg.ppm.distbin"))));
//        for(int i = 0; i < distbin.length; i++){
//            out.writeDouble(distbin[i]);
//        }        

        double[][] d = MatrixLoader.NormMatrixIO("/home/luciano/Desktop/ic/Rodrigo/descriptors/acc/ic08topics_cs2", "jpg.cs_2", 20180, 180);
        for (int i = 0; i < 20180; i++) {
            for (int j = 0; j < 180; j++) {
                System.out.printf("[" + "%f" + "] ", d[i][j]);
            }
            System.out.println("");
        }
    }
}
