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

/**
 *
 * @author Luciano Araujo Dourado Filho
 */
public class Distbin {

    double[] distbin;
    String name;
    
    public Distbin(int size, File f) throws FileNotFoundException, IOException {
        this.name = f.getName();
        FileInputStream fis = new FileInputStream(f);
        LEDataInputStream str = new LEDataInputStream(fis);
        distbin = MatrixLoader.readDistbin(size, str);
        fis.close();
        str.close();
    }

    /**
     *  Returns the current value for a given position. 
     * @param pos
     * @return 
     */
    public double get(int pos) {
        return distbin[pos];
    }

    /**
     *  Return the size. 
     */
    public int size(){
        return distbin.length;
    }
    
    /**
     *  Return the name. 
     * @return 
     */
    public String getName(){
        return name;
    }
    
    public static void main(String[] args) throws IOException {
        String path = "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/bic/ic08topics/";
        String filename = "4947.jpg.ppm.distbin";
        File f = new File(path+filename);
        Distbin d = new Distbin(20180, f);
        System.out.println(d.getName());
        System.out.println(d.get(20002));
    }
}
