package time_measurer;

import kmeans.ClusteringAlgorithms;
import kmeans.VectorFloat;

/**
 *
 * @author soma
 */
public class TimeMeasurerSequential implements TimeMeasurer{
    @Override
    public int[] perform(int k, VectorFloat[] dataPoints, int iterations){
        return ClusteringAlgorithms.kmeans(k, dataPoints, iterations);
    }
}
