package kmeans;

import java.util.Random;

public class ClusteringAlgorithms {
    
    protected static VectorFloat[] dataPoints;
    protected static VectorFloat[] clusterCenters;
    protected static int[] clusters;
    protected static int numberOfDimensions;
    protected static int K;

    public static int[] kmeans(int k, VectorFloat[] dataPoints, int iterations) {
        ClusteringAlgorithms.dataPoints = dataPoints;
        ClusteringAlgorithms.K = k;
        ClusteringAlgorithms.numberOfDimensions = dataPoints[0].size();
        
        checkInputDataSizeValidity();
        
        generateRandomClusterCenters();
        
        for (int iteration = 0; iteration < iterations; iteration++) {
            adjustClusters();
        }
        
        return clusters;
    }
    
    protected static void checkInputDataSizeValidity() {
        for (int i = 1; i < numberOfDimensions; i++) {
            if (dataPoints[i].size() != numberOfDimensions) {
                throw new IllegalArgumentException("Input dimensions do not match!");
            }
        }
    }
    
    protected static void generateRandomClusterCenters() {
        Random random = new Random();
        clusterCenters = new VectorFloat[K];
        for (int i = 0; i < K; i++) {
            int chosenId = random.nextInt(dataPoints.length);
            VectorFloat vectorToCopy = dataPoints[chosenId];
            clusterCenters[i] = new VectorFloat(vectorToCopy);
        }
    }
    
    protected static void adjustClusters() {
        clusters = new int[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {
            double[] distances = dataPoints[i].distancesFrom(clusterCenters);
            clusters[i] = getIndexOfSmallestDistance(distances);
        }
        calculateClusterCenters(clusters);
    }
    
    protected static int getIndexOfSmallestDistance(double[] distances) {
        int minimumDistanceId = 0;
        for (int j = 1; j < distances.length; j++) {
            if (distances[j] < distances[minimumDistanceId]) {
                minimumDistanceId = j;
            }
        }
        return minimumDistanceId;
    }
    
    protected static void calculateClusterCenters(int[] clusters) {
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < numberOfDimensions; j++) {
                float mean = 0;
                int count = 0;
                for (int k = 0; k < dataPoints.length; k++) {
                    if (clusters[k] == i) {
                        mean += dataPoints[k].get(j);
                        count++;
                    }
                }
                mean /= count;
                clusterCenters[i].set(j, mean);
            }
        }
    }
    
}