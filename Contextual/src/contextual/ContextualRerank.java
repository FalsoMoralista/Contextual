package contextual;

import util.LoadMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.mindprod.ledatastream.LEDataInputStream;
import java.util.Arrays;

public class ContextualRerank {

    private static String DESCRIPTORS_MAP;
    private static String COLLECTION_MAP;

    private static final String EXTENSION = ".distbin";

    private static Properties descriptorProperties;
    private static Properties collectionProperties;

    private static final int COLLECTION_SIZE = 20000;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        ContextualRerank calc = new ContextualRerank("/home/luciano/Desktop/ic/Rodrigo/descriptors/", "src/resources/clef.map");
        calc.run();
        long time = System.currentTimeMillis();
        calc.calculateContext(2, 2);
        System.out.println("Tempo total:\t\t" + String.format("%.2f", (System.currentTimeMillis() - time) / 1000.0 / 3600.0));
        System.out.println("ok");
    }

    public ContextualRerank(String descriptorsDir, String collectionDir) {
        this.DESCRIPTORS_MAP = descriptorsDir + "descriptors.map";
        this.COLLECTION_MAP = collectionDir;
    }

    /**
     * Runs class
     */
    public void run() {
        setUp();
    }

    /**
     * Build an image's KNN given its path and its amount of neighbors. Obs:
     * Both images that we are using to compute distances has to be filtered out
     * from the set, considering that we can't take into account the distances
     * to themselves.
     *
     * @param imgRankPath the path from an image rank
     * @param K its amount of neighbors
     * @return an array with the ID's from an image's KNN in decreasing order
     */
    private int[] buildKNN(String imgRankPath, int K, int filter) {
        int[] knn = new int[K];
        try {
            FileReader reader = new FileReader(imgRankPath);
            BufferedReader buffer = new BufferedReader(reader);
            buffer.readLine(); // discard the distance to itself
            for (int i = 0; i < knn.length; i += 1) {
                int n = Integer.parseInt(buffer.readLine());
                if (n == filter) {
                    n = Integer.parseInt(buffer.readLine());
                }
                knn[i] = n;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return knn;
    }

    /**
     * Generate a reverse map for a given properties map.
     */
    public static void reverseMap(Properties p, String collectionDir) {
        File file = new File(collectionDir + ".rmap");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (int j = 0; j < p.size(); j++) {
                bw.write(p.getProperty(Integer.toString(j)) + "=" + j + "\n");
            }
            bw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Calculate contextual distances for the given K values. Obs: The starter
     * value associated to Ks has to be bigger than 2 and the ending value Ke
     * has to be lesser than (N-2) being N the size of the collection given the
     * fact that when new distances are computed and a set of K nearest
     * neighbors is built, two values are disregarded (two images that we are
     * using to compute contextual distances)
     * @param Ks
     * @param Ke
     */
    public void calculateContext(int Ks, int Ke) {
        int K = Ks;
        while (K <= Ke) {
            contextualRerank(K++);
        }
    }

    /**
     * Calculate new distances utilizing contextual re-rank.
     */
    private void contextualRerank(int K) {
        String descriptorPath = this.DESCRIPTORS_MAP.substring(0, this.DESCRIPTORS_MAP.length() - 16); // descriptor path		
        for (int d = 1; d < this.descriptorProperties.size() - 10; d += 1) { // for each descriptor D
            String descriptor = descriptorProperties.getProperty(Integer.toString(d));// gets the descriptor name
            System.out.println(descriptor);
            System.out.printf("Calculando contextos usando K = %d %n", K);
            for (int l = 20117; l < 20118; l += 1) { // for each imgL(topics) do:
                double[] contextualDistances = new double[COLLECTION_SIZE];// collection size			
                String imgL = descriptorPath + "/" + descriptor + "/" + collectionProperties.getProperty(Integer.toString(l)) + EXTENSION; // get imgL's path				
                System.out.println(imgL);
                for (int i = 0; i < COLLECTION_SIZE; i++) { // for each imgI(collection) do: compute distance between L & I using contextual information
                    if (i != l) {
                        String imgI = descriptorPath + "/" + descriptor + "/" + collectionProperties.getProperty(Integer.toString(i)) + EXTENSION; // get imgI's path
                        System.out.println(imgI);
                        int[] knn = buildKNN(imgI.substring(0, imgI.length() - 12) + ".rank", K, l); // build imgI's KNN
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
                        contextualDistances[i] = Math.sqrt(di + dj); // recalculate distance from imgI to imgL
                        System.out.println(contextualDistances[i]);
                    }
                }
                String path = imgL.substring(0, imgL.length() - 12) + ".cs_" + Integer.toString(K);
//				write(contextualDistances,path);
                if (l % 10 == 0) // show the progress in a scale of 10 
                {
                    System.out.println("[" + "Distancias recalculadas: " + (l - COLLECTION_SIZE) + "/" + 180 + ", descritor " + d + "/" + descriptorProperties.size() + "]");
                }
            }
            System.out.println("[" + "Descritor pronto: " + descriptorProperties.getProperty(Integer.toString(d)) + "]");
        }
    }

    /**
     * Returns the distance between two images for a given descriptor: Opens the
     * img1 rank in the corresponding position to distance for img2 and reads
     * it. TODO refactor verify if changing between reading file libraries is
     * really necessary.
     */
    private static double dist(int img1, int img2, int descriptor) {
        String descriptorPath = DESCRIPTORS_MAP.substring(0, DESCRIPTORS_MAP.length() - 16); // descriptor path		
        String descriptorName = descriptorProperties.getProperty(Integer.toString(descriptor));
        double val = 0;
        try {
            String imgJ = descriptorPath + "/" + descriptorName + "/" + collectionProperties.getProperty(Integer.toString(img1));
            FileInputStream fis = new FileInputStream(imgJ + EXTENSION); // load the rank from imgJ
            fis.skip(8 * img2);
//						DataInputStream lis = new DataInputStream(fis);
            LEDataInputStream lis = new LEDataInputStream(fis); // usar caso little ending
            val = lis.readDouble();
            lis.close();
            fis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return val;
    }

    /**
     * Write an double array to a respective path. TODO write the array
     */
    private static void write(double[] distances, String path) {

        File file;
        file = new File(path);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < distances.length; i += 1) {
                bw.write(distances[i] + "\n");
            }
            bw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Sets up maps TODO refactor, LoadMap class is not needed.
     */
    private void setUp() {
        LoadMap load = new LoadMap(this.DESCRIPTORS_MAP); // loads a map containing the descriptors
        load.load();
        descriptorProperties = load.export();
        load = new LoadMap(this.COLLECTION_MAP); // loads a map containing the collection
        load.load();
        collectionProperties = load.export();
    }
}
