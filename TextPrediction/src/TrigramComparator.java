import java.util.Comparator;

/**
 * Created by Chris on 24/02/2016.
 */
public class TrigramComparator implements Comparator<Trigram> {
    private int trigramCount;

    /*public int compareTo(Trigram compareTrigram) {
        return trigramCount.compareTo(compareTrigram.getTrigramCount());
    }*/



    @Override
    public int compare(Trigram o1, Trigram o2) {
        return o1.getTrigramCount() - o2.getTrigramCount();
    }
}
