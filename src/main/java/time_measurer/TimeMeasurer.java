package time_measurer;

import kmeans.VectorFloat;

/**
 *
 * @author soma
 */
public interface TimeMeasurer {
    public int[] perform(int k, VectorFloat[] dataPoints, int iterations);
}
