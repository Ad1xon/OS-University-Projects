import java.util.*;

public class PageReferenceGenerator {

    public static List<Integer> generate(int length, int totalPages, int localityWindow, int phaseLength) {
        Random rand = new Random();
        List<Integer> result = new ArrayList<>();

        while (result.size() < length) {

            int base = rand.nextInt(Math.max(1, totalPages - localityWindow));
            List<Integer> localPages = new ArrayList<>();
            for (int i = 0; i < localityWindow; i++) {
                localPages.add(base + i);
            }

            for (int i = 0; i < phaseLength && result.size() < length; i++) {
                result.add(localPages.get(rand.nextInt(localPages.size())));
            }
        }

        return result;
    }
}
