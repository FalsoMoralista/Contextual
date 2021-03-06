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

        File f = new File("resources/properties/contextual.properties");
        properties.load(new FileInputStream(f));

        DATA_DIRECTORY = properties.getProperty("DATA_DIRECTORY");
        COLLECTION_SIZE = Integer.parseInt(properties.getProperty("COLLECTION_SIZE"));
        MAP_FILE = properties.getProperty("MAP_FILE");

        f = new File("resources/maps/clef.map");
        clef.load(new FileInputStream(f));

        f = new File("resources/maps/descriptors.map");
        descriptors.load(new FileInputStream(f));
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
     * @param descriptor
     * @throws java.io.IOException
     */
    public void contextualRerank(int Ks, int Ke, int descriptor) throws IOException {

        int K = Ks;

        System.out.println("Starting iterative contextual rerank parameters: Ks: " + '[' + Ks + ']' + " Ke: " + '[' + Ke + ']' + " Descriptor: " + descriptors.getProperty(Integer.toString(descriptor)));

        double topics[][] = new double[COLLECTION_SIZE][180];
        double collection[][] = new double[20000][20000];

        String dScriptor = descriptors.getProperty(Integer.toString(descriptor)); // get the descriptor passed by parameter

        System.out.println("Descriptor :" + dScriptor);

        File path = new File(DATA_DIRECTORY + dScriptor + '/');

        File path1 = new File("/home/luciano/ic/experimentos/cs_iterativo/" + dScriptor + '/' + Ks +"/"+dScriptor+"_cs"+Ks+'_'+Ks);

        loadMatrixes(topics, collection, path, path1, COLLECTION_SIZE, 180);
        File output = new File(DATA_DIRECTORY + dScriptor + '/');

        if (K > 1) {
            while (K != Ke) {
                --K;
                contextualRerank(descriptor, Ks, Ke, topics, collection, K);
                write(topics, output, Ks, K);
            }
        }

//        K = Ks;
//        while (K <= Ke) {
//            contextualRerank(descriptor, Ks, Ke, topics, collection, K);
//            write(topics, output, Ks, K);
//            K++;
//        }
    }

    /**
     * Load the distbins topics. The columns represent the images, the rows its
     * distances to other images.
     */
    private void loadMatrixes(double[][] topics, double[][] collection, File path, File path1, int amntRows, int amntColumns) throws IOException {
        System.out.println("Loading distbins...");

        System.out.println("Loading topics under: " + path1.getPath());

        int column = 0;
        for (int img = 20000; img < 20180; img++) {

            StringBuilder builder = new StringBuilder();

            String currentDistbin = clef.getProperty(Integer.toString(img));

            currentDistbin = currentDistbin.replaceAll("ic08topics", "");

            builder.append(path1.getPath() + '/' + currentDistbin + EXT);

            File distbin = new File(builder.toString());
            Distbin dist = new Distbin(20180, distbin);

            for (int row = 0; row < dist.size(); row++) {
                topics[row][column] = dist.get(row);
            }
            column++;
        }

        System.out.println("done (1/2)");

        System.out.println("Loading original matrix's path: " + path.getPath());

        column = 0;
        for (int img = 0; img < COLLECTION_SIZE - 180; img++) {

            StringBuilder builder = new StringBuilder();

            String currentDistbin = clef.getProperty(Integer.toString(img));

            builder.append(path.getPath() + '/' + currentDistbin + EXT);

            File distbin = new File(builder.toString());
            Distbin dist = new Distbin(20000, distbin);

            for (int row = 0; row < dist.size(); row++) {
                collection[row][column] = dist.get(row);
            }
            column++;
        }

        System.out.println("Done loading");
    }

    private void contextualRerank(int descriptor, int Ks, int Ke, double[][] topics, double collection[][], int K) throws IOException {

        System.out.println("Executing... K = " + K);

        String dScriptor = descriptors.getProperty(Integer.toString(descriptor)); // get the descriptor passed by parameter

        Rank[] rankings = new Rank[180];

        rank(rankings, topics); // sort ranked lists

        for (int l = COLLECTION_SIZE - 180; l < COLLECTION_SIZE; l++) { // for each topic (topics columns)

            for (int i = 0; i < COLLECTION_SIZE - 180; i++) { // for each imgI (topics rows)

                if (i != l) {// discard itself                        

                    String currentIMG = clef.getProperty(Integer.toString(i)); // get the current image

                    File f = new File(DATA_DIRECTORY + dScriptor + "/" + currentIMG + EXT);

                    Distbin distbin = new Distbin(COLLECTION_SIZE - 180, f);

                    Rank rank = rankings[l - 20000];

                    int[] knn = buildKNN(K, l, rank);

                    int ck = 0;
                    double dj = 0;
                    for (int j : knn) {// for each imgJ((KNN)L) do : weighted sum of distance from imgL neighbors, to imgI												
                        dj = dj + collection[i][j]/*dist(j, i, descriptor)*/ * (K - ck);
                        ck++;
                    }

                    double di = topics[i][l - 20000] / K;
                    dj = dj / (K * (K + 1) / 2);
                    di = Math.pow(di, 2);
                    dj = Math.pow(dj, 2);

                    topics[i][l - 20000] = Math.sqrt(di + dj); // recalculate distance from imgI to imgL                        
                }
            }
            System.out.println("Feito. Total: " + ((l - 20000) + 1) + "/" + (COLLECTION_SIZE - 20000));
        }
    }

    private void write(double[][] contextualMatrix, File path, int Ks, int Ke) throws IOException {

        System.out.println("Writing files...");

        String folderName = "cs" + Ks + '_' + Ke;

        File dir = new File(folderName);

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
            System.out.println("Creating file under " + path);
            f.mkdir();
            cloneOrginalMatrix(absPath, csDir);
        }
    }

    private void cloneOrginalMatrix(String absolutePath, String csDirectory) throws IOException {

        System.out.println("Cloning original distbins into workspace...");

        String cmd[] = {"resources/scripts/" + "copy.sh", absolutePath + "ic08topics", csDirectory};

        Runtime.getRuntime().exec(cmd);
    }

    private void rank(Rank[] rank, double[][] topics) {

        for (int column = 0; column < 180; column++) {

            double[] values = new double[20000];

            for (int row = 0; row < 20000; row++) {
                values[row] = topics[row][column];
            }

            Distbin d = new Distbin(values);
            Rank r = new Rank(d);
            rank[column] = r;
        }
    }

    public static void main(String[] args) throws IOException {
        IterativeContextualRerank rerank = new IterativeContextualRerank();
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("arg0 = Ks");
            System.out.println("arg1 = Ke");
            System.out.println("arg2 = descriptor id");
        } else {
            int arg0 = Integer.parseInt(args[0]);
            int arg1 = Integer.parseInt(args[1]);
            int arg2 = Integer.parseInt(args[2]);
            rerank.contextualRerank(arg0, arg1, arg2);
        }
    }
}
