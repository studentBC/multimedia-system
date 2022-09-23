import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.lang.Exception;
import java.util.*;

class task extends TimerTask {

	private final String fn;
	private final ImageDisplay ren;
	task ( String f,  ImageDisplay ren)
	{
	  this.fn = f;
	  this.ren = ren;
	}
	public void run() {
		ren.showIms(fn);
	}
}
public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 640;//1920; // default image width and height
	int height = 480;//1080;
	// int width = 480;//1920; // default image width and height
	// int height = 640;//1080;
	static ImageDisplay ren;
	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	public ArrayList<String> preProcessFile (File[] listOfFiles) {
		ArrayList<String> fns = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) fns.add(listOfFiles[i].getAbsolutePath());
		Collections.sort(fns);
		return fns;
	}
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img)
	{
		try
		{
			int frameLength = width*height*3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y,pix);
					ind++;
				}
			}
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void showIms(String fn){

		// Read a parameter from command line
		//String param1 = args[1];
		//System.out.println("go to show img "+fn);

		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, fn, imgOne);

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(imgOne));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		frame.pack();
		frame.setVisible(true);
		//frame.dispose();
	}
	//public static void renderImage(File[] listOfFiles) throws InterruptedException {
	public void playVideo(BufferedImage img) throws InterruptedException {
		// System.out.println("go fucking schedule!");
		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(img));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		frame.pack();
		frame.setVisible(true);
	}
	//store all front end pixels into array 
	public BufferedImage removeGreenBackGround(String fn, String bn) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage bgImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, fn, img);
		readImageRGB(width, height, bn, bgImg);
		//filter out green We use the range (in hsv): (36,0,0) ~ (86,255,255)
		float[] hsv = new float[3];
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				Color c = new Color(img.getRGB(j, i));
				//RGB to HUV
				Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);
				hsv[0]*=360;
				hsv[1]*=100;
				hsv[2]*=100;
				//System.out.println(hsv[0] + ", "+ hsv[1] + ", " + hsv[2]);
				//System.out.println(c.getRed() + ", "+ c.getGreen() + ", " + c.getBlue());
				if (hsv[0] >= 95 && hsv[0] < 180 &&
					hsv[1] >= 0 && hsv[1] <= 100 &&
					hsv[2] >= 0 && hsv[2] <= 100 ) {
					//found the green then we simply use the background RGB
					//System.out.println("found the green");
					img.setRGB(j, i, bgImg.getRGB(j, i));
				}
			}
		}
		
		return img;
	}
	//current picture, next picture
	public BufferedImage substractBackGround(String fn, String bn) {
		BufferedImage cur = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage next = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, fn, cur);
		readImageRGB(width, height, bn, next);
		//filter out green We use the range (in hsv): (36,0,0) ~ (86,255,255)
		//float[] hsv = new float[3];
		int totalDiff;
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				Color cc = new Color(cur.getRGB(j, i));
				Color nc = new Color(next.getRGB(j, i));
				totalDiff = Math.abs(cc.getRed() - nc.getRed()) + 
							Math.abs(cc.getGreen() - nc.getGreen()) + 
							Math.abs(cc.getBlue() - nc.getBlue());
				//System.out.println(hsv[0] + ", "+ hsv[1] + ", " + hsv[2]);
				//System.out.println(c.getRed() + ", "+ c.getGreen() + ", " + c.getBlue());
				if (totalDiff <= 5) {
					//found the green then we simply use the background RGB
					//System.out.println("found the green");
					cur.setRGB(j, i, 52224);
				}
			}
		}
		
		return cur;
	}
	public static void main(String[] args) {
		//ImageDisplay ren = new ImageDisplay();
		//read all rgb files in that video
		ren = new ImageDisplay();
		File foreGround = new File(args[0]);
		ArrayList<String>fgFn  = ren.preProcessFile(foreGround.listFiles());
		File backGround = new File(args[1]);
		ArrayList<String>bgFn  = ren.preProcessFile(backGround.listFiles());
		int mode = Integer.parseInt(args[2]);
		System.out.println("your mode is " + mode);
		
		if (mode == 1) {
			for (int i = 0; i < fgFn.size(); i++) {
				try {
					ren.playVideo(ren.removeGreenBackGround(fgFn.get(i), bgFn.get(i)));
					Thread.sleep(41); //1000/24
					ren.frame.dispose();
				} catch (Exception e) {

				}
			}
			//ren.renderImage(listOfFiles);
		} else {
			//https://stackoverflow.com/questions/10487152/comparing-two-images-for-motion-detecting-purposes
			System.out.println("come come man");
			for (int i = 1; i < fgFn.size(); i++) {
				try {
					ren.playVideo(ren.substractBackGround(fgFn.get(i-1), fgFn.get(i)));
					Thread.sleep(41); //1000/24
					ren.frame.dispose();
				} catch (Exception e) {

				}
			}
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public JLabel getLbIm1() {
		return lbIm1;
	}

	public void setLbIm1(JLabel lbIm1) {
		this.lbIm1 = lbIm1;
	}

	public BufferedImage getImgOne() {
		return imgOne;
	}

	public void setImgOne(BufferedImage imgOne) {
		this.imgOne = imgOne;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
