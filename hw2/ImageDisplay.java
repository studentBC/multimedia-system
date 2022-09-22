
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Exception;

// class task extends TimerTask {

//     private final int next;


//     task ( int next )
//     {
//       this.next = next;
//     }

//     public void run() {
// 		for (i = 0; i < next; i++) {
// 			ren.showIms(listOfFiles[i].getName());
// 			//System.out.println("File " + listOfFiles[i].getName());
// 		}
//     }
// }
public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 480;//1920; // default image width and height
	int height = 640;//1080;
	static ImageDisplay ren;
	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
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
		//System.out.println("The second parameter was: " + param1);

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
	}
	public static void renderImage(File[] listOfFiles) throws InterruptedException {

		//ImageDisplay ren = new ImageDisplay();
		//final int i = 1, next = 24;
		TimerTask task = new TimerTask() {
			// final int next = 24;
			// final int i = 0;
		 	public void run() {
		 		//System.out.println("Task performed on " + new Date());
				int next = 24;
				for (int i = 0; i < next; i++) {
					ren.showIms(listOfFiles[i].getName());
					//System.out.println("File " + listOfFiles[i].getName());
				}
		 		next+=24;
		 	}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 60*1000L, 60*1000L);
		Thread.sleep(1000L * 20); //20 seconds
		timer.cancel(); 
		
		
	}
	public static void main(String[] args) {
		//ImageDisplay ren = new ImageDisplay();
		//read all rgb files in that video
		File folder = new File(args[0]);
		File[] listOfFiles = folder.listFiles();
		ren = new ImageDisplay();
		try {
			renderImage(listOfFiles);
		} catch(Exception e) {
			//throw(e);
		}
		//ren.showIms(args);
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
