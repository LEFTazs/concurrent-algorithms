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

public class Drawer extends PApplet {
    PImage img;
    PImage imgChanged;
    int screenSize = 250;
    int clusterSize = 6;
    int iterations = 1000;
    FloatList[] data = new FloatList[3];
    
    boolean original = false;

    @Override
    public void settings() {
        img = loadImage("garden.jpg");
        img.resize(screenSize * img.width / img.height, screenSize);
        size(img.width, img.height);

        imgChanged = createImage(img.width, img.height, RGB);

        data[0] = new FloatList();
        data[1] = new FloatList();
        data[2] = new FloatList();
    }

    int[] output;
    int[] colors;

    @Override
    public void setup() {
        img.loadPixels();

        for (int h = 0; h < img.height; h++) {
            for (int w = 0; w < img.width; w++) {
                data[0].append(red(img.pixels[w + h * img.width]));
                data[1].append(green(img.pixels[w + h * img.width]));
                data[2].append(blue(img.pixels[w + h * img.width]));
            }
        }

        output = ClusteringAlgorithms.kmeans(data, clusterSize, iterations);

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
