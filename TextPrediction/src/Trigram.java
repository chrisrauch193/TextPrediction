/**
 * Created by Chris on 23/02/2016.
 */
public class Trigram {
    private static int noOfTrigrams = 0;
    private String trigram = "";
    private int trigramCount = 0;

    public Trigram(String trigram) {
        this.trigram = trigram;
        trigramCount++;
        noOfTrigrams++;
    }

    public String getTrigram() {
        return trigram;
    }

    public void setTrigram(String trigram) {
        this.trigram = trigram;
    }

    public static int getNoOfTrigrams() {
        return noOfTrigrams;
    }

    public static void incrementNoOfTrigrams() {
        noOfTrigrams++;
    }

    public int getTrigramCount() {
        return trigramCount;
    }

    public void incrementTrigramCount() {
        this.trigramCount++;
    }

    public int compareTo(Trigram trigram)
    {
        return(trigram.trigramCount);
    }
}
