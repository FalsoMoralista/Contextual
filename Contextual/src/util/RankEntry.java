package util;

/**
 *
 */
import java.util.Arrays;

/**
 * @author luciano
 *
 */
public class RankEntry implements Comparable<RankEntry> {

    private String imgID;
    private double distanceTo;
    
    private int id;
    

    public RankEntry(int i, double get) {
        this.id = i;
        this.distanceTo = get;
    }

    public RankEntry(String imgID, double distanceTo) {
        super();
        this.imgID = imgID;
        this.distanceTo = distanceTo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }        

    public String getID() {
        return imgID;
    }

    public void setID(String id) {
        this.imgID = id;
    }

    public double getDistanceTo() {
        return distanceTo;
    }

    public void setDistanceTo(double distance) {
        this.distanceTo = distance;
    }

    @Override
    public String toString() {
        if(imgID != null)
            return imgID;
        else
            return Integer.toString(id);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        RankEntry entry = new RankEntry("TestImg", 0.0);

        System.out.println(entry.getID() + " to:" + entry.getDistanceTo());
        System.out.println(entry.toString());

        RankEntry[] entries = new RankEntry[3];
        entries[0] = new RankEntry("0", 0);
        entries[1] = new RankEntry("1", 0.86);
        entries[2] = new RankEntry("2", 0.56);
        System.out.println("");
        System.out.println("Testing entries ARRAY:");
        for (int i = 0; i < 3; i++) {
            System.out.println("------------------------------------------------------");
            System.out.println(entries[i].toString());
        }
        System.out.println("------------------------------------------------------");
        System.out.println("");

        System.out.println("Now testing SORT");
        Arrays.sort(entries);
        for (int i = 0; i < 3; i++) {
            System.out.println("------------------------------------------------------");
            System.out.println(entries[i].toString());
        }

    }

    @Override
    public int compareTo(RankEntry o) {
        // TODO Auto-generated method stub
        return Double.compare(distanceTo, o.getDistanceTo());
    }
}
