package usecases;

import kmeans.VectorFloat;
import kmeans.ClusteringAlgorithms;
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

public class ImageDrawer extends PApplet {
    PImage img;
    PImage imgChanged;
    int screenSize = 250;
    int clusterSize = 6;
    int iterations = 1000;
    VectorFloat[] data;
    
    boolean original = false;

    @Override
    public void settings() {
        img = loadImage("https://preview.redd.it/6pdgr1lhfys41.png?width=640&crop=smart&auto=webp&s=8ecb41eca02b689dc809518fddaa6108a034e9a2");
        img.resize(screenSize * img.width / img.height, screenSize);
        size(img.width, img.height);

        imgChanged = createImage(img.width, img.height, RGB);
    }

    int[] output;
    int[] colors;

    @Override
    public void setup() {
        img.loadPixels();

        data = new VectorFloat[img.pixels.length];
        int currentVectorId = 0;
        for (int h = 0; h < img.height; h++) {
            for (int w = 0; w < img.width; w++) {
                data[currentVectorId] = new VectorFloat(3); //3: the number of channels (r,g,b)
                data[currentVectorId].set(0, red(img.pixels[w + h * img.width]));
                data[currentVectorId].set(1, green(img.pixels[w + h * img.width]));
                data[currentVectorId].set(2, blue(img.pixels[w + h * img.width]));
                currentVectorId++;
            }
        }

        output = ClusteringAlgorithms.kmeans(clusterSize, data, iterations);

        colors = new int[clusterSize];
        for (int c = 0; c < colors.length; c++) {
            float meanRed = 0;
            float meanBlue = 0;
            float meanGreen = 0;
            int count = 0;
            for (int i = 0; i < output.length; i++) {
                if (c == output[i]) {
                    count++;
                    meanRed += red(img.pixels[i]);
                    meanGreen += green(img.pixels[i]);
                    meanBlue += blue(img.pixels[i]);
                }
            }
            meanRed /= count;
            meanGreen /= count;
            meanBlue /= count;
            colors[c] = color(meanRed, meanGreen, meanBlue);
        }

        imgChanged.loadPixels();

        for (int w = 0; w < img.width; w++) {
            for (int h = 0; h < img.height; h++) {
                int cp = output[w + h * img.width];

                imgChanged.pixels[w + h * img.width] = colors[cp];
            }
        }

        img.updatePixels();
        imgChanged.updatePixels();
    }

    @Override
    public void draw() {
        if (original) {
            image(img, 0, 0);
        } else {
            image(imgChanged, 0, 0);
        }
    }

    @Override
    public void mousePressed() {
        original = !original;
    }


}
