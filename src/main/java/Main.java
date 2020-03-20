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

public class Main extends PApplet {

PImage img;
PImage imgChanged;
int screenSize = 100;
int clusterSize = 6;
int iterations = 1000;
FloatList[] data = new FloatList[3];

public void settings()
{
   img = loadImage("garden.jpg");
   img.resize(screenSize*img.width/img.height, screenSize);
   size(img.width, img.height); 
   
   imgChanged = createImage(img.width, img.height, RGB);
   
   data[0] = new FloatList();
   data[1] = new FloatList();
   data[2] = new FloatList();
}

int[] output;
int[] colors;

public void setup()
{
  img.loadPixels();
      
   for (int h = 0; h < img.height; h++)
     for (int w = 0; w < img.width; w++)
     {
         data[0].append(red(img.pixels[w + h * img.width]));
         data[1].append(green(img.pixels[w + h * img.width]));
         data[2].append(blue(img.pixels[w + h * img.width]));
     }
   
   output = Kmeans(data,  clusterSize, iterations);
   
   colors = new int[clusterSize];
   for (int c = 0; c < colors.length; c++)
   {
     float meanRed = 0;
     float meanBlue = 0;
     float meanGreen = 0;
     int count = 0;
     for (int i = 0; i < output.length; i++)
     {
         if (c == output[i])
         {
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
   
   for (int w = 0; w < img.width; w++)
      for (int h = 0; h < img.height; h++)
      {
         int cp = output[w + h * img.width];
         
         imgChanged.pixels[w + h * img.width] = colors[cp];
      }
   
   img.updatePixels();
   imgChanged.updatePixels();
}

public void draw()
{  
    if (original) image(img, 0, 0);
    else image(imgChanged, 0, 0);
}

boolean original = false;
public void mousePressed()
{
  original = !original;
}

public int[] Kmeans(FloatList[] data, int k, int iterations)
{    
  //Run check
  int dataSize = data[0].size();
  for (int i = 1; i < data.length; i++)
    if (data[i].size() != dataSize) return null;
  
  //Add k number of random positions
  FloatList[] clusterPoint = new FloatList[this.data.length];
  for (int i = 0; i < clusterPoint.length; i++)
  {
      clusterPoint[i] = new FloatList();
      for (int point = 0; point < k; point++)
      {
          clusterPoint[i].append(PApplet.parseInt(random(this.data[i].min(), this.data[i].max())));
      }
  }
  
  //Make clusters
  int[] cluster = null;
  
  //Start looping  
  float timeMean = 0;
  for (int repeat = 0; repeat < iterations; repeat++)
  {
    for (int i = 0; i < 100; i++)
    {
        if (i < 100*(PApplet.parseFloat(repeat) / PApplet.parseFloat(iterations))) print("#");
        else print("=");
    }
    if (repeat != 0) println("Time left: " + (timeMean / repeat) * (iterations - repeat) / (1000 * 60) + " minutes.");
    for (int i = 0; i < 5; i++)
      println("");
      
    int time = millis();
      
    cluster = new int[dataSize];
    
    //Assign the points to the newly made positions based on distance
    for (int i = 0; i < dataSize; i++)
    {
      FloatList distances = new FloatList();
      for (int cp = 0; cp < k; cp++)
      {
        float distance = 0;
        for (int d = 0; d < this.data.length; d++)
        {
            distance += sq(data[d].get(i) - clusterPoint[d].get(cp));
        }
        distances.append(sqrt(distance));
      }
      for (int cp = 0; cp < distances.size(); cp++)
      {
          if (distances.get(cp) == distances.min())
          {
              cluster[i] = cp;
              break;
          }
      }
    }
    
    //Calculate the groups mean value
    for (int i = 0; i < k; i++)
    {
       for (int d = 0; d < this.data.length; d++)
       {
         float mean = 0;
         int count = 0;
         for (int di = 0; di < dataSize; di++)
         {
             if (cluster[di] == i)
             {
                 mean += data[d].get(di);
                 count++;
             }
         }
         mean /= count;
         clusterPoint[d].set(i, mean);
       }
    }
    
    timeMean += millis() - time;
  }

  return cluster;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
