//*****************************************************************************
//
// Image.cpp : Defines the class operations on images
//
// Author - Parag Havaldar
// Code used by students as starter code to display and modify images
//
//*****************************************************************************

#include "Image.h"
#include <iostream>
#include <vector>
#include <string>
using namespace std;


// Constructor and Desctructors
MyImage::MyImage() 
{
	Data = NULL;
	Width = -1;
	Height = -1;
	ImagePath[0] = 0;
}

MyImage::~MyImage()
{
	if ( Data )
		delete Data;
}


// Copy constructor
MyImage::MyImage( MyImage *otherImage)
{
	Height = otherImage->Height;
	Width  = otherImage->Width;
	Data   = new char[Width*Height*3];
	strcpy(otherImage->ImagePath, ImagePath );

	for ( int i=0; i<(Height*Width*3); i++ )
	{
		Data[i]	= otherImage->Data[i];
	}


}



// = operator overload
MyImage & MyImage::operator= (const MyImage &otherImage)
{
	Height = otherImage.Height;
	Width  = otherImage.Width;
	Data   = new char[Width*Height*3];
	strcpy( (char *)otherImage.ImagePath, ImagePath );

	for ( int i=0; i<(Height*Width*3); i++ )
	{
		Data[i]	= otherImage.Data[i];
	}
	
	return *this;

}


// MyImage::ReadImage
// Function to read the image given a path
bool MyImage::ReadImage()
{

	// Verify ImagePath
	if (ImagePath[0] == 0 || Width < 0 || Height < 0 )
	{
		fprintf(stderr, "Image or Image properties not defined");
		fprintf(stderr, "Usage is `Image.exe Imagefile w h`");
		return false;
	}
	
	// Create a valid output file pointer
	FILE *IN_FILE;
	IN_FILE = fopen(ImagePath, "rb");
	if ( IN_FILE == NULL ) 
	{
		fprintf(stderr, "Error Opening File for Reading");
		return false;
	}
	cout << "the image path we have is " << ImagePath << endl;
	// Create and populate RGB buffers
	int i;
	char *Rbuf = new char[Height*Width]; 
	char *Gbuf = new char[Height*Width]; 
	char *Bbuf = new char[Height*Width]; 

	for (i = 0; i < Width*Height; i ++)
	{
		Rbuf[i] = fgetc(IN_FILE);
	}
	for (i = 0; i < Width*Height; i ++)
	{
		Gbuf[i] = fgetc(IN_FILE);
	}
	for (i = 0; i < Width*Height; i ++)
	{
		Bbuf[i] = fgetc(IN_FILE);
	}
	cout << "start to read img " << endl;
	//for (i = 0; i < Height * Width; i++)
	//{
	//	cout << Rbuf[i] << ", " << Gbuf[i] << ", " << Bbuf[i] << endl;
	//}
	// Allocate Data structure and copy
	Data = new char[Width*Height*3];
	for (i = 0; i < Height*Width; i++)
	{
		Data[3*i]	= Bbuf[i];
		Data[3*i+1]	= Gbuf[i];
		Data[3*i+2]	= Rbuf[i];
	}

	// Clean up and return
	delete Rbuf;
	delete Gbuf;
	delete Bbuf;
	fclose(IN_FILE);

	return true;

}



// MyImage functions defined here
bool MyImage::WriteImage()
{
	// Verify ImagePath
	// Verify ImagePath
	cout << "width " << Width << ", " << Height << endl;
	if (ImagePath[0] == 0 || Width < 0 || Height < 0 )
	{
		fprintf(stderr, "Image or Image properties not defined");
		return false;
	}
	
	// Create a valid output file pointer
	FILE *OUT_FILE;
	OUT_FILE = fopen(ImagePath, "wb");
	if ( OUT_FILE == NULL ) 
	{
		fprintf(stderr, "Error Opening File for Writing");
		return false;
	}

	// Create and populate RGB buffers
	int i;
	char *Rbuf = new char[Height*Width]; 
	char *Gbuf = new char[Height*Width]; 
	char *Bbuf = new char[Height*Width]; 

	for (i = 0; i < Height*Width; i++)
	{
		Bbuf[i] = Data[3*i];
		Gbuf[i] = Data[3*i+1];
		Rbuf[i] = Data[3*i+2];
	}

	
	// Write data to file
	for (i = 0; i < Width*Height; i ++)
	{
		fputc(Rbuf[i], OUT_FILE);
	}
	for (i = 0; i < Width*Height; i ++)
	{
		fputc(Gbuf[i], OUT_FILE);
	}
	for (i = 0; i < Width*Height; i ++)
	{
		fputc(Bbuf[i], OUT_FILE);
	}
	
	// Clean up and return
	delete Rbuf;
	delete Gbuf;
	delete Bbuf;
	fclose(OUT_FILE);

	return true;

}

