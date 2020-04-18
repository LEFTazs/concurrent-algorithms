package kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Vector<N> {
    protected N[] values;
    
    public Vector(int length) {
        values = (N[]) new Object[length];
    }
    
    public Vector(N[] array) {
        values = array.clone();
    }
    
    public Vector(Vector<N> vectorToCopy) {
        values = vectorToCopy.toArray();
    }
        
    public N get(int index) {
        return values[index];
    }
    
    public void set(int index, N value) {
        values[index] = value;
    }
    
    public N[] toArray() {
        return values.clone();
    }

    public void fromArray(N[] array) {
        if (array.length != this.size())
            throw new IllegalArgumentException("Invalid input size.");
        values = array.clone();
    }
    
    public int size() {
        return values.length;
    }
    
    public N max(Comparator<N> comp) {
        List<N> valuesAsList = Arrays.asList(values);
        return Collections.max(valuesAsList, comp);
    }
    
    public N min(Comparator<N> comp) {
        List<N> valuesAsList = Arrays.asList(values);
        return Collections.min(valuesAsList, comp);
    }
            
}
