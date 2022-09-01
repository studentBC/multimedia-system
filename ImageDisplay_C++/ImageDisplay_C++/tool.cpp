#include <iostream>
#include <vector>
#include "tool.h"
//#define R1 3            // number of rows in Matrix-1
//#define C1 3            // number of columns in Matrix-1
//#define R2 3            // number of rows in Matrix-2
//#define C2 1            // number of columns in Matrix-2
//double rgb[3][3] = { {0.299, 0.587, 0.114},{0.596, -0.274, -0.322 },{ 0.211, -0.523, 0.312} };
//double yuv[3][3] = { {1.000, 0.956, 0.621},{1.000, -0.272, -0.647 },{ 1.000, -1.106, 1.703} };
vector<vector<double>> getYUV(vector<vector<double>> mat2) {
	vector<vector<double>> rslt(R1, vector<double>(C2, 0));

	for (int i = 0; i < R1; i++) {
		for (int j = 0; j < C2; j++) {
			rslt[i][j] = 0;
			for (int k = 0; k < R2; k++) {
				rslt[i][j] += rgb[i][k] * mat2[k][j];
			}

			//cout << rslt[i][j] << "\t";
		}

		//cout << endl;
	}
	return rslt;
}

vector<double> getRGB(vector<vector<double>> mat2) {
	vector<double> rslt(3, 0);
	for (int i = 0; i < R1; i++) {
		for (int j = 0; j < C2; j++) {
			rslt[i] = 0;

			for (int k = 0; k < R2; k++) {
				rslt[i] += yuv[i][k] * mat2[k][j];
			}

			//cout << rslt[i][j] << "\t";
		}

		//cout << endl;
	}
	return rslt;
}
