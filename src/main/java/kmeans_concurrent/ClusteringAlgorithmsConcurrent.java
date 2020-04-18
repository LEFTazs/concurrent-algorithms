package kmeans_concurrent;

import java.util.Arrays;
import kmeans.ClusteringAlgorithms;
import kmeans.VectorFloat;

public class ClusteringAlgorithmsConcurrent extends ClusteringAlgorithms {
    private static int threads = 4;
    
    public static int[] kmeans(int k, VectorFloat[] dataPoints, int iterations, int threads) {
        ClusteringAlgorithmsConcurrent.dataPoints = dataPoints;
        ClusteringAlgorithmsConcurrent.K = k;
        ClusteringAlgorithmsConcurrent.numberOfDimensions = dataPoints[0].size();
        ClusteringAlgorithmsConcurrent.threads = threads;
        
        checkInputDataSizeValidity();
        
        generateRandomClusterCenters();
        
        for (int iteration = 0; iteration < iterations; iteration++) {
            adjustClusters();
        }
        
        return clusters;
    }
    
    protected static void adjustClusters() {
        VectorFloat[][] subArrays = splitArray(dataPoints, threads);
        SubTask[] subtasks = initalizeAndStartSubTasks(subArrays);
        clusters = finishSubTasks(subtasks);
        calculateClusterCenters(clusters);
    }
    
    private static SubTask[] initalizeAndStartSubTasks(VectorFloat[][] splitArray) {
        SubTask[] subtasks = new SubTask[threads];
        for (int i = 0; i < subtasks.length; i++) {
            subtasks[i] = new SubTask(splitArray[i]);
            subtasks[i].start();
        }
        return subtasks;
    }
    
    private static int[] finishSubTasks(SubTask[] subtasks) {
        try {
            return tryFinishSubTasks(subtasks);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
        return new int[0];
    }
    
    private static int[] tryFinishSubTasks(SubTask[] subtasks) throws InterruptedException {
        int subtaskSize = subtasks[0].getTaskSize();
        int lastSubtaskSize = subtasks[subtasks.length - 1].getTaskSize();
        int[] results = new int[(subtasks.length - 1) * subtaskSize + lastSubtaskSize];
        for (int i = 0; i < subtasks.length; i++) {
            subtasks[i].join();
            int[] result = subtasks[i].getClusters();
            System.arraycopy(result, 0, results, i * subtaskSize, result.length); 
        }
        return results;
    }
    
    private static VectorFloat[][] splitArray(VectorFloat[] array, int splitsize) {
        if (array.length < splitsize)
            throw new IllegalArgumentException("Argument 'parts' is larger than the array size.");
        
        VectorFloat[][] arrayParts = new VectorFloat[splitsize][];
        int partSizes = array.length / splitsize;
        boolean areAllSplitsEqualSize = (array.length % splitsize) == 0;
        if (areAllSplitsEqualSize) {
            for (int i = 0; i < splitsize; i++) {
                arrayParts[i] = Arrays.copyOfRange(array, i * partSizes, 
                        i * partSizes + partSizes);
            }
        } else {
            partSizes++;
            for (int i = 0; i < splitsize - 1; i++) {
                arrayParts[i] = Arrays.copyOfRange(array, i * partSizes, 
                        i * partSizes + partSizes);
            }
            int i = splitsize - 1;
            arrayParts[i] = Arrays.copyOfRange(array, i * partSizes, 
                        array.length);
        }
        return arrayParts;
    }
    
    private static class SubTask extends Thread implements Runnable {
        private VectorFloat[] data;
        private int[] clusters;
        private int taskSize;
        
        SubTask(VectorFloat[] data) {
            this.data = data;
            this.clusters = new int[data.length];
            this.taskSize = data.length;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < this.data.length; i++) {
                double[] distances = this.data[i].distancesFrom(clusterCenters);
                this.clusters[i] = getIndexOfSmallestDistance(distances);
            }
        }
        
        public int[] getClusters() {
            return this.clusters;
        }

        public int getTaskSize() {
            return taskSize;
        }
    }
}
