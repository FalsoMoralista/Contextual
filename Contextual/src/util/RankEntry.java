package br.uefs.ecomp.contextual;
/**
 * 
 */

import java.util.Arrays;

/**
 * @author luciano
 *
 */
public class RankEntry implements Comparable<RankEntry>{


	private String imgID;
	private double distanceTo;
	
	public String getID() {
		return imgID;
	}

	public void setID(String id) {
		this.imgID = id;
	}

	public double getDistanceTo() {
		return distanceTo;
	}

	public RankEntry(String imgPath, double distanceTo) {
		super();
		this.imgID = imgPath;
		this.distanceTo = distanceTo;
	}

	public void setDistanceTo(double distance) {
		this.distanceTo = distance;
	}

	
	@Override
	public String toString() {
		return  imgID;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RankEntry entry = new RankEntry("TestImg",0.0);
		
		System.out.println(entry.getID()+" to:"+entry.getDistanceTo());
		System.out.println(entry.toString());

		RankEntry[] entries = new RankEntry[3];
		entries[0] = new RankEntry("0",0);
		entries[1] = new RankEntry("1",0.86);
		entries[2] = new RankEntry("2",0.56);
		System.out.println("");
		System.out.println("Testing entries ARRAY:");
		for(int i=0; i< 3; i++) {
			System.out.println("------------------------------------------------------");
			System.out.println(entries[i].toString());
		}
		System.out.println("------------------------------------------------------");
		System.out.println("");
		
		System.out.println("Now testing SORT");
		Arrays.sort(entries);
		for(int i=0; i< 3; i++) {
			System.out.println("------------------------------------------------------");
			System.out.println(entries[i].toString());
		}
		
	}
	
	@Override
	public int compareTo(RankEntry o) {
		// TODO Auto-generated method stub
		return Double.compare(distanceTo, o.getDistanceTo());
	}}
