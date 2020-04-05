import java.util.Random;
import processing.core.PApplet;
import processing.data.FloatList;

public class ClusteringAlgorithms {
    
    private static VectorFloat[] data;
    private static VectorFloat[] clusterCenters;
    private static int[] clusters;
    private static int sizeOfData;
    private static int numberOfDimensions;
    private static int numberOfClusters;

    public static int[] kmeans(int k, VectorFloat[] data, int iterations) {
        ClusteringAlgorithms.data = data;
        ClusteringAlgorithms.numberOfClusters = k;
        ClusteringAlgorithms.sizeOfData = data.length;
        ClusteringAlgorithms.numberOfDimensions = data[0].size();
        
        checkInputDataSizeValidity();
        
        generateRandomClusterCenters();
        
        for (int iteration = 0; iteration < iterations; iteration++) {
            adjustClusters();
        }
        
        return clusters;
    }
    
    private static void checkInputDataSizeValidity() {
        for (int i = 1; i < numberOfDimensions; i++) {
            if (data[i].size() != numberOfDimensions) {
                throw new IllegalArgumentException("Input dimensions do not match!");
            }
        }
    }
    
    private static void generateRandomClusterCenters() {
        Random random = new Random();
        clusterCenters = new VectorFloat[numberOfClusters];
        for (int i = 0; i < numberOfClusters; i++) {
            int chosenId = random.nextInt(sizeOfData);
            VectorFloat vectorToCopy = data[chosenId];
            clusterCenters[i] = new VectorFloat(vectorToCopy);
        }
    }
    
    private static void adjustClusters() {
        clusters = new int[sizeOfData];
        for (int i = 0; i < sizeOfData; i++) {
            FloatList distances = calculateDataDistanceFromClusterCenters(i);
            chooseClasterForOneData(distances, i);
        }
        calculateClusterCenters(clusters);
    }
    
    private static FloatList calculateDataDistanceFromClusterCenters(int i) {
        FloatList distances = new FloatList();
        for (int j = 0; j < numberOfClusters; j++) {
            double distance = data[i].distance(clusterCenters[j]);
            distances.append((float) distance);
        }
        return distances;
    }
    
    private static void chooseClasterForOneData(FloatList distances, int i) {
        for (int j = 0; j < distances.size(); j++) {
            if (distances.get(j) == distances.min()) {
                clusters[i] = j;
                return;
            }
        }
    }
    
    private static void calculateClusterCenters(int[] clusters) {
        for (int i = 0; i < numberOfClusters; i++) {
            for (int j = 0; j < numberOfDimensions; j++) {
                float mean = 0;
                int count = 0;
                for (int k = 0; k < sizeOfData; k++) {
                    if (clusters[k] == i) {
                        mean += data[k].get(j);
                        count++;
                    }
                }
                mean /= count;
                clusterCenters[i].set(j, mean);
            }
        }
    }
    
}