import java.util.List;

public interface PageReplacementAlgorithm {
    void simulate(List<Integer> referenceString);
    int getPageFaultCount();
    String getName();
    List<Boolean> getFaultHistory();

}
