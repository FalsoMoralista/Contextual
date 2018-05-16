package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 	@author luciano
 *	Carrega um mapa com chave-valor para um objeto do tipo Properties
 */
public class LoadMap {
	
	private Properties properties;
	private static String DIR;
	
	public LoadMap(String s) {
		DIR = s;
	}
	
	public static void main(String[] agrs) {
		LoadMap m =  new LoadMap("src/br/unicamp/ic/lis/rfrunner/clef.map");
		m.load();
		System.out.println(m.export().getProperty("0"));
	}
	
	/**
	 * Carrega o mapa em properties.
	 * 
	 */ 
	public void load() {
		properties =  new Properties();
		try {
			properties.load(new FileInputStream(DIR)); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	/**
	 *	Exporta o mapa.
	 *	@return mapa
	 */
	public Properties export() {
		return properties; 	
	}
}
