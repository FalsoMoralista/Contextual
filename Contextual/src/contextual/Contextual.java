/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contextual;

import java.io.File;
import java.io.IOException;
import util.Distbin;

/**
 *
 * @author Luciano Araujo Dourado Filho
 */
public class Contextual {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        System.out.println("teste");

        File f = new File("/home/luciano/Desktop/ic/Rodrigo/descriptors/las/ic08topics_cs2/4947.jpg.ppm.distbin");

        Distbin d1 = new Distbin(20180, f);

        f = new File("/home/luciano/Desktop/ic/Rodrigo/descriptors/bic/ic08topics/4947.jpg.ppm.distbin");

        Distbin d2 = new Distbin(20180, f);

        System.out.println("Original na posicao 0:" + d1.get(20002));
        System.out.println(d2.get(20002));
    }

}
