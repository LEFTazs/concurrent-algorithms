package usecases;

import java.util.Random;
import kmeans.ClusteringAlgorithms;
import kmeans.VectorFloat;
import kmeans_concurrent.ClusteringAlgorithmsConcurrent;

public class FakeDataClusterer {
    static final float maxRandomValue = 100F;
    static final int dataWidth = 500;
    static final int dataHeight = 500;
    static final int k = 5;
    static final int iterations = 1000;
    
    public static void cluster() {
        System.out.println("Creating random data . . .");
        Random random = new Random();
        VectorFloat[] data = new VectorFloat[dataWidth*dataHeight];
        int currentVectorId = 0;
        for (int h = 0; h < dataHeight; h++) {
            for (int w = 0; w < dataWidth; w++) {
                data[currentVectorId] = new VectorFloat(3);
                data[currentVectorId].set(0, random.nextFloat() * maxRandomValue);
                data[currentVectorId].set(1, random.nextFloat() * maxRandomValue);
                data[currentVectorId].set(2, random.nextFloat() * maxRandomValue);
                currentVectorId++;
            }
        }
        
        System.out.println("Clustering . . .");
        
        ClusteringAlgorithmsConcurrent.kmeans(k, data, iterations, 4);
    }
}
