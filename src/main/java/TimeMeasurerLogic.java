/**
 *
 * @author soma
 */
import java.io.*;
import java.util.Random;

public class TimeMeasurerLogic {
    final String FILENAME = "Time measurements.txt";
    
    int clusterSize = 2;
    int dataFrom = 100, dataTo = 5000, dataStep = 100;
    int clusterFrom = 2, clusterTo = 8, clusterStep = 1;
    int dimensionsFrom = 1, dimensionsTo = 2, dimensionsStep = 1;

    public void setDimensionsFrom(int dimensionsFrom, int dimensionsTo, int dimensionsStep) {
        this.dimensionsFrom = dimensionsFrom;
        this.dimensionsTo = dimensionsTo;
        this.dimensionsStep = dimensionsStep;
    }
    
    VectorFloat[] dataPoints;
    int iterations = 1000;
    
    TimeMeasurerLogic(){
        makeFile();
        writeFile("Data\tDimensions\tClusters\tIterations\tTime(ms)");
    }
    
    public void setDatas(int from, int to, int step){
        this.dataFrom = from;
        this.dataTo = to;
        this.dataStep = step;
    }
    
    public void setCluster(int from, int to, int step){
        this.clusterFrom = from;
        this.clusterTo = to;
        this.clusterStep = step;
    }
    
    private void makeFile(){
        File file = new File(FILENAME);
    }
    
    private void writeFile(String text){
        try {
            FileWriter writer = new FileWriter(FILENAME, true);
            writer.write(text + "\n");
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private double measure(TimeMeasurer kmeans){
        long startTime = System.nanoTime();
        kmeans.perform(this.clusterSize, this.dataPoints, this.iterations);
        long stopTime = System.nanoTime();
        return (stopTime - startTime) / 1000000;
    }
    
    private VectorFloat[] createRandomInputs(int size, int dimensions){   
        Random rd = new Random();
        dataPoints = new VectorFloat[size];
        int currentVectorId = 0;
        for (int i = 0; i < size; ++i) {
            dataPoints[currentVectorId] = new VectorFloat(dimensions);
            for(int j = 0; j < dimensions; ++j){
                dataPoints[currentVectorId].set(j, rd.nextFloat());
            }
            currentVectorId++;
        }
        
        return dataPoints;
    }
    
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
    
    public void start(){
        for(int i = clusterFrom; i < clusterTo; i += clusterStep){
            clusterSize = i;
            for(int j = this.dataFrom; j < dataTo; j += dataStep){
                for(int k = this.dimensionsFrom; k < this.dimensionsTo; k += dimensionsStep){
                    dataPoints = createRandomInputs(j, k);
                    TimeMeasurerSequential t = new TimeMeasurerSequential();
                    double time = measure(t);
                    writeFile(this.dataPoints.length + "\t" + 
                              k + "\t" +
                              this.clusterSize + "\t" +
                              this.iterations + "\t" +
                              time);
                }
            }
        }
    }
}
