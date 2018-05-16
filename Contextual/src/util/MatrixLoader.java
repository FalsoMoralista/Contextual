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
import java.util.Arrays;
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
     * Returns a double matrix (Read as little-endian).
     *
     * @param extension
     * @param distbins
     * @param rowNumber
     * @param columNumber
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static final double[][] LEMatrixIO(File[] distbins, String extension, int rowNumber, int columNumber) throws FileNotFoundException, IOException {

        double[][] matrix = new double[rowNumber][columNumber];

        int distCounter = 0;

        for (File f : distbins) { // for each file

            FileInputStream fis = new FileInputStream(new File(f.getPath() + extension));
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
     * Returns a double matrix (Read normally).
     *
     * @param extension
     * @param distbins
     * @param rowNumber
     * @param columNumber
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static final double[][] NormMatrixIO(File[] distbins, String extension, int rowNumber, int columNumber) throws FileNotFoundException, IOException {

        double[][] matrix = new double[rowNumber][columNumber];

        int distCounter = 0;

        for (File f : distbins) { // for each file
            String path = f.getPath();
            path = path.replaceAll(".ppm", ".cs_3");
            double[] readyDistbin = readDistbin(new File(path + extension)); // input its values

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

    public static double[][] LEMatrixIOFromMap() {
        return null;
    }

    /**
     * Writes a matrix column to a file.
     *
     * @param file
     * @param rows
     * @param column
     * @param matrix
     * @throws java.io.IOException
     */
    public static final void matrixOutput(File file, double[][] matrix, int rows, int column) throws IOException {

        File newFile = new File(file.getPath()+".distbin");

        FileOutputStream fos = new FileOutputStream(newFile);

        DataOutputStream str = new DataOutputStream(fos);

        LEDataOutputStream out = new LEDataOutputStream(str);

        for (int i = 0; i < rows; i++) {
            out.writeDouble(matrix[i][column]);
        }
        
        out.close();
        str.close();
        fos.close();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String descriptorPath = "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/bic/";
        String mapPath = "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/clef.map";
        File[] files = MatrixLoader.getFilesFromMap(descriptorPath, mapPath);
        files = Arrays.copyOfRange(files, 20000, 20180);
        System.out.println(files[0]);
//        double[][] d = MatrixLoader.NormMatrixIO(files, "", 20180, 180);
        double[][] d = MatrixLoader.LEMatrixIO(files, ".distbin", 20180, 180);
        System.out.println(d[20001][0]);
//        double[][] d = MatrixLoader.NormMatrixIO("/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/bic/ic08topics_cs3", "jpg.cs_3", 20180, 180);
//        for (int i = 0; i < 20180; i++) {
//            for (int j = 0; j < 180; j++) {
//                System.out.printf("[" + "%f" + "] ", d[i][j]);
//            }
//            System.out.println("");
//        }
    }
}
