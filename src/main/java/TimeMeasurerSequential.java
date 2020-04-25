/**
 *
 * @author soma
 */
public class TimeMeasurerSequential implements TimeMeasurer{
    public int[] perform(int k, VectorFloat[] dataPoints, int iterations){
        return ClusteringAlgorithms.kmeans(k, dataPoints, iterations);
    }
}
