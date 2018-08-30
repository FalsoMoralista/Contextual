/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contextual;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import util.Distbin;
import util.MatrixLoader;
import util.Rank;

/**
 *
 * @author Luciano Araujo Dourado Filho
 */
public class IterativeContextualRerank {

    private final String DATA_DIRECTORY;
    private final String MAP_FILE;
    private final String EXT = ".distbin";
    private final int COLLECTION_SIZE;

    private Properties properties;
    private Properties clef;
    private Properties descriptors;

    public IterativeContextualRerank() throws FileNotFoundException, IOException {
        properties = new Properties();
        clef = new Properties();
        descriptors = new Properties();

        File f = new File("src/resources/properties/contextual.properties");
        properties.load(new FileInputStream(f));

        DATA_DIRECTORY = properties.getProperty("DATA_DIRECTORY");
        COLLECTION_SIZE = Integer.parseInt(properties.getProperty("COLLECTION_SIZE"));
        MAP_FILE = properties.getProperty("MAP_FILE");

        f = new File("src/resources/maps/clef.map");
        clef.load(new FileInputStream(f));

        f = new File("src/resources/maps/descriptors.map");
        descriptors.load(new FileInputStream(f));
    }

    /**
     * Return the rank for a distbin.
     *
     * @param d
     * @return
     */
    public static Rank getRank(Distbin d) {
        return new Rank(d);
    }

    /**
     * Calculate contextual distances for the given K values. Obs: The starter
     * value associated to Ks has to be bigger than 2 and the ending value Ke
     * has to be lesser than (N-2) being N the size of the collection given the
     * fact that when new distances are computed and a set of K nearest
     * neighbors is built, two values are disregarded (two images that we are
     * using to compute contextual distances)
     *
     * @param Ks
     * @param Ke
     * @throws java.io.IOException
     */
    public void contextualRerank(int Ks, int Ke) throws IOException {
        int K = Ks;
        while (K <= Ke) {
            contextualRerank(K++);
        }
    }

    private void contextualRerank(int K) throws IOException {

        System.out.println("Executando com K = " + K);

        for (int d = 0; d < descriptors.size(); d++) { // for each descriptor

            System.out.println("Descritor :" + descriptors.getProperty(Integer.toString(d)));

            double[][] contextualMatrix = new double[COLLECTION_SIZE][180];

            for (int l = COLLECTION_SIZE - 180; l < COLLECTION_SIZE; l++) { // for each topic

                for (int i = 0; i < COLLECTION_SIZE - 180; i++) { // for each imgI(collection)

                    if (i != l) {// discard itself                        

                        String currentDescriptor = descriptors.getProperty(Integer.toString(d));

                        String currentIMG = clef.getProperty(Integer.toString(i));

                        File f = new File(DATA_DIRECTORY + currentDescriptor + "/" + currentIMG + EXT);

                        Distbin distbin = new Distbin(COLLECTION_SIZE - 180, f);

                        Rank rank = getRank(distbin);

                        int[] knn = buildKNN(K, l, rank);

                        int ck = 0;
                        double dj = 0;
                        for (int j : knn) {// for each imgJ((KNN)I) do : weighted sum of distance from imgI neighbors, to imgL												
                            dj = dj + dist(j, l, d) * (K - ck);
                            ck += 1;
                        }

                        double di = dist(i, l, d) / K;
                        dj = dj / (K * (K + 1) / 2);
                        di = Math.pow(di, 2);
                        dj = Math.pow(dj, 2);

                        contextualMatrix[i][l - 20000] = Math.sqrt(di + dj); // recalculate distance from imgI to imgL                        
                    }
                }
                System.out.println("Feito. Total: " + (l - 20000) + "/" + (COLLECTION_SIZE - 20000));

                String topicImg = clef.getProperty(Integer.toString(l));

                String currentDescriptor = descriptors.getProperty(Integer.toString(d));

                File nTopic = new File(DATA_DIRECTORY + currentDescriptor + "/" + topicImg + EXT);

                Distbin topic = new Distbin(COLLECTION_SIZE, nTopic);

                /**
                 * Copy the original distances from the 180 topics to the end of
                 * this new distbin.
                 */
                System.out.println("Fazendo apppend");
                for (int append = 20000; append < 20180; append++) {
                    contextualMatrix[append][l - 20000] = topic.get(append);
                }
                System.out.println("ok");
            }
            write(contextualMatrix, d, K);
        }
    }

    private void write(double[][] contextualMatrix, int descriptor, int K) throws IOException {

        System.out.println("Escrevendo arquivo");

        String descriptorName = descriptors.getProperty(Integer.toString(descriptor));

        String descriptorBasePath = DATA_DIRECTORY + descriptorName + "/";

        String folderName = "ic08topics_cs" + K + "/";

        File dir = new File(descriptorBasePath + "/" + folderName);

        dir.mkdir();

        for (int img = 20000; img < clef.size(); img++) {

            String currentImg = clef.getProperty(Integer.toString(img));

            currentImg = currentImg.replace("ic08topics", "");

            File name = new File(dir.getPath() + currentImg + "/");

            MatrixLoader.matrixOutput(name, contextualMatrix, 20180, img - 20000);
        }
    }

    private double dist(int img1, int img2, int descriptor) throws IOException {

        String currentDescriptor = descriptors.getProperty(Integer.toString(descriptor));

        String currentIMG = clef.getProperty(Integer.toString(img1));

        File distbin = new File(DATA_DIRECTORY + currentDescriptor + "/" + currentIMG + EXT);

        return MatrixLoader.getLine(distbin, img2);
    }

    /**
     * Build an image's KNN given its distbin and its amount of neighbors. Obs:
     * Both images that we are using to compute distances has to be filtered out
     * from the set, considering that we can't take into account the distances
     * to themselves.
     *
     * @param K the amount of neighbors.
     * @param filter the
     * @param rank
     * @return
     */
    public int[] buildKNN(int K, int filter, Rank rank) {

        int[] knn = new int[K];

        int pos = 0;
        int aux = 1;

        while (pos < K) {
            int tmp = rank.get(aux).getId();
            if (tmp == filter) {
                aux++;
            }
            knn[pos++] = rank.get(aux++).getId();
        }

        return knn;
    }

    public void buildWorkspace(int Ks, int Ke, int descriptor) throws IOException {
        System.out.println("Building workspace ...");
        
        String descriptorName = descriptors.getProperty(Integer.toString(descriptor));

        String path = DATA_DIRECTORY + descriptorName + '/' + "cs" + Ks + '_' + Ke;

        String absPath = DATA_DIRECTORY + descriptorName + '/';

        String csDir = '/' + "cs" + Ks + '_' + Ke;

        File f = new File(path);

        if (!f.exists()) {
            System.out.println("Creating file under "+ path);
            f.mkdir();
            cloneOrginalMatrix(absPath, csDir);
        }
    }
    
    private void cloneOrginalMatrix(String absolutePath, String csDirectory) throws IOException{

        System.out.println("Cloning original distances ...");
        
        System.out.println(absolutePath);

        System.out.println(csDirectory);
        
        String cmd[] = { absolutePath + "copy.sh", absolutePath + "ic08topics",csDirectory };

        Runtime.getRuntime().exec(cmd);
    }

    public static void main(String[] args) throws IOException {
        IterativeContextualRerank rerank = new IterativeContextualRerank();
        rerank.buildWorkspace(1, 1, 0);
    }
}