// Here is where you would place your code to modify an image
// eg Filtering, Transformation, Cropping, etc.
bool MyImage::Modify()
{
	cout << "prev w and h are " << Width << ", " << Height << endl;
	bool changed = false;
	int A = stoi(parameters.back()), 
		y = stoi(parameters[1]),
		u = stoi(parameters[2]),
		v = stoi(parameters[3]);
	double sw = stod(parameters[4]), sh = stod(parameters[5]);
	int w = Width * sw, h = Height * sh;
	double rh = 1 / sh, rw = 1 / sw;
	unsigned char ua, ub, uc;
	cout << "enter to change " << endl;
	// TO DO by student
	// get YUV array
	vector<vector<double>> Y(Width, vector<double>(Height)),
		U (Width, vector<double>(Height)),
		V (Width, vector<double>(Height));
	vector<double>tmp;
	int row = 0, col = 0;
	for (int i = 0; i < Width * Height; i++)
	{
		ua = Data[3 * i];//B
		ub = Data[3 * i + 1];//G
		uc = Data[3 * i + 2];//R
		//cout << (double)ua << ", " << (double)ub << ", " << (double)uc << endl;
		tmp = getYUV({(double)ua, (double)ub, (double)uc});
		Y[row][col] = (tmp[0]);
		U[row][col] = (tmp[1]);
		V[row][col] = (tmp[2]);
		col++;
		if (col == Height) {
			row++;
			col = 0;
		}
		//YUV.push_back(getYUV({ (double)ua, (double)ub, (double)uc} ));
		//cout << Y.back() << ", " << U.back() << ", " << V.back() << endl;
	}
	
	// subsampling first
	// Sub sample Y U and V separately according to the input parameters
	//1 suggesting no sub sampling and n suggesting a sub sampling by n
	vector <vector<double>>sy = getSample(Y, y);
	vector <vector<double>>su = getSample(U, u);
	vector <vector<double>>sv = getSample(V, v);
	// transfer sampling YUV to RGB
	
	vector <vector<double>>rr(Width, vector<double>(Height, 0)),
		gg(Width, vector<double>(Height, 0)),
		bb(Width, vector<double>(Height, 0));
	for (int row = 0; row < Width; row++) {
		for (int col = 0; col < Height; col++) {
			vector<double>tmp = getRGB({ sy[row][col], su[row][col], sv[row][col]});
			rr[row][col] = tmp[0];
			gg[row][col] = tmp[1];
			bb[row][col] = tmp[2];
			//cout << tmp[0] << ", " << tmp[1] << ", " << tmp[2] << endl;
		}
		
	}
	
	if (A) {
		// Blur by 3x3
		blur(rr); blur(gg); blur(bb);
	}
	// Subsample by resize
	// sample operation
	// Adjust sample values.Although samples are lost, prior to further process, all values must be interpolated in place
	// need to reset height, width of picture and data length
	
	//Width = w; Height = h;
	Data = new char[Width * Height * 3];
	int k = 0;
	for ( double i=0; i<Width; i = round( i+rw))
	{
		
		for (double j = 0; j < Height; j = round(j+rh)) {
			//if (i % rw == 0 && j % rh == 0) {
				Data[3 * k] = rr[i][j];
				Data[3 * k + 1] = gg[i][j];
				Data[3 * k + 2] = bb[i][j];
				k ++;
				//cout << rr[i][j] << ", " << gg[i][j] << ", " << bb[i][j] << endl;
			//}
		}
	}
	//for (int i = 0; i < Width * Height; i++) cout << Data[i] << ", ";
	/*The next two parameters are single precision floats Swand Sh which take positive
	values < 1.0 and control the scaled output image width and height independently.*/

	//Apply the inverse matrix to get the RGB data
	//for (int i = 0, k = 0; i < w; i++)
	//{
	//	for (int j = 0; j < h; j++) {
	//		//vector<double>tmp = getRGB({ {YUV[i][j][0]}, {YUV[i][j][1]}, {YUV[i][j][2]} });
	//		Data[k] = tmp[0];
	//		Data[k + 1] = tmp[1];
	//		Data[k + 2] = tmp[2];
	//		k += 3;
	//	}
	//}
	/*Finally a integer A(0 or 1) to suggest that antialiasing(prefiltering needs to be
	performed). 0 indicates no antialiasing and vice versa*/
	



	return changed;
}