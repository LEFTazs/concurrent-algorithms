package usecases;


import kmeans.ClusteringAlgorithms;
import kmeans.VectorFloat;
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.List;

public class GraphDrawer extends PApplet {

    List<VectorFloat> data;
    int[] clusters;
    boolean doneEditing = false;
    int rectSize = 10;

    
    @Override
    public void settings() {
        size(500, 500);
    }
    
    @Override
    public void setup() {
        rectMode(CENTER);
        data = new ArrayList<>();
    }

    @Override
    public void draw() {
        if (!doneEditing) {
            for (int i = 0; i < data.size(); i++) {
                rect(data.get(i).get(0), data.get(i).get(1), rectSize, rectSize);
            }
        } else {
            for (int k = 0; k < clusters.length; k++) {
                int cluster = clusters[k];
                switch (cluster) {
                    case 0:
                        fill(0xFF140DFF);
                        break;
                    case 1:
                        fill(0xFFFF0D0D);
                        break;
                    case 2:
                        fill(0xFF03FC2E);
                        break;
                    case 3:
                        fill(0xFFE7FC03);
                        break;
                    default:
                        break;
                }
                rect(data.get(k).get(0), data.get(k).get(1), rectSize, rectSize);
            }
        }
    }

    @Override
    public void mousePressed() {
        if (mouseButton == RIGHT) {
            VectorFloat[] dataAsArray = data.toArray(new VectorFloat[0]);
            clusters = ClusteringAlgorithms.kmeans(2, dataAsArray, 15000);
            doneEditing = true;
        }

        if (!doneEditing) {
            VectorFloat vf = new VectorFloat(2);
            vf.set(0, (float) mouseX);
            vf.set(1, (float) mouseY);
            data.add(vf);
        }
    }
}
