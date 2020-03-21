import java.util.Random;
import processing.core.PApplet;
import processing.data.FloatList;

public class ClusteringAlgorithms {
    
    private static FloatList[] data;
    private static FloatList[] clusterCenters;
    private static int[] clusters;
    private static int sizeOfData;
    private static int numberOfDimensions;
    private static int numberOfClusters;

    public static int[] kmeans(FloatList[] data, int k, int iterations) {
        ClusteringAlgorithms.data = data;
        ClusteringAlgorithms.numberOfClusters = k;
        ClusteringAlgorithms.sizeOfData = data[0].size();
        ClusteringAlgorithms.numberOfDimensions = data.length;
        
        checkInputDataSizeValidity();
        
        generateRandomClusterCenters();
        
        for (int iteration = 0; iteration < iterations; iteration++) {
            adjustClusters();
        }
        
        return clusters;
    }
    
    private static void checkInputDataSizeValidity() {
        for (int i = 1; i < numberOfDimensions; i++) {
            if (data[i].size() != sizeOfData) {
                throw new RuntimeException("Invalid input!");
            }
        }
    }
    
    private static void generateRandomClusterCenters() {
        Random random = new Random();
        clusterCenters = new FloatList[numberOfDimensions];
        for (int i = 0; i < clusterCenters.length; i++) {
            clusterCenters[i] = new FloatList();
            for (int point = 0; point < numberOfClusters; point++) {
                float upperBound = data[i].max() - data[i].min();
                float lowerBound = data[i].min();
                float randomPosition = random.nextFloat() * upperBound + lowerBound;
                clusterCenters[i].append(randomPosition);
            }
        }
    }
    
    private static void adjustClusters() {
        for (int i = 0; i < sizeOfData; i++) {
            FloatList distances = calculateDataDistanceFromClusterCenters(i);
            chooseClastersForOneData(distances, i);
        }
        calculateClusterCenters(clusters);
    }
    
    private static FloatList calculateDataDistanceFromClusterCenters(int i) {
        FloatList distances = new FloatList();
        for (int cp = 0; cp < numberOfClusters; cp++) {
            float distance = 0;
            for (int d = 0; d < numberOfDimensions; d++) {
                distance += PApplet.sq(data[d].get(i) - clusterCenters[d].get(cp));
            }
            distances.append(PApplet.sqrt(distance));
        }
        return distances;
    }
    
    private static void chooseClastersForOneData(FloatList distances, int i) {
        clusters = new int[sizeOfData];
        for (int cp = 0; cp < distances.size(); cp++) {
            if (distances.get(cp) == distances.min()) {
                clusters[i] = cp;
                return;
            }
        }
    }
    
    private static void calculateClusterCenters(int[] clusters) {
        for (int i = 0; i < numberOfClusters; i++) {
            for (int d = 0; d < numberOfDimensions; d++) {
                float mean = 0;
                int count = 0;
                for (int di = 0; di < sizeOfData; di++) {
                    if (clusters[di] == i) {
                        mean += data[d].get(di);
                        count++;
                    }
                }
                mean /= count;
                clusterCenters[d].set(i, mean);
            }
        }
    }
    
}
