/* Project 1
*/

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorListener;

import org.w3c.dom.events.MouseEvent;

import javax.swing.*;

public class CBIR extends JFrame{
    
    private JLabel photographLabel = new JLabel();  //container to hold a large 
    private JButton [] button; //creates an array of JButtons
    private ImageIcon [] orig_images; //creates an array of images
    private int [] buttonOrder = new int [101]; //creates an array to keep up with the image order
    private GridLayout gridLayout1;
    private GridLayout gridLayout2;
    private GridLayout gridLayout3;
    private GridLayout gridLayout4;
    private JPanel panelBottom1;
    private JPanel panelTop;
    private JPanel buttonPanel;
    private Double [][] intensityMatrix = new Double [100][26];
    private Double [][] colorCodeMatrix = new Double [100][65];
    private Boolean [] relevanceChecked = new Boolean [100];
    private Double[][] normalizedFeature = new Double[100][90];
    private Map <Double , LinkedList<Integer>> map;
    int picNo = 0;
    int imageCount = 1; //keeps up with the number of images displayed since the first page.
    int pageNo = 1;
    boolean relevanceChecker = false;
    JLabel checkRelevance = new JLabel("Check");


    
    public static void main(String args[]) {
        readImage hold = new readImage();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CBIR app = new CBIR();
                app.setVisible(true);
            }
        });
    }
    
    

    public CBIR() {
      //The following lines set up the interface including the layout of the buttons and JPanels.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Icon Demo: Please Select an Image");  
        setSize(1400, 680);
        // this centers the frame on the screen
        setLocationRelativeTo(null);   
        
        panelBottom1 = new JPanel();
        panelTop = new JPanel();
        buttonPanel = new JPanel();
        gridLayout1 = new GridLayout(7, 3, 5, 5); //Pictures
        gridLayout2 = new GridLayout(1, 2, 5, 5); //Main layout
        gridLayout3 = new GridLayout(2, 1, 5, 5);
        gridLayout4 = new GridLayout(3, 3, 5, 5);   //Buttons Layout
        setLayout(gridLayout2);//Divides screen 
        panelBottom1.setLayout(gridLayout1);
        JScrollPane scroll = new JScrollPane(panelBottom1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelTop.setLayout(gridLayout3);
        add(panelTop);
        add(scroll);
        photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
        photographLabel.setHorizontalTextPosition(JLabel.CENTER);
        photographLabel.setHorizontalAlignment(JLabel.CENTER);
        photographLabel.setVerticalAlignment(JLabel.CENTER);
        photographLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        buttonPanel.setLayout(gridLayout4);
        JScrollPane scroll_big = new JScrollPane(photographLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelTop.add(scroll_big);


        //Upon clicking on the big image, a new window will appear to show the image 
        photographLabel.addMouseListener(new MouseListener(){
          @Override
          public void mousePressed(java.awt.event.MouseEvent e) {
            JFrame new_frame = new JFrame();
            new_frame.setTitle("Image Viewer");
            JLabel new_icon = new JLabel();
              new_icon.setIcon(photographLabel.getIcon());
              new_frame.add(new_icon);
              new_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
              new_frame.dispose();
              new_frame.pack();
              new_frame.setVisible(true);
          }
          @Override
          public void mouseClicked(java.awt.event.MouseEvent e) { }
          @Override
          public void mouseReleased(java.awt.event.MouseEvent e) { }
          @Override
          public void mouseEntered(java.awt.event.MouseEvent e) { }
          @Override
          public void mouseExited(java.awt.event.MouseEvent e) { }
        });


        panelTop.add(buttonPanel);
        JButton previousPage = new JButton("Previous Page");
        JButton nextPage = new JButton("Next Page");
        JButton intensity = new JButton("Intensity");
        JButton colorCode = new JButton("Color Code");
        JButton intensityAndcolorCode = new JButton("Intensity and Color Code");
        JCheckBox relevance = new JCheckBox("Relevance", false); 
        
        checkRelevance.setForeground(Color.red);
        buttonPanel.add(previousPage);
        buttonPanel.add(nextPage);
        buttonPanel.add(intensity);
        buttonPanel.add(colorCode);
        buttonPanel.add(intensityAndcolorCode);
        buttonPanel.add(relevance);
        
        nextPage.addActionListener(new nextPageHandler());
        previousPage.addActionListener(new previousPageHandler());
        intensity.addActionListener(new intensityHandler());
        colorCode.addActionListener(new colorCodeHandler());
        intensityAndcolorCode.addActionListener(new intensityAndcolorCodeHandler());
        relevance.addActionListener(new relevanceHandler());
        
        
        button = new JButton[101];
        orig_images = new ImageIcon[101];
        /*This for loop goes through the images in the database and stores them as icons and adds
         * the images to JButtons and then to the JButton array
        */
        for (int i = 1; i < 101; i++) {
            ImageIcon icon;
            icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
            ImageIcon pic;
            pic = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
            Image image = icon.getImage(); // transform it 
            Image newimg;
            if(icon.getIconWidth() > icon.getIconHeight()){
              newimg = image.getScaledInstance(128, 85,  java.awt.Image.SCALE_SMOOTH); // scale it 
            } else {
              newimg = image.getScaledInstance(57, 85,  java.awt.Image.SCALE_SMOOTH); // scale it
            }
            icon = new ImageIcon(newimg);  // transform it back
            
              if(icon != null){
                button[i] = new JButton(icon);
                orig_images[i] = pic;
                button[i].addActionListener(new IconButtonHandler(i, icon));
                buttonOrder[i] = i;
            }
        }
        Arrays.fill(relevanceChecked, false);
        readIntensityFile();
        readColorCodeFile();
        normalizeFeature();
        displayFirstPage();
    }



    /*This method opens the intensity text file containing the intensity matrix with the histogram bin values for each image.
     * The contents of the matrix are processed and stored in a two dimensional array called intensityMatrix. When reading from file,
     * assume histogram for all images are already computed correctly and that the file exists. Parses and prepares for later
     * comparing images
    */
    public void readIntensityFile(){
      Scanner read;
      String value_stored = "";
      int lineNumber = 0;
      int bin_num = 0;
      try{
        read = new Scanner(new File ("intensity.txt"));
        while (read.hasNextLine()) {
          String data = read.nextLine();
          value_stored = "";
          for(int i = 0; i < data.length(); i++) {
            if(data.charAt(i) == ',') {
                intensityMatrix[lineNumber][bin_num] = Double.parseDouble(value_stored);
                value_stored = "";
                bin_num++;
            } else {
              value_stored += "" + data.charAt(i);
            }
          }
          intensityMatrix[lineNumber][bin_num] = Double.parseDouble(value_stored);
          lineNumber++;
          bin_num = 0;
          value_stored = "";
        }
        read.close();
      }
      catch(FileNotFoundException EE){
        System.out.println("The file intensity.txt does not exist");
      }
    }
    


    /*This method opens the color code text file containing the color code matrix with the histogram bin values for each image.
     * The contents of the matrix are processed and stored in a two dimensional array called colorCodeMatrix.
    */
    private void readColorCodeFile(){
      Scanner read_color;
      String value_stored = "";
      int lineNumber = 0;
      int bin_num = 0;
      try{
        read_color = new Scanner(new File ("colorCodes.txt"));
        while (read_color.hasNextLine()) {
          String data = read_color.nextLine();
          value_stored = "";
          for(int i = 0; i < data.length(); i++) {
            if(data.charAt(i) == ',') {
                colorCodeMatrix[lineNumber][bin_num] = Double.parseDouble(value_stored);
                value_stored = "";
                bin_num++;
            } else {
              value_stored += "" + data.charAt(i);
            }
          }
          colorCodeMatrix[lineNumber][bin_num] = Double.parseDouble(value_stored);
          lineNumber++;
          bin_num = 0;
          value_stored = "";
        }
        read_color.close();
      }
      catch(FileNotFoundException EE){
        System.out.println("The file colorCodes.txt does not exist");
      }
    }
      


    /*This class implements an ActionListener for each iconButton.  When an icon button is clicked, the image on the 
     * the button is added to the photographLabel and the picNo is set to the image number selected and being displayed.
    */ 
    private class IconButtonHandler implements ActionListener{
      int pNo = 0;
      ImageIcon iconUsed;
      
      IconButtonHandler(int i, ImageIcon j){
        pNo = i;
        iconUsed = orig_images[i];
      }
      
      public void actionPerformed( ActionEvent e){
        photographLabel.setIcon(iconUsed);
        photographLabel.setText(pNo + ".jpg      Width: " + iconUsed.getIconWidth() + "px   Height: " + iconUsed.getIconHeight() +
                                "px      Total Pixels: " + (iconUsed.getIconWidth() * iconUsed.getIconHeight()) + "px");
        picNo = pNo;
      }
    }



    /*This method displays the first twenty images in the panelBottom.  The for loop starts at number one and gets the image
     * number stored in the buttonOrder array and assigns the value to imageButNo.  The button associated with the image is 
     * then added to panelBottom1.  The for loop continues this process until twenty images are displayed in the panelBottom1
     * If relevancy is checked, it will show the checkmarks for each picture.
    */
    private void displayFirstPage(){
      int imageButNo = 0;
      imageCount = 1;
      panelBottom1.removeAll(); 
      for(int i = 1; i < 21; i++){
        imageButNo = buttonOrder[i];
        panelBottom1.add(button[imageButNo]); 
        if(relevanceChecker) {
        	JCheckBox imgRelevance = new JCheckBox("Relevance", relevanceChecked[imageButNo - 1]);
        	panelBottom1.add(imgRelevance);
        	imgRelevance.addActionListener(new imgRelevanceHandler(imageButNo));
        }
        imageCount ++;
      }
      panelBottom1.revalidate();  
      panelBottom1.repaint();
    }


    
    /*This class implements an ActionListener for the nextPageButton.  The last image number to be displayed is set to the 
     * current image count plus 20.  If the endImage number equals 101, then the next page button does not display any new 
     * images because there are only 100 images to be displayed.  The first picture on the next page is the image located in 
     * the buttonOrder array at the imageCount. If relevancy is checked, it will show the checkmarks for each picture.
    */
    private class nextPageHandler implements ActionListener{
      public void actionPerformed( ActionEvent e){
        int imageButNo = 0;
        int endImage = imageCount + 20;
        if(endImage <= 101){
          panelBottom1.removeAll(); 
          for (int i = imageCount; i < endImage; i++) {
            imageButNo = buttonOrder[i];
            panelBottom1.add(button[imageButNo]);
            if(relevanceChecker) {
              JCheckBox imgRelevance = new JCheckBox("Relevance", relevanceChecked[imageButNo - 1]);
              panelBottom1.add(imgRelevance);
              imgRelevance.addActionListener(new imgRelevanceHandler(imageButNo));
            }
            imageCount++;
          }
          panelBottom1.revalidate();  
          panelBottom1.repaint();
        }
      }
    }
    


    /*This class implements an ActionListener for the previousPageButton. The last image number to be displayed is set to the 
     * current image count minus 40. If the endImage number is less than 1, then the previous page button does not display any new 
     * images because the starting image is 1. The first picture on the next page is the image located in 
     * the buttonOrder array at the imageCount. If relevancy is checked, it will show the checkmarks for each picture
    */
    private class previousPageHandler implements ActionListener{
      public void actionPerformed( ActionEvent e){
        int imageButNo = 0;
        int startImage = imageCount - 40;
        int endImage = imageCount - 20;
        if(startImage >= 1){
          panelBottom1.removeAll();
          /*The for loop goes through the buttonOrder array starting with the startImage value
            * and retrieves the image at that place and then adds the button to the panelBottom1.
          */
          for (int i = startImage; i < endImage; i++) {
            imageButNo = buttonOrder[i];
            panelBottom1.add(button[imageButNo]);
            if(relevanceChecker) {
              JCheckBox imgRelevance = new JCheckBox("Relevance", relevanceChecked[imageButNo - 1]);
              panelBottom1.add(imgRelevance);
              imgRelevance.addActionListener(new imgRelevanceHandler(imageButNo));
            }
            imageCount--;
          }
          panelBottom1.revalidate();  
          panelBottom1.repaint();
        }
      }
    }
    
    

    /*This class implements an ActionListener when the user selects the intensityHandler button.  The image number that the
     * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
     * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one.
     * The size of the image is retrieved from the intensityMatrix at index 0.  The selected image's intensity bin values are 
     * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
     * The images are then arranged from most similar to the least. COMPARATOR 
     */
    private class intensityHandler implements ActionListener{
      public void actionPerformed( ActionEvent e){
          double [] distance = new double [101]; //place all of the computed distances for each pic here
          map = new HashMap<Double, LinkedList<Integer>>(); //key is the distance. Values are the pictures that have this distance
          double hold_dist = 0; //holder for computed distance
          int pic = (picNo - 1); //source pic
          
          // if no picture is selected do nothing
          if(pic == -1) {
            return;
          }

          // Applies the Manhattan Distance formula to compare distances
          for(int compare_image = 0; compare_image < 100; compare_image++){
            for(int j = 1; j < 26; j++){
              hold_dist += Math.abs((intensityMatrix[pic][j] / intensityMatrix[pic][0]) -
                          (intensityMatrix[compare_image][j] / intensityMatrix[compare_image][0]));
            }
            distance[compare_image + 1] = hold_dist;
            if(!map.containsKey(hold_dist)){
              LinkedList<Integer> images = new LinkedList<Integer>();
              images.add(compare_image);
              map.put(hold_dist, images);
            } else {
              LinkedList<Integer> images = map.get(hold_dist);
              images.add(compare_image);
              map.put(hold_dist, images);
            }
            hold_dist = 0;
          }

          Arrays.sort(distance);

          //Analyzes the sorted distances and looks at map to reorder the pictures
          for(int i = 1; i < 101; i++) {
            LinkedList<Integer> images = map.get(distance[i]);
            if(images.size() == 1){
              buttonOrder[i] = (images.getFirst() + 1);
            } else {
              int counter = 0;
              for(int j = 0; j < images.size(); j++) {
                buttonOrder[i + counter] = (images.get(j) + 1);
                counter++;
              }
              i += (counter - 1);
            }
          }
          imageCount = 1;
          displayFirstPage();
      }
    }
    


    /*This class implements an ActionListener when the user selects the colorCode button.  The image number that the
     * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
     * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one. 
     * The size of the image is retrieved from the colorCodeMatrix at index 0.  The selected image's intensity bin values are 
     * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
     * The images are then arranged from most similar to the least.
     */ 
    private class colorCodeHandler implements ActionListener{

      public void actionPerformed( ActionEvent e){
        double [] distance = new double [101]; //place all of the computed distances for each pic here
        map = new HashMap<Double, LinkedList<Integer>>(); //key is the distance. Values are the pictures that have this distance
        double hold_dist = 0; //holder for computed distance
        int pic = (picNo - 1); //source pic
        
        // if no picture is selected do nothing
        if(pic == -1) {
          return;
        }

        // Applies the Manhattan Distance formula to compare distances
        for(int compare_image = 0; compare_image < 100; compare_image++){
          for(int j = 1; j < 65; j++){
            hold_dist += Math.abs((colorCodeMatrix[pic][j] / colorCodeMatrix[pic][0]) -
                        (colorCodeMatrix[compare_image][j] / colorCodeMatrix[compare_image][0]));
          }
          distance[compare_image + 1] = hold_dist;
          if(!map.containsKey(hold_dist)){
            LinkedList<Integer> images = new LinkedList<Integer>();
            images.add(compare_image);
            map.put(hold_dist, images);
          } else {
            LinkedList<Integer> images = map.get(hold_dist);
            images.add(compare_image);
            map.put(hold_dist, images);
          }
          hold_dist = 0;
        }

        Arrays.sort(distance);

        //Analyzes the sorted distances and looks at map to reorder the pictures
        for(int i = 1; i < 101; i++) {
          LinkedList<Integer> images = map.get(distance[i]);
          if(images.size() == 1){
            buttonOrder[i] = (images.getFirst() + 1);
          } else {
            int counter = 0;
            for(int j = 0; j < images.size(); j++) {
              buttonOrder[i + counter] = (images.get(j) + 1);
              counter++;
            }
            i += (counter - 1);
          }
        }
        imageCount = 1;
        displayFirstPage();
      }
    }
    


    // Merges intensity and colorcode histograms together into the normalizedFeature matrix
    // and normalizes all of the bins for future use.
    private void normalizeFeature(){
      // featureMatrix for original merged feature matrix
      double [][] featureMatrix = new double [100][90];

      //Merge intensity
      for(int i = 0; i < 100; i++) {
        for(int j = 0; j < 26; j++){
          featureMatrix[i][j] = intensityMatrix[i][j] / intensityMatrix[i][0];
          if(j == 0) {
            featureMatrix[i][j] = intensityMatrix[i][j];
          }
        }
      }

      //Merge colorcode
      for(int i = 0; i < 100; i++) {
        for(int j = 1; j < 65; j++){
          featureMatrix[i][j+25] = colorCodeMatrix[i][j] / colorCodeMatrix[i][0];
        }
      }
      
      // get average for each column
      double [] avgMatrix = new double[89];
      double [] stdevMatrix = new double[89];
      for(int i = 1; i < 90; i++) {
        double sum = 0.0;
        for(int j = 0; j < 100; j++) {
          sum += featureMatrix[j][i];
        }
        avgMatrix[i-1] = sum / 100;


        //get standard deviation for each column
        double standardDeviation = 0.0;
        for(int j = 0; j < 100; j++) {
          standardDeviation += Math.pow(featureMatrix[j][i] - avgMatrix[i-1], 2);
        }
        stdevMatrix[i-1] = Math.sqrt(standardDeviation/(100 - 1));
      }
      

      // normalized feature matrix
      for(int i = 0; i < 100; i++) {
        for(int j = 0; j < 90; j++) {
          if(j == 0) {
            normalizedFeature[i][j] = featureMatrix[i][j];
          } else {
            if(stdevMatrix[j - 1] == 0){
              normalizedFeature[i][j] = featureMatrix[i][j];
            } else {
              normalizedFeature[i][j] = (featureMatrix[i][j] - avgMatrix[j-1]) / stdevMatrix[j-1];
            }
          }
        }
      }
    }



    /*This class implements an ActionListener when the user selects the intensityAndcolorCodeHandler button. 
    * The image number that the user would like to find similar images for is stored in the variable pic.  
    * pic takes the image number associated with the image selected and subtracts one to account for the fact
    * that the normalizedFeature matrix starts with zero and not one. The selected image's intensity and colorcode
    * bins values are compared to all the other image's bin values and a score is determined for how well the
    * images compare. The images are then arranged from most similar to the least. When no user feedback is provided
    * the weight is 1/89. When user relevance feedback is provided, the method recalculates the weights according
    * to the user's selected images.
    */
    private class intensityAndcolorCodeHandler implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
        double [] distance = new double [101]; //place all of the computed distances for each pic here
        map = new HashMap<Double, LinkedList<Integer>>(); //key is the distance. Values are the pictures that have this distance
        double hold_dist = 0; //holder for computed distance
        int pic = (picNo - 1); //source pic
        
        // if no picture is selected do nothing
        if(pic == -1) {
          return;
        }
            
        // weight for calculating the manhattan distance
        double [] weight = new double[90];
        Arrays.fill(weight, 0.011235955);
        double [] updatedWeight = new double[90];
        Arrays.fill(updatedWeight, 0);



        //if there are relevant images
        int checked = 0;
        if(relevanceChecker) {
          for(int i = 0; i < 100; i++) {
            if(relevanceChecked[i]) {
              checked++;
            }
          }
        }

        // if at least one picture is checked for relevance, modify the weight
        if(checked > 0) {
          UpdateWeightsHelper(checked, weight, updatedWeight);
        }



      // compute Manhattan distance 
      for(int compare_image = 0; compare_image < 100; compare_image++) {
        for(int j = 1; j < 90; j++){
          hold_dist += weight[j] * Math.abs(normalizedFeature[pic][j] - normalizedFeature[compare_image][j]);
        }
        distance[compare_image + 1] = hold_dist;
        if(!map.containsKey(hold_dist)){
          LinkedList<Integer> images = new LinkedList<Integer>();
            images.add(compare_image);
            map.put(hold_dist, images);
        } else {
            LinkedList<Integer> images = map.get(hold_dist);
            images.add(compare_image);
            map.put(hold_dist, images);
        }
        hold_dist = 0;
      }

      Arrays.sort(distance);
      
      //Analyzes the sorted distances and looks at map to reorder the pictures
      for(int i = 1; i < 101; i++) {
        LinkedList<Integer> images = map.get(distance[i]);
        if(images.size() == 1) {
          buttonOrder[i] = (images.getFirst() + 1);				  
        } else {
          int counter = 0;
          for(int j = 0; j < images.size(); j++) {
            buttonOrder[i + counter] = (images.get(j) + 1);
              counter++;
            }
          i += (counter - 1);
        }
      }

      imageCount = 1;
      displayFirstPage();
      }
    }
    


    // Enables and disables all of the check boxes next to the images
    private class relevanceHandler implements ActionListener {
    	@Override
    	public void actionPerformed(ActionEvent e) {
    		if(relevanceChecker) {//No checkboxes
    			relevanceChecker = false;
          imageCount = 1;
    			displayFirstPage();
    		} else {//Yes checkboxes
    			relevanceChecker = true; 
    			imageCount = 1;
    			displayFirstPage();
    		}
    	}
    }
    


  //Keeps track of which image is selected for relevance and which image is not selected
  private class imgRelevanceHandler implements ActionListener {
    int img = 0;
    public imgRelevanceHandler(int imageButNo) {
      img = imageButNo;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if(relevanceChecked[img - 1]) {
            relevanceChecked[img - 1] = false;
      } else relevanceChecked[img - 1] = true;
    }
  }



  // When intensity and colorcode button is pressed and user has provided relevance feedback, this
  // method will help calculate and normalize the new weights for each col.
  public void UpdateWeightsHelper(int checked, double[] weight, double[] updatedWeight) {
    double [] avgMatrix = new double[89];
    double [] stdevMatrix = new double[89];
    double min = Double.MAX_VALUE; // variable to find the smallest standard deviation
    
    //For every col
    for(int i = 1; i < 90; i++) {
      double sum = 0.0;
      double standardDeviation = 0.0;

      //Compute average for selected images
      for(int j = 0; j < 100; j++) {
        if(relevanceChecked[j]) {
          sum += normalizedFeature[j][i];
        }
      }
      avgMatrix[i - 1] = sum / checked;
      
      //Compute standard deviation
      for(int j = 0; j < 100; j++) {
        if(relevanceChecked[j]) {
          standardDeviation += Math.pow(normalizedFeature[j][i] - avgMatrix[i - 1], 2);
        }
      }
      stdevMatrix[i - 1] = Math.sqrt(standardDeviation/(checked - 1));
      
      // Added code for if stdev == 0 finding the smallest non-0 standard deviation
      if(stdevMatrix[i - 1] < min && stdevMatrix[i - 1] != 0) { 
        min = stdevMatrix[i - 1];
      }
    }

    // Code for if stdev == 0
    for(int i = 0; i < 89; i++) {
      if(stdevMatrix[i] == 0) {
        if(avgMatrix[i] != 0) stdevMatrix[i] = (min / 2);
      }
    }
    
    //Calculates the updated weights
    double updatedWeightSum = 0.0;
    for(int i = 1; i < 90; i++) {
      if(stdevMatrix[i - 1] != 0) {
        updatedWeight[i] = 1 / stdevMatrix[i - 1];
      } else updatedWeight[i] = 0;
      updatedWeightSum += updatedWeight[i];
    }
    
    //Normalizes the updated weights
    for(int i = 1; i < 90; i++) {
      weight[i] = updatedWeight[i] / updatedWeightSum;
    }
  }

}