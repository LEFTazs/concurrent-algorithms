package kmeans;

import processing.core.*;
import usecases.FakeDataClusterer;

public class Main {
    public static void main(String[] passedArgs) {
        FakeDataClusterer.cluster();
        /*String[] appletArgs = new String[]{"usecases.ImageDrawer"};
        if (passedArgs != null) {
            PApplet.main(PApplet.concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }*/
    }
}
