package br.uefs.ecomp.contextual;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import com.mindprod.ledatastream.LEDataOutputStream;

public class DistbinGenerator {
	
	private static LEDataOutputStream outputStream;
	private static DataInputStream inputStream;
	
	
	public DistbinGenerator() {
	}
	
	public static void generate(BufferedReader reader,String filePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath+".ppm.distbin");
		DataOutputStream dos = new DataOutputStream(fos);
		LEDataOutputStream out = new LEDataOutputStream(dos);
		for(int i = 0; i < 20000; i++) {
			out.write(reader.readLine().getBytes());
		}
		fos.close();
		dos.close();
		out.close();
	}
	
	public static void main(String[] args) {
		File[] files = new File("/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/bic/ic08topics_cs3").listFiles();
		List<String> distbins = new LinkedList<>();
		int count = 0;
		for(File f : files) {
			if(f.getName().contains(".cs")) {
				distbins.add(f.getPath());
			}
		}
		distbins.forEach(d ->{
			try {
				FileReader reader = new FileReader(d);
				BufferedReader br = new BufferedReader(reader);
				String sub = d.substring(0,d.indexOf("g")+1);
				sub = sub.substring(sub.lastIndexOf("/")+1,sub.length());
				DistbinGenerator.generate(br, "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/bic/ic08topics_cs3/"+sub);
				reader.close();
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		});		
	}
}
