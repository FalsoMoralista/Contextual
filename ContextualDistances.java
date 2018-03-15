package br.uefs.ecomp.contextual;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import com.mindprod.ledatastream.LEDataInputStream;
import com.sun.javafx.scene.control.skin.ListCellSkin;

public class ContextualDistances {

	private static String DESCRIPTORS_MAP;
	private static String COLLECTION_MAP;

	private static final String EXTENSION = ".distbin";

	private static Properties descriptorProperties;
	private static Properties collectionProperties;

	private static final int COLLECTION_SIZE = 20000;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		ContextualDistances calc = new ContextualDistances("/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/", "src/br/unicamp/ic/lis/rfrunner/clef.map");			
		calc.run();
		calc.calculateContext(3, 3);
		System.out.println("ok");
	}


	public ContextualDistances(String descriptorsDir, String collectionDir) {
		this.DESCRIPTORS_MAP = descriptorsDir+"descriptors.map";
		this.COLLECTION_MAP = collectionDir;
	}

	/**
	 *	Runs class 
	 */
	public void run() {
		setUp();
	}

	/**
	 * Build an image's KNN given its path and its amount of neighbors.
	 * @param imgRankPath the path from an image rank
	 * @param K its amount of neighbors
	 * @return an array with the ID's from an image's KNN in decreasing order
	 */
	private int[] buildKNN(String imgRankPath, int K) {
		int[] knn = new int[K];
		try {
			FileReader reader = new FileReader(imgRankPath);
			BufferedReader buffer =  new BufferedReader(reader);
			buffer.readLine(); // discard the distance to itself
			for(int i =0; i < knn.length; i+= 1) {
				knn[i] = Integer.parseInt(buffer.readLine());
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
	 *	Generate a reverse map for a given properties map. 
	 */
	public static void reverseMap(Properties p, String collectionDir) {	
		File file = new File(collectionDir+".rmap");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for(int j =0; j < p.size(); j++) {
				bw.write(p.getProperty(Integer.toString(j))+"="+j+"\n");
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
	 *	Calculate the contextual distances for the given K values. 
	 */
	public void calculateContext(int Ks, int Ke) {
		int K = Ks;
		while(K <= Ke) {
			contextualRerank(K++);
		}
	}

	/**
	 *	Calculate new distances utilizing contextual re-rank.
	 *	TODO write the output array to the respectively path 
	 */
	private void contextualRerank(int K) {
		String descriptorPath = this.DESCRIPTORS_MAP.substring(0,this.DESCRIPTORS_MAP.length()-16); // descriptor path		
		long time = System.currentTimeMillis();
		for(int d = 0; d < this.descriptorProperties.size()-1; d+=1) { // for each descriptor D
			String descriptor = descriptorProperties.getProperty(Integer.toString(d));// gets the descriptor name
			for(int l = COLLECTION_SIZE; l < COLLECTION_SIZE + 180; l += 1) { // for each imgL(topics) do:
				double[] contextualDistances = new double[COLLECTION_SIZE];// collection size			
				String imgL = descriptorPath+"/"+descriptor+"/"+collectionProperties.getProperty(Integer.toString(l))+EXTENSION; // get imgL's path				
				System.out.printf("Calculando contexto%n");
				for(int i = 0; i < COLLECTION_SIZE; i++) { // for each imgI(collection) do: compute distance between L & I using contextual information
					if(i!= l) {
						String imgI = descriptorPath+"/"+descriptor+"/"+collectionProperties.getProperty(Integer.toString(i))+EXTENSION; // get imgI's path
						int[] knn = buildKNN(imgL+".rank",K); // build imgI's KNN
						int ck = 0;
						double dj = 0;
						for(int j : knn) {// for each imgJ((KNN)I) do : weighted sum of distance from imgI neighbors, to imgL												
							dj = dj + dist(j,i,d) * (K - ck);
							ck += 1;
						}
						double di = dist(i,l,d) / K; 
						dj = dj / (K * (K - 1) / 2);
						di = Math.pow(di, 2);
						dj = Math.pow(dj,2);
						contextualDistances[i] = Math.sqrt(di+dj); // recalculate distance from imgI to imgL
					}
				}
				String path = imgL.substring(0, imgL.length()-12)+".cs_"+Integer.toString(K);
				System.out.println("Escrevendo arquivo");
				System.out.println("feito");
				write(contextualDistances,path);
			}
			System.out.println("["+"Descritor pronto: "+descriptorProperties.getProperty(Integer.toString(d))+"]");
		}
		
		System.out.println("Tempo total:\t\t" + String.format("%.2f",(System.currentTimeMillis() - time)/1000.0/3600.0));			
	}

	/**
	 *	Returns the distance between two images for a given descriptor:
	 *	Opens the img1 rank in the corresponding position to distance for img2 and reads it.	 
	 *	TODO refactor verify if changing between reading file libraries is really necessary.
	 */
	private static double dist(int img1, int img2, int descriptor) {
		String descriptorPath = DESCRIPTORS_MAP.substring(0,DESCRIPTORS_MAP.length()-16); // descriptor path		
		String descriptorName = descriptorProperties.getProperty(Integer.toString(descriptor));
		double val  = 0;			
		try {
			String imgJ = descriptorPath+"/"+descriptorName+"/"+collectionProperties.getProperty(Integer.toString(img1));
			FileInputStream fis = new FileInputStream(imgJ+EXTENSION); // load the rank from imgJ
			fis.skip(8*img2);
			//			DataInputStream lis = new DataInputStream(fis);
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
	 *	Write an double array to a respective path.
	 *	TODO write the array
	 */
	private static void write(double[] distances, String path) {

		File file; 
		file = new File(path);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for(int i = 0; i < distances.length; i+= 1) {
				bw.write(distances[i]+"\n");
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
	 *	Sets up maps 
	 *	TODO refactor, LoadMap class is not needed. 
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
