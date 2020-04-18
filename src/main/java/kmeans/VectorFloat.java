package kmeans;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class VectorFloat extends Vector<Float> {
    
    public VectorFloat(int length) {
        super(length);
    }
    
    public VectorFloat(Float[] array) {
        super(array);
    }
    
    public VectorFloat(VectorFloat vectorToCopy) {
        super(vectorToCopy);
    }
    
    public Float max() {
        List<Float> valuesAsList = Arrays.asList(values);
        return Collections.max(valuesAsList);
    }
    
    public Float min() {
        List<Float> valuesAsList = Arrays.asList(values);
        return Collections.min(valuesAsList);
    }
    
    public double distance(VectorFloat other) {
        if (other.size() != this.size())
            throw new IllegalArgumentException("Invalid input size.");
        
        double distance = 0;
        for (int i = 0; i < this.size(); i++) {
            distance += Math.pow(this.get(i) - other.get(i), 2);
        }
        return Math.sqrt(distance);
    }
}
