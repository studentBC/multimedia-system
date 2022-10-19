
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class JPG2000 {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 512; // default image width and height
	int height = 512;
	// Constants as used by Gregoire P.
	double alpha = -1.586134342;
	double beta = -0.05298011854;
	double gamma = 0.8829110762;
	double delta = 0.4435068522;
	double zeta = 1.149604398;
	int N = width*height;
	ArrayList<Double>ICT = new ArrayList<>(); //Irreversible Color Transform
	double x[];
	double Y [];
	double yy[][];
	double Cb [];
	double cb[][];
	double Cr [];
	double cr[][];
	double matrix[][] = {
		{0.299, 0.587, 0.114},
		{-0.16875, -0.33126, 0.5},
		{0.5, -0.41869, -0.08131}
	};
	double inverse[][] = {
		{1, 0, 1.402},
		{1.0, -0.34413, -0.71414},
		{1.0, 1.772, 0.0}
	};
	//https://web.archive.org/web/20120305164605/http://www.embl.de/~gpau/misc/dwt97.c
	//https://github.com/accord-net/framework/blob/master/Sources/Accord.Math/Wavelets/CDF97.cs#L103
	/**
 *  fwt97 - Forward biorthogonal 9/7 wavelet transform (lifting implementation)
 *
 *  x is an input signal, which will be replaced by its output transform.
 *  n is the length of the signal, and must be a power of 2.
 *
 *  The first half part of the output signal contains the approximation coefficients.
 *  The second half part contains the detail coefficients (aka. the wavelets coefficients).
 *
 *  See also iwt97.
 */
double[] fwt97(double[] x, int start, int end) {
	double a;
	int i;
  
	// Predict 1
	a=-1.586134342;
	for (i=start+1;i<end-2;i+=2) {
	  x[i]+=a*(x[i-1]+x[i+1]);
	} 
	x[end-1]+=2*a*x[end-2];
  
	// Update 1
	a=-0.05298011854;
	for (i=start+2;i<end;i+=2) {
	  x[i]+=a*(x[i-1]+x[i+1]);
	}
	x[start]+=2*a*x[start+1];
  
	// Predict 2
	a=0.8829110762;
	for (i=start+1;i<end-2;i+=2) {
	  x[i]+=a*(x[i-1]+x[i+1]);
	}
	x[end-1]+=2*a*x[end-2];
  
	// Update 2
	a=0.4435068522;
	for (i=start+2;i<end;i+=2) {
	  x[i]+=a*(x[i-1]+x[i+1]);
	}
	x[start]+=2*a*x[start+1];
  
	// Scale
	a=1/1.149604398;
	for (i=start;i<end;i++) {
	  if (i%2 == 1) x[i]*=a;
	  else x[i]/=a;
	}
  
	// Pack
	double tempbank[] = new double[end];
	for (i=start;i<end;i++) {
	  if (i%2==0) tempbank[i/2]=x[i];
	  else tempbank[end/2+i/2]=x[i];
	}
	//return tempbank;
	for (i=start;i<end;i++) x[i]=tempbank[i];
	return x;
  }
  
  /**
   *  iwt97 - Inverse biorthogonal 9/7 wavelet transform
   *
   *  This is the inverse of fwt97 so that iwt97(fwt97(x,n),n)=x for every signal x of length n.
   *
   *  See also fwt97.
   */
  double[] iwt97(double[] x, int start, int end) {
	double a;
	int i, half = (end+start)/2, j = 0;
  
	// Unpack
	double tempbank[] = new double[half*2];
	//System.out.println(end+" " + half + " " + start);
	//if (tempbank==0) tempbank=(double *)malloc(n*sizeof(double));
	for (i=start, j = 0;i<half;i++, j++) {
	  tempbank[j*2]=x[j+start];
	  tempbank[j*2+1]=x[j+half];
	}
	for (i=start, j = 0;i<end;i++, j++) x[i]=tempbank[j];
  
	// Undo scale
	a=1.149604398;
	for (i=start;i<end;i++) {
	  if (i%2 == 1) x[i]*=a;
	  else x[i]/=a;
	}
  
	// Undo update 2
	a=-0.4435068522;
	for (i=start+2;i<end;i+=2) {
	  x[i]+=a*(x[i-1]+x[i+1]);
	}
	x[start]+=2*a*x[start+1];
  
	// Undo predict 2
	a=-0.8829110762;
	for (i=start+1;i<end-2;i+=2) {
	  x[i]+=a*(x[i-1]+x[i+1]);
	}
	x[end-1]+=2*a*x[end-2];
  
	// Undo update 1
	a=0.05298011854;
	for (i=start+2;i<end;i+=2) {
	  x[i]+=a*(x[i-1]+x[i+1]);
	}
	x[start]+=2*a*x[start+1];
  
	// Undo predict 1
	a=1.586134342;
	for (i=start+1;i<end-2;i+=2) {
	  x[i]+=a*(x[i-1]+x[i+1]);
	} 
	x[end-1]+=2*a*x[end-2];
	return x;
  }
	double[][] fwt2d(double[][] x, int width, int height)
	{
		for (int j = 0; j < width; j++)
		{
			// Predict 1
			for (int i = 1; i < height - 1; i += 2)
				x[i][j] += alpha * (x[i - 1][j] + x[i + 1][j]);
			x[height - 1][j] += 2 * alpha * x[height - 2][j];

			// Update 1
			for (int i = 2; i < height; i += 2)
				x[i][j] += beta * (x[i - 1][j] + x[i + 1][j]);
			x[0][j] += 2 * beta * x[1][j];

			// Predict 2
			for (int i = 1; i < height - 1; i += 2)
				x[i][j] += gamma * (x[i - 1][j] + x[i + 1][j]);
			x[height - 1][j] += 2 * gamma * x[height - 2][j];

			// Update 2
			for (int i = 2; i < height; i += 2)
				x[i][j] += delta * (x[i - 1][j] + x[i + 1][j]);
			x[0][j] += 2 * delta * x[1][j];
		}

		// Pack
		var tempbank = new double[width][height];
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if ((i % 2) == 0)
					tempbank[j][i / 2] = (1 / zeta) * x[i][j];
				else
					tempbank[j][ i / 2 + height / 2] = (zeta / 2) * x[i][j];
			}
		}

		for (int i = 0; i < width; i++)
			for (int j = 0; j < width; j++)
				x[i][j] = tempbank[i][j];

		return x;
	}

	double[][] iwt2d(double[][] x, int width, int height)
	{
		// Unpack
		var tempbank = new double[width][height];
		for (int j = 0; j < width / 2; j++)
		{
			for (int i = 0; i < height; i++)
			{
				tempbank[j * 2][i] = zeta * x[i][j];
				tempbank[j * 2 + 1][ i] = (2 / zeta) * x[i][ j + width / 2];
			}
		}

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				x[i][ j] = tempbank[i][j];


		for (int j = 0; j < width; j++)
		{
			// Undo update 2
			for (int i = 2; i < height; i += 2)
				x[i][ j] -= delta * (x[i - 1][ j] + x[i + 1][j]);
			x[0][ j] -= 2 * delta * x[1][j];

			// Undo predict 2
			for (int i = 1; i < height - 1; i += 2)
				x[i][j] -= gamma * (x[i - 1][ j] + x[i + 1][ j]);
			x[height - 1][ j] -= 2 * gamma * x[height - 2][ j];

			// Undo update 1
			for (int i = 2; i < height; i += 2)
				x[i][ j] -= beta * (x[i - 1][ j] + x[i + 1][ j]);
			x[0][j] -= 2 * beta * x[1][ j];

			// Undo predict 1
			for (int i = 1; i < height - 1; i += 2)
				x[i][ j] -= alpha * (x[i - 1][ j] + x[i + 1][ j]);
			x[height - 1][ j] -= 2 * alpha * x[height - 2][ j];
		}

		return x;
	}
	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img)
	{
		try
		{
			int len = width*height;
			int frameLength = len*3;
			Y = new double[len];
			Cb = new double[len];
			Cr = new double[len];
			yy = new double[height][width];
			cb = new double[height][width];
			cr = new double[height][width];
			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			byte[] bytes = new byte[frameLength];

			raf.read(bytes);

			int ind = 0, i = 0;
			for(int y = 0; y < height; y++)
			{
				for(int j = 0; j < width; j++)
				{
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2];
					double R = r ,G = g, B = b; 
					yy[j][y] = Y[i] = R*matrix[0][0]+ G*matrix[0][1]+ B*matrix[0][2];
					cb[j][y] = Cb[i] = R*matrix[1][0]+ G*matrix[1][1]+ B*matrix[1][2];
					cr[j][y] = Cr[i]= R*matrix[2][0]+ G*matrix[2][1]+ B*matrix[2][2];

					ICT.add(Y[i]);
					ICT.add(Cb[i]);
					ICT.add(Cr[i]);
					
					// int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					// //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					// img.setRGB(j,y,pix);
					ind++;
					i++;
				}
			}
			//showIms(0, len, 512);
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
	double[][] FWT97(double[][] data, int levels)
	{
		int w = data.length;
		int h = data[0].length;

		for (int i = 0; i < levels; i++)
		{
			fwt2d(data, w, h);
			fwt2d(data, w, h);
			w >>= 1;
			h >>= 1;
		}

		return data;
	}

	/// <summary>
	///   Inverse biorthogonal 9/7 2D wavelet transform
	/// </summary>
	/// 
	double[][] IWT97(double[][] data, int levels)
	{
		int w = data.length;
		int h = data[0].length;

		for (int i = 0; i < levels - 1; i++)
		{
			h >>= 1;
			w >>= 1;
		}

		for (int i = 0; i < levels; i++)
		{
			data = iwt2d(data, w, h);
			data = iwt2d(data, w, h);
			h <<= 1;
			w <<= 1;
		}

		return data;
	}
	public void showIms(int start, int end, int len) {

		//https://en.wikipedia.org/wiki/JPEG_2000
		//https://stackoverflow.com/questions/19621847/java-rgb-color-space-to-ycrcb-color-space-conversion
		//first transfer it to Y Cr Cb
		imgOne = new BufferedImage(len, len, BufferedImage.TYPE_INT_RGB);
		System.out.println(len+ " : " + start + " : " + Y.length);
		int k = start, r, g, b;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				r = (int)(Y[k]*inverse[0][0]+Cb[k]*inverse[0][1]+Cr[k]*inverse[0][2]);
				g = (int)(Y[k]*inverse[1][0]+Cb[k]*inverse[1][1]+Cr[k]*inverse[1][2]);
				b = (int)(Y[k]*inverse[2][0]+Cb[k]*inverse[2][1]+Cr[k]*inverse[2][2]);
				int val = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    			imgOne.setRGB(j,i,val);//it just like scan row by row from col 1 to n
				k++;
			}
		}
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

	public void show2D(int len) {

		//https://en.wikipedia.org/wiki/JPEG_2000
		//https://stackoverflow.com/questions/19621847/java-rgb-color-space-to-ycrcb-color-space-conversion
		//first transfer it to Y Cr Cb
		imgOne = new BufferedImage(len, len, BufferedImage.TYPE_INT_RGB);
		//System.out.println(len+ " : " + start + " : " + Y.length);
		int r, g, b;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				r = (int)(yy[j][i]*inverse[0][0]+cb[j][i]*inverse[0][1]+cr[j][i]*inverse[0][2]);
				g = (int)(yy[j][i]*inverse[1][0]+cb[j][i]*inverse[1][1]+cr[j][i]*inverse[1][2]);
				b = (int)(yy[j][i]*inverse[2][0]+cb[j][i]*inverse[2][1]+cr[j][i]*inverse[2][2]);
				int val = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    			imgOne.setRGB(j,i,val);//it just like scan row by row from col 1 to n
			}
		}
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
	public static void main(String[] args) {
		JPG2000 j2 = new JPG2000();
		// Read a parameter from command line
		//String param1 = args[1];
		//System.out.println("The second parameter was: " + param1);
		int mode = Integer.parseInt(args[1]), len = j2.width;
		//Progressive Encoding-Decoding Implementation
		/*This is when n = -1. In this case you will go through the creation of the entire DWT representation till level 0. 
		Then decode each level recursively and display the output. The first display will be at level 0, then level 1 and so on till you reach level 9. 
		You should see the image progressively improving with details.*/
		Boolean presentAll = false;
		if (mode < 0) {
			mode = 9;
			presentAll = true;
		}
		// Read in the specified image
		j2.imgOne = new BufferedImage(j2.width, j2.height, BufferedImage.TYPE_INT_RGB);
		j2.readImageRGB(j2.width, j2.height, args[0], j2.imgOne);
		int start = 0, end = len*len, mid;


		j2.yy = j2.FWT97(j2.yy, mode);
		j2.cb = j2.FWT97(j2.cb, mode);
		j2.cr = j2.FWT97(j2.cr, mode);
		j2.show2D(j2.width);
		for (int i = 0; i < mode; i++) {
			j2.yy = j2.IWT97(j2.yy, mode-i);
			j2.cb = j2.IWT97(j2.cb, mode-i);
			j2.cr = j2.IWT97(j2.cr, mode-i);
			j2.show2D(j2.width);
		}
		
		/*for 1 d array transform
		for (int i = 0; i < mode; i++) {
			//quadratic so every time cut it for four
			mid = (end+start)/2;
			//first cut it horizontal
			j2.Y = j2.fwt97(j2.Y, start, end);
			j2.Cb = j2.fwt97(j2.Cb, start, end);
			j2.Cr = j2.fwt97(j2.Cr, start, end);
			j2.showIms(start, end, len);

			//cut it vertical for left part
			System.out.println(start);
			j2.Y = j2.fwt97(j2.Y, start, mid);
			j2.Cb = j2.fwt97(j2.Cb, start, mid);
			j2.Cr = j2.fwt97(j2.Cr, start, mid);
			//j2.showIms(start, end, len);
			//cut it vertical for right part
			//System.out.print(mid+", "+start + ", " + end);
			j2.Y = j2.fwt97(j2.Y, mid, end);
			j2.Cb = j2.fwt97(j2.Cb, mid, end);
			j2.Cr = j2.fwt97(j2.Cr, mid, end);
			//we can only use the right bottom one for next cut
			//j2.showIms(start, end, len);
			start = mid;
			
			len>>=1;
		}
		//show the LL each time
		for (int i = 0; i < mode; i++) {
			start = end-len;
			mid = start+(end-start)/2;
			//first cut it horizontal
			j2.iwt97(j2.Y, start, end);
			j2.iwt97(j2.Cb, start, end);
			j2.iwt97(j2.Cr, start, end);
			//cut it vertical for left part
			j2.iwt97(j2.Y, start, mid);
			j2.iwt97(j2.Cb, start, mid);
			j2.iwt97(j2.Cr, start, mid);
			//cut it vertical for right part
			j2.iwt97(j2.Y, mid, end);
			j2.iwt97(j2.Cb, mid, end);
			j2.iwt97(j2.Cr, mid, end);
			//transfer back to RGB and draw on screen?
			//j2.showIms(start, end, len);
			len<<=1;
		}
		//j2.showIms(args);
		*/
	}

}
