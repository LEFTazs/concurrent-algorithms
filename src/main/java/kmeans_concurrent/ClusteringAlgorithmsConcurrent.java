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
            throw new IllegalArgumentException("Argument 'splitsize' is larger than the array size.");
        
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
        int currentlyUsedThreads = chooseThreadNumber(K, numberOfDimensions);
        int[] clusterSplits = splitRange(0, K, currentlyUsedThreads);
        int[] dimensionSplits = splitRange(0, numberOfDimensions, currentlyUsedThreads);
        SubTaskCalculateClusterMeans[] subtasks = 
                initalizeAndStartCalculateClusterMeansSubTasks(currentlyUsedThreads, clusterSplits, dimensionSplits);
        finishCalculateClusterMeansSubTasks(subtasks);
    }
    
    private static int[] splitRange(int from, int to, int splitsize) {
        int rangeSize = to - from;
        
        if (rangeSize < splitsize)
            throw new IllegalArgumentException("Argument 'splitsize' is larger than the range.");
        
        int partSizes = rangeSize / splitsize;
        boolean areAllSplitsEqualSize = (rangeSize % splitsize) == 0;
        int[] splits = new int[splitsize];
        if (areAllSplitsEqualSize) {
            for (int i = 0; i < splitsize; i++) {
                splits[i] = (i+1) * partSizes;
            }
        } else {
            partSizes++;
            for (int i = 0; i < splitsize - 1; i++) {
                splits[i] = (i+1) * partSizes;
            }
            splits[splitsize - 1] = to;
        }
        
        return splits;
    }
    
    private static int chooseThreadNumber(int K, int numberOfDimensions) {
        return Math.min(threads, Math.min(K, numberOfDimensions));
    }
    
    private static SubTaskCalculateClusterMeans[] initalizeAndStartCalculateClusterMeansSubTasks(int threads, int[] clusterSplits, int[] dimensionSplits) {
        SubTaskCalculateClusterMeans[] subtasks = new SubTaskCalculateClusterMeans[threads];
        subtasks[0] = new SubTaskCalculateClusterMeans(0, clusterSplits[0], 0, dimensionSplits[0]);
        for (int i = 1; i < subtasks.length; i++) {
            subtasks[i] = new SubTaskCalculateClusterMeans(clusterSplits[i-1], clusterSplits[i], dimensionSplits[i-1], dimensionSplits[i]);
            subtasks[i].start();
        }
        return subtasks;
    }
    
    private static void finishCalculateClusterMeansSubTasks(SubTaskCalculateClusterMeans[] subtasks) {
        try {
            tryFinishCalculateClusterMeansSubTasks(subtasks);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    private static void tryFinishCalculateClusterMeansSubTasks(SubTaskCalculateClusterMeans[] subtasks) throws InterruptedException {
        for (int i = 0; i < subtasks.length; i++) {
            subtasks[i].join();
        }
    }
    
    private static class SubTaskCalculateClusterMeans extends Thread implements Runnable {
        private int fromCluster;
        private int toCluster;
        private int fromDimension;
        private int toDimension;
        
        SubTaskCalculateClusterMeans(int fromCluster, int toCluster, int fromDimension, int toDimension) {
            this.fromCluster = fromCluster;
            this.toCluster = toCluster;
            this.fromDimension = fromDimension;
            this.toDimension = toDimension;
        }
        
        @Override
        public void run() {
            for (int i = fromCluster; i < toCluster; i++) {
                for (int j = fromDimension; j < toDimension; j++) {
                    float mean = 0;
                    int count = 0;
                    for (int k = 0; k < dataPoints.length; k++) {
                        if (clusters[k] == i) {
                            mean += dataPoints[k].get(j);
                            count++;
                        }
                    }
                    mean /= count;
                    synchronized(clusterCenters[i]) {
                        clusterCenters[i].set(j, mean);
                    }
                }
            }
        }
        
        
    }
}
