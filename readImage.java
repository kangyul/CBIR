/*
 * Project 1
*/

import java.awt.image.BufferedImage;
import java.lang.Object.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;


public class readImage
{
  int imageCount = 1;
  double intensityMatrix [][] = new double[100][26]; // 100 pictures total. 
                                            // 25 histogram bins with the first histogram bin for storing total # of image pixels
  double colorCodeMatrix [][] = new double[100][65]; // // 100 pictures total. 
                                            // 64 histogram bins with the first histogram bin for storing total # of image pixels

  /*Each image is retrieved from the folder.  The height and width are found for the image and the getIntensity and
   * getColorCode methods are called to compute the intensity and color code data from the picture.
  */
  public readImage()
  {
    while(imageCount < 101) {
      try {
        //look inside folder and read/analyze every image file
        String imagePath = "images/" + imageCount + ".jpg";
        BufferedImage pic = ImageIO.read(new File(imagePath));
        getIntensity(pic, pic.getHeight(), pic.getWidth());
        getColorCode(pic, pic.getHeight(), pic.getWidth());
      } 
      catch (IOException e) {
        System.out.println("Error occurred when reading the file.");
      }
      imageCount++;
    }
    
    //Print and write all of the data into a text file
    writeIntensity();
    writeColorCode();
    
  }
  





  //intensity method to get intensity data from picture and put into intensityMatrix histogram
  public void getIntensity(BufferedImage image, int height, int width){
    //for every pixel in image
    int[] pixel;
    intensityMatrix[imageCount - 1][0] = height * width;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
          pixel = image.getRaster().getPixel(x, y, new int[3]);
          double intensity = 0.299 * pixel[0] + 0.587 * pixel[1] + 0.114 * pixel[2];
          int bin = (int)Math.floor(intensity / 10.0) + 1;
          if(bin == 26) {
            bin--;
          }
          intensityMatrix[imageCount - 1][bin]++;
      }
    }
  }
  
  //color code method to get color code data from picture and put into colorCodeMatrix histogram
  public void getColorCode(BufferedImage image, int height, int width){
    //for every pixel in image
    int[] pixel;
    colorCodeMatrix[imageCount - 1][0] = height * width;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
          pixel = image.getRaster().getPixel(x, y, new int[3]);

          String pix_0 = Integer.toBinaryString(pixel[0]);
          String pix_1 = Integer.toBinaryString(pixel[1]);
          String pix_2 = Integer.toBinaryString(pixel[2]);

          if(pix_0.length() == 8){
            pix_0 = "" + pix_0.charAt(0) + pix_0.charAt(1);
          } else if(pix_0.length() == 7) {
            pix_0 = "0" + pix_0.charAt(0);
          } else {
            pix_0 = "00";
          }

          if(pix_1.length() == 8){
            pix_1 = "" + pix_1.charAt(0) + pix_1.charAt(1);
          } else if(pix_1.length() == 7) {
            pix_1 = "0" + pix_1.charAt(0);
          } else {
            pix_1 = "00";
          }

          if(pix_2.length() == 8){
            pix_2 = "" + pix_2.charAt(0) + pix_2.charAt(1);
          } else if(pix_2.length() == 7) {
            pix_2 = "0" + pix_2.charAt(0);
          } else {
            pix_2 = "00";
          }

          String pix_total = pix_0 + pix_1 + pix_2;
          int pix_decimal = Integer.parseInt(pix_total, 2);
          colorCodeMatrix[imageCount - 1][pix_decimal + 1]++;
      }
    }
  }
  
  



  //This method writes the contents of the colorCode matrix to a file named colorCodes.txt.
  public void writeColorCode(){
    try {
      FileWriter file = new FileWriter("colorCodes.txt");
      for(int i = 0; i < 100; i++) {
        file.write(Integer.toString((int)colorCodeMatrix[i][0]));
        for(int j = 1; j < 65; j++) {
          file.write("," + Integer.toString((int)colorCodeMatrix[i][j]));
        }
        file.write("\n");
      }
      file.close();
    } catch (IOException e) {
      System.out.println("An error occurred.");
    }
  }
  
  //This method writes the contents of the intensity matrix to a file called intensity.txt
  public void writeIntensity(){
    try {
      FileWriter file = new FileWriter("intensity.txt");
      for(int i = 0; i < 100; i++) {
        file.write(Integer.toString((int)intensityMatrix[i][0]));
        for(int j = 1; j < 26; j++) {
          file.write("," + Integer.toString((int)intensityMatrix[i][j]));
        }
        file.write("\n");
      }
      file.close();
    } catch (IOException e) {
      System.out.println("An error occurred.");
    }
  }
  
  public static void main(String[] args) {
    new readImage();
  }

}