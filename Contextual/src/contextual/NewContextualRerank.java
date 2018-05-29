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
import util.Rank;

/**
 *
 * @author Luciano Araujo Dourado Filho
 */
public class NewContextualRerank {

    private final String DATA_DIRECTORY;
    private final String MAP_FILE;
    private final String EXT = ".distbin";
    private final int COLLECTION_SIZE;

    private Properties properties;
    private Properties clef;
    private Properties descriptors;

    public NewContextualRerank() throws FileNotFoundException, IOException {
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
        while (K < Ke) {
            contextualRerank(K++);
        }
    }

    private void contextualRerank(int K) throws IOException {
        for (int d = 0; d < 1/*descriptors.size()*/; d++) { // for each descriptor
            for (int l = COLLECTION_SIZE - 180; l < COLLECTION_SIZE; l++) { // for each topic
                double[] contextualDistances = new double[COLLECTION_SIZE];
                for (int i = 0; i < COLLECTION_SIZE; i++) { // for each imgI(collection)
                    if (i != l) {// discard itself                        
                        String currentDescriptor = descriptors.getProperty(Integer.toString(d));
                        String currentIMG = clef.getProperty(Integer.toString(i));
                        File f = new File(DATA_DIRECTORY + currentDescriptor + "/" + currentIMG + EXT);
                        Distbin distbin = new Distbin(COLLECTION_SIZE, f);
                        Rank rank = getRank(distbin);
                        int[] knn = buildKNN(K, l, rank);
                        int ck = 0;
                        double dj = 0;
                        for (int j : knn) {// for each imgJ((KNN)I) do : weighted sum of distance from imgI neighbors, to imgL												
                            System.out.println(j);
                            dj = dj + dist(j, l, d) * (K - ck);
                            ck += 1;
                        }

                    }
                }
            }
        }
    }

    private int dist(int img1, int img2, int d) {
        return 0;
    }
    
    
    /**
     * Build an image's KNN given its distbin and its amount of neighbors. 
     * Obs:
     * Both images that we are using to compute distances has to be filtered out
     * from the set, considering that we can't take into account the distances
     * to themselves. 
     * @param K the amount of neighbors.
     * @param filter the 
     * @param rank
     * @return 
     */
    public int[] buildKNN(int K, int filter, Rank rank) {
        
        int[] knn = new int[K];
        
        int pos = 0;
        int aux = 1;
        
        while(pos < K){
            int tmp = rank.get(aux).getId();
            if(tmp == filter){
                aux++;
            }
            knn[pos++] = rank.get(aux++).getId();
        }
        
        return knn;
    }

    public static void main(String[] args) throws IOException {
        NewContextualRerank rerank = new NewContextualRerank();
//        rerank.contextualRerank(2, 3);
        Distbin dist = new Distbin(20180, new File("/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/bic/18/18777.jpg.ppm.distbin"));
        int knn[] = rerank.buildKNN(3, 839, getRank(dist));
        System.out.println("KNN:");
        for(int i = 0; i < knn.length; i++){
            System.out.println(knn[i]);
        }
    }
}
