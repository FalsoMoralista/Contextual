/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.mindprod.ledatastream.LEDataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import static util.matrixAppender.append;
import static util.matrixAppender.getFromDistbin;
import static util.matrixAppender.getFromTxT;

/**
 *
 * @author Luciano Araujo Dourado Filho
 */
public class DistbinManager {

    public static File[] getFiles(String mainPath, Properties map) {
        File[] file = new File[map.size()];

        for (int i = 0; i < file.length; i++) {
            file[i] = new File(mainPath + map.getProperty(Integer.toString(i)));
        }

        return file;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        //########################### SET UP ###################################

        String baseDirectory = "/home/luciano/Desktop/ic/Rodrigo/descriptors/";

        Properties descriptors = new Properties();

        File file = new File(baseDirectory + "descriptors.map");

        descriptors.load(new FileInputStream(file));

        File[] descriptorPath = DistbinManager.getFiles(baseDirectory, descriptors);

        String[] contextualMapsPath = {
            baseDirectory + "ic08topics_cs2.map",
            baseDirectory + "ic08topics_cs3.map",
            baseDirectory + "ic08topics_cs4.map",
            baseDirectory + "ic08topics_cs5.map",
            baseDirectory + "ic08topics_cs6.map",
            baseDirectory + "ic08topics_cs7.map",
            baseDirectory + "ic08topics_cs8.map",
            baseDirectory + "ic08topics_cs9.map",
            baseDirectory + "ic08topics_cs10.map",};
        //######################################################################

        String mapPath = baseDirectory + "clef.map";
        
        for (int i = 0; i < descriptors.size(); i++) { // para cada descritor:

            File[] topics = MatrixLoader.getFilesFromMap(descriptorPath[i].getPath() + '/', mapPath); // liste todos seus arquivos a partir do mapa

            topics = Arrays.copyOfRange(topics, 20000, 20180); // filtre apenas os topicos (180)

            double[][] distbins = getFromDistbin(topics, ".distbin", 20180, 180); // carregue a matriz desses topicos para a memoria

            for (int c = 0; c < contextualMapsPath.length; c++) { // para cada matriz de contexto

                File[] files = MatrixLoader.getFilesFromMap(descriptorPath[i].getPath() + "/", contextualMapsPath[c]); // liste todos os arquivos do mapa

                files = Arrays.copyOfRange(files, 20000, 20180);// filtre apenas os topicos
                double[][] contextual = matrixAppender.getFromTxT(files, Integer.toString(c + 2), "", 20180, 180); // carrega uma matriz de contexto                         

                double[][] appended = matrixAppender.append(distbins, contextual, 20000, 20180, 0, 180); // faz um append dos topics da matriz original na matriz de contexto

                for (int f = 0; f < files.length; f++) {
                    MatrixLoader.matrixOutput(files[f], appended, 20180, i);// escreve os arquivos em formato distbin                    
                }

            }
        }

    }
}
