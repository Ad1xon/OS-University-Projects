import java.util.*;

public class ReferenceGenerator {
    public static List<Integer> generateReferenceString(int length, int base, int range) {
        Random rand = new Random();
        List<Integer> refs = new ArrayList<>();
        int localitySize = Math.max(5, range / 8);
        int currentLocality = 0;

        for (int i = 0; i < length; i++) {
            if (i % 50 == 0) {
                currentLocality = rand.nextInt(Math.max(1, range - localitySize + 1));
            }

            if (rand.nextDouble() < 0.95) {
                refs.add(base + currentLocality + rand.nextInt(localitySize));
            } else {
                refs.add(base + rand.nextInt(range));
            }
        }

        return refs;
    }
}
