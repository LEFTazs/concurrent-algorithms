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
        SubTaskCalculateClusters[] subtasks = initalizeAndStartCalculateClustersSubTasks(subArrays);
        clusters = finishCalculateClustersSubTasks(subtasks);
        calculateClusterCenters();
    }
    
    private static SubTaskCalculateClusters[] initalizeAndStartCalculateClustersSubTasks(VectorFloat[][] splitArray) {
        SubTaskCalculateClusters[] subtasks = new SubTaskCalculateClusters[threads];
        for (int i = 0; i < subtasks.length; i++) {
            subtasks[i] = new SubTaskCalculateClusters(splitArray[i]);
            subtasks[i].start();
        }
        return subtasks;
    }
    
    private static int[] finishCalculateClustersSubTasks(SubTaskCalculateClusters[] subtasks) {
        try {
            return tryFinishCalculateClustersSubTasks(subtasks);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
        return new int[0];
    }
    
    private static int[] tryFinishCalculateClustersSubTasks(SubTaskCalculateClusters[] subtasks) throws InterruptedException {
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
    
    private static class SubTaskCalculateClusters extends Thread implements Runnable {
        private VectorFloat[] data;
        private int[] clusters;
        private int taskSize;
        
        SubTaskCalculateClusters(VectorFloat[] data) {
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
    
    
    protected static void calculateClusterCenters() {
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < numberOfDimensions; j++) {
                VectorFloat[][] subArrays = splitArray(dataPoints, threads);
                SubTaskCalculateClusterMeans[] subtasks = 
                        initalizeAndStartCalculateClusterMeansSubTasks(subArrays, i, j);
                float mean = finishCalculateClusterMeansSubTasks(subtasks);
                clusterCenters[i].set(j, mean);
            }
        }
    }
    
    private static SubTaskCalculateClusterMeans[] initalizeAndStartCalculateClusterMeansSubTasks(VectorFloat[][] splitArray, int currentCluster, int currentDimension) {
        SubTaskCalculateClusterMeans[] subtasks = new SubTaskCalculateClusterMeans[threads];
        for (int i = 0; i < subtasks.length; i++) {
            int dataStartPosition = i*splitArray[i].length;
            subtasks[i] = new SubTaskCalculateClusterMeans(splitArray[i], dataStartPosition, currentCluster, currentDimension);
            subtasks[i].start();
        }
        return subtasks;
    }
    
    private static float finishCalculateClusterMeansSubTasks(SubTaskCalculateClusterMeans[] subtasks) {
        try {
            return tryFinishCalculateClusterMeansSubTasks(subtasks);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
        return 0F;
    }
    
    private static float tryFinishCalculateClusterMeansSubTasks(SubTaskCalculateClusterMeans[] subtasks) throws InterruptedException {
        float sum = 0F;
        int count = 0;
        for (int i = 0; i < subtasks.length; i++) {
            subtasks[i].join();
            sum += subtasks[i].getSum();
            count += subtasks[i].getCount();
        }
        return sum / count;
    }

    
    private static class SubTaskCalculateClusterMeans extends Thread implements Runnable {
        private VectorFloat[] data;
        private int currentCluster;
        private int currentDimension;
        private float sum;
        private int count;
        private int dataStartPosition;
        
        SubTaskCalculateClusterMeans(VectorFloat[] data, int dataStartPosition, int currentCluster, int currentDimension) {
            this.data = data;
            this.currentCluster = currentCluster;
            this.currentDimension = currentDimension;
            this.sum = 0F;
            this.count = 0;
            this.dataStartPosition = dataStartPosition;
        }
        
        @Override
        public void run() {
            for (int k = 0; k < this.data.length; k++) {
                if (clusters[this.dataStartPosition + k] == this.currentCluster) {
                    this.sum += this.data[k].get(this.currentDimension);
                    this.count++;
                }
            }
        }
        
        public float getSum() {
            return sum;
        }

        public int getCount() {
            return count;
        }
    }
}
