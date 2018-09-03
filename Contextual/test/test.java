
import java.io.File;
import java.io.IOException;
import util.Distbin;
import util.Rank;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author luciano
 */
public class test {

    public static void main(String[] args) throws IOException {
        Distbin d = new Distbin(20000, new File("/home/luciano/ic/descritores/ccom/cs1_1/4947.jpg.ppm.distbin"));
        Rank r = new Rank(d);
        for (int i = 0; i < 10; i++) {
//            System.out.println(d.get(i));
            System.out.println(r.get(i).getDistanceTo());
        }
    }
}
