package com.test.sobel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {
	public static void main(String args[]) throws IOException {

		System.out.println("Start Program");
		
		//getting the current directory in order to use the iamge
		String currentDirectory;
		currentDirectory = System.getProperty("user.dir");
			
		//the name of the file that we are going to process
		String directoryAndFileName = currentDirectory + "\\sobel.png";
		
		//get the file
		File iamge = new File(directoryAndFileName);
		
		//getting the image as a matrix with JavaFX class
		BufferedImage matrix = ImageIO.read(iamge);
		
		//width of the matrix
		int width = matrix.getWidth();
		//height of the matrix
		int height = matrix.getHeight();
		
		//intialization of the output matrix
		int[][] gradientMatrix = new int[width][height];
		
		//variable to store the max gradient
		int max = -1;
		
		//start time of the Sobel Operator
		long startTime = System.currentTimeMillis();
		
		//Sobel Operator, double for to iterate over the whole matrix
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				
				//getting the gray value of all the position of the kernel 3x3 that are in RGB.
				//first column
				int position00 = getGrayValue(matrix.getRGB(i - 1, j - 1));
				int position01 = getGrayValue(matrix.getRGB(i - 1, j));
				int position02 = getGrayValue(matrix.getRGB(i - 1, j + 1));
				
				//second column
				int position10 = getGrayValue(matrix.getRGB(i, j - 1));
				int position11 = getGrayValue(matrix.getRGB(i, j));
				int position12 = getGrayValue(matrix.getRGB(i, j + 1));

				//third column
				int position20 = getGrayValue(matrix.getRGB(i + 1, j - 1));
				int position21 = getGrayValue(matrix.getRGB(i + 1, j));
				int position22 = getGrayValue(matrix.getRGB(i + 1, j + 1));
				
				//convolution of the kernel with matrix [-1, 0, 1; -2, 0, 2; -1, 0, 1] in order to obtain Gx
				int Gx = ((-1 * position00) + (0 * position01) + (1 * position02)) + ((-2 * position10) + (0 * position11) + (2 * position12))
						+ ((-1 * position20) + (0 * position21) + (1 * position22));
				
				//sconvolution of the kernel with matrix [-1, -2, -1; 0, 0, 0; 1, 2, 1] in order to obtain Gy
				int Gy = ((-1 * position00) + (-2 * position01) + (-1 * position02)) + ((0 * position10) + (0 *position11) + (0 * position12))
						+ ((1 * position20) + (2 * position21) + (1 * position22));
				
				//to obtain G we have to calculate the square root of the Gx squared + Gy squared 	
				double gDouble = Math.sqrt((Gx * Gx) + (Gy * Gy));
				
				//casting this value to int
				int gInt = (int) gDouble;
				
				//for the post processing it is important to get the max gradient
				if (max < gInt) {
					max = gInt;
				}
				
				//set the G value
				gradientMatrix[i][j] = gInt;
			}
		}
		
		//end time
		long endTime = System.currentTimeMillis();
		
		//calculate the cost of the algorithm
		long differenceTime = endTime - startTime;
		System.out.println("Request time Algorithm: " + differenceTime);
		
		//restart the time to calculate the post processing
		startTime = System.currentTimeMillis();
		
		//getting the scale to normalize all the values
		double normalizeValue = 255.0 / max;
		
		//Starts de post processing
		//normalize the values a transform the matrix into gray scale sobel operator output matrix
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				//get the value
				int grayColor = gradientMatrix[i][j];
				
				//normalizing the value
				grayColor = (int) (grayColor * normalizeValue);

				//getting the grey color R=G=B and alpha = 1 to make the image opaque
				grayColor = 0xff000000 | (grayColor << 16) | (grayColor << 8) | grayColor;
				
				//set the value
				matrix.setRGB(i, j, grayColor);
			}
		}

		//get the otuput file
		File finalFile = new File(currentDirectory + "\\sobel" + String.valueOf(System.currentTimeMillis()) +  ".png");
		//if it not exists, create it
		finalFile.createNewFile();
		//write the file
		ImageIO.write(matrix, "png", finalFile);
		
		//end time of post processing
		endTime = System.currentTimeMillis();
		
		//calculate the cost of the post processing
		differenceTime = endTime - startTime;
		System.out.println("Time of Postprocessing: " + differenceTime);
		
		System.out.println("End program");
	}

	public static int getGrayValue(int rgb) {
		//get the R + alpha value
		int red = (rgb >> 16) & 0xff;
		
		//get the G + alpha value
		int green = (rgb >> 8) & 0xff;
		
		//get the B + alpha value
		int blue = (rgb) & 0xff;

		//calculating luminance of a pixel to get the gray value
		//Colorimetric (perceptual luminance-preserving) conversion to grayscale
		int gray = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
		
		//return value
		return gray;
	}
}
