/*
Copyright (C) 2014  Thomas Sanchez Lengeling.
 KinectPV2, Kinect one library for processing
 
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */
import KinectPV2.*;

KinectPV2 kinect;

int [] depthZero;

//BUFFER ARRAY TO CLEAN DE PIXLES
PImage depthToColorImg;

void setup() {
  size(512*2, 424*2, P3D);

  depthToColorImg = createImage(512, 424, PImage.RGB);
  depthZero    = new int[ KinectPV2.WIDTHDepth * KinectPV2.HEIGHTDepth];

  //SET THE ARRAY TO 0s
  for (int i = 0; i < KinectPV2.WIDTHDepth; i++) {
    for (int j = 0; j < KinectPV2.HEIGHTDepth; j++) {
      depthZero[424*i + j] = 0;
    }
  }

  kinect = new KinectPV2(this);
  kinect.enableDepthImg(true);
  kinect.enableColorImg(true);
  kinect.activateRawDepth(true);
  kinect.activateRawColor(true);
  kinect.init();
}

void draw() {
  background(0);

  float [] mapDCT = kinect.getMapDepthToColor();

  //get the raw data from depth and color
  int [] colorRaw = kinect.getRawColor();
  int [] depthRaw = kinect.getRawDepth();

  //clean de pixels
  PApplet.arrayCopy(depthZero, depthToColorImg.pixels);

  int count = 0;
  depthToColorImg.loadPixels(); 
  for (int i = 0; i < KinectPV2.WIDTHDepth; i++) {
    for (int j = 0; j < KinectPV2.HEIGHTDepth; j++) {

      //incoming pixels 512 x 424 with position in 1920 x 1080
      float valX = mapDCT[count*2 + 0]; 
      float valY = mapDCT[count*2 + 1];

      //maps the pixels to 512 x 424, not necessary but looks better
      int valXDepth = (int)((valX/1920.0)*512.0);
      int valYDepth = (int)((valY/1080.0)*424.0);

      int  valXColor = (int)(valX);
      int  valYColor = (int)(valY);

      if ( valXDepth >= 0 && valXDepth < 512 && valYDepth >= 0 && valYDepth < 424 &&
        valXColor >= 0 && valXColor < 1920 && valYColor >= 0 && valYColor < 1080) {
        color colorPixel = colorRaw[valYColor*1920 + valXColor];
        //color colorPixel = depthRaw[valYDepth*512 + valXDepth];
        depthToColorImg.pixels[valYDepth*512 + valXDepth] = colorPixel;
      }
      count++;
    }
  }
  depthToColorImg.updatePixels();

  image(depthToColorImg, 0, 424);
  image(kinect.getColorImage(), 0, 0, 512, 424);
  image(kinect.getDepthImage(), 512, 0);

  text("fps: "+frameRate, 50, 50);
}

