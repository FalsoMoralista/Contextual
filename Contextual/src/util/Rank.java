/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Set up a distbin's rank.
 *
 * @author Luciano Araujo Dourado Filho
 */
public class Rank {

    private LinkedList<RankEntry> rank;

    public Rank(Distbin d) {
        this.rank = new LinkedList<>();
        for (int i = 0; i < d.size(); i++) {
            RankEntry r = new RankEntry(i, d.get(i));
            rank.add(r);
        }
        Collections.sort(rank);
        System.out.println(rank.get(2).getId());
        System.out.println(rank.get(0).getDistanceTo());
    }

    public static void main(String[] args) throws IOException {
        String path = "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/bic/18/";
        String filename = "18777.jpg.ppm.distbin";
        File f = new File(path + filename);
        Distbin d = new Distbin(20180, f);
        
        Rank r = new Rank(d);
    }
}
