package br.uefs.ecomp.contextual;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import com.mindprod.ledatastream.LEDataInputStream;
import com.mindprod.ledatastream.LEDataOutputStream;

/**
 * Gera os rankings ordenados das imagens dos descritores
 * @author luciano
 *
 */
public class LoadRank {

	private static final String CLEF_DIR = "src/br/unicamp/ic/lis/rfrunner/clef.map";
	private static final String DESCRIPTORS_DIR = "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/";				
	private static final String EXT = ".distbin";


	private static Properties clefDirectoryMap;
	private static Properties descriptorsMap;

	private static final int COLLECTION_SIZE = 20000;

	private RankEntry[] ranks = new RankEntry[COLLECTION_SIZE];


	private static double MEAN;
	private static double SD;

	public LoadRank() {

	}


	/**
	 *	Class runner 
	 */
	public void run() {

		loadDirectories();
		try {
			setUpDescriptorsMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				 normalizedImport();
//		writeDistbin();
	}


	private void setUpValues(String path) {

		try {
			FileReader reader = new FileReader(path+"mean");

			BufferedReader buffer = new BufferedReader(reader);

			MEAN = Double.parseDouble(buffer.readLine());
			buffer.close();

			reader = new FileReader(path+"sd");
			buffer = new BufferedReader(reader);
			SD = Double.parseDouble(buffer.readLine());
			buffer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 *	Loads up the collection map 
	 */
	private void loadDirectories() {

		LoadMap map = new LoadMap(CLEF_DIR);
		map.load();
		clefDirectoryMap = map.export();				
	}


	/**
	 *	Ordena um rank.
	 *	@param rank
	 */
	public static void sort(RankEntry[] rank) {
		Arrays.sort(rank);
	}

	/**
	 *	Normalize and import
	 */
	private void normalizedImport() {
		try {
			long time = System.currentTimeMillis();
			for(int x = 0; x < descriptorsMap.size()-1; x++) { // descriptor list
				String descriptor = descriptorsMap.getProperty(Integer.toString(x))+"/";
				for(int j = 0; j < COLLECTION_SIZE; j++) { // amount of images
					String property = clefDirectoryMap.getProperty(Integer.toString(j));
					String CAT = DESCRIPTORS_DIR+descriptor+property+EXT;
					FileInputStream fis = new FileInputStream(CAT);
//					DataInputStream lis = new DataInputStream(fis);// usar quando big ending
					LEDataInputStream lis = new LEDataInputStream(fis);		// usar quando little ending
					System.out.println("path: "+clefDirectoryMap.getProperty(Integer.toString(j)));
					for(int i = 0; i < COLLECTION_SIZE; i++){// distances
						double dist = lis.readDouble();
//						System.out.println(dist);
						//setUpValues(CAT);
						//double norm_dist = normalize(dist);
						ranks[i] = new RankEntry(Integer.toString(i),dist);
						
					}
					sort(ranks); // sort
					String sub = CAT.substring(0,CAT.length()-12);
					this.delete(CAT+".rank");
					write(ranks, sub); // write the array to the output file		
					System.out.println("["+"Arquivo escrito: "+j+"]");
				}				
				System.out.println("["+"Descritor pronto: "+descriptorsMap.getProperty(Integer.toString(x))+"]");
			}
			System.out.println("Tempo total:\t\t" + String.format("%.2f",(System.currentTimeMillis() - time)/1000.0/3600.0));			
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void write(RankEntry[]ranks, String path) {

		//		System.out.println(DIST_DIR+property+".rank");		
		File file; 
		file = new File(path+".rank");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for(int i =0; i < ranks.length; i++) {
				bw.write(ranks[i].getID()+"\n");
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

	public static void delete(String path) {
		File file = new File(path);
		if(file.exists()) {
			Boolean deleted = file.delete();
			if(!deleted) {
				System.out.println("NOT DELETED");
			}
		}
	}
	
	/**
	 *	Gets the descriptors list from file system 
	 * @throws IOException 
	 */
	public void setUpDescriptorsMap() throws IOException {

		String cat = DESCRIPTORS_DIR+"descriptors.map";
		File descriptorList = new File(cat);
		if(!descriptorList.exists()) {
			LinkedList<String> descriptors = new LinkedList();

			File f = new File(DESCRIPTORS_DIR);
			File[] files = f.listFiles();

			int count = 0;

			for(File x : files) {
				descriptors.add(Integer.toString(count)+"="+x.getName());
				count+= 1;
			}			

			Iterator i = descriptors.iterator();		

			File map = new File(cat);
			BufferedWriter writer = new BufferedWriter(new FileWriter(map));
			while(i.hasNext()) {
				writer.write((String)i.next()+"\n");
			}
			writer.close();			
		}
//		System.out.println(cat);
		LoadMap map = new LoadMap(cat);
		map.load();
		descriptorsMap = map.export();
	}

	private void writeDistbin() {
		for(int d = 1; d < this.descriptorsMap.size(); d++) {
			String dir = "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/";
			String descriptor = this.descriptorsMap.getProperty(Integer.toString(d)); // pega o nome do descritor			
			double []values = new double[clefDirectoryMap.size()];
			for(int img = 0; img < clefDirectoryMap.size(); img += 1) { // pega cada imagem do descritor 							
				try {
					String image = this.clefDirectoryMap.getProperty(Integer.toString(img));
					FileReader reader = new FileReader(dir+descriptor+"/"+image);// le os valores para escrever no novo arquivo
					BufferedReader buffer = new BufferedReader(reader);					
					for(int i = 0; i < values.length; i++) {
						values[i] = Double.parseDouble(buffer.readLine()); // le para o array
					}	
					
//					System.out.println(Arrays.toString(values));
					
					FileOutputStream fos = new FileOutputStream(dir+descriptor+"/"+image+".distbin");
					DataOutputStream dos = new DataOutputStream(fos);
									
					for(int i = 0; i < values.length; i += 1) {
						double doub = values[i];
//						System.out.println(doub);
						dos.writeDouble(doub); // escreve no arquivo
						
					}	
					dos.close();
					
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}				
	}

	public static void main(String[] args) throws IOException {
		LoadRank l = new LoadRank();		
		l.run();		
	}
}
