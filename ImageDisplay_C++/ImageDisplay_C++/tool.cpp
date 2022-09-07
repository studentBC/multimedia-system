#include <iostream>
#include <vector>
#include "tool.h"
#include <algorithm>    // std::max
//#define R1 3            // number of rows in Matrix-1
//#define C1 3            // number of columns in Matrix-1
//#define R2 3            // number of rows in Matrix-2
//#define C2 1            // number of columns in Matrix-2
//double rgb[3][3] = { {0.299, 0.587, 0.114},{0.596, -0.274, -0.322 },{ 0.211, -0.523, 0.312} };
//double yuv[3][3] = { {1.000, 0.956, 0.621},{1.000, -0.272, -0.647 },{ 1.000, -1.106, 1.703} };
int step[6] = { 0, 0, -1, 0, 1, 0 };
int diag[5] = { 1, -1, -1, 1,  1};
vector<double> getYUV(vector<double> mat2) {
	vector<double> rslt(R1,  0);

	for (int i = 0; i < R1; i++) {
		//for (int j = 0; j < C2; j++) {
			for (int k = 0; k < R2; k++) {
				rslt[i] += rgbMatrix[i][k] * mat2[k];
			}

			//we need to know yuv value range
			//rslt[i] = min(255.0, max(0.0, rslt[i]));
			//cout << rslt[i][0] << "\t";
		//}

		//cout << endl;
	}
	return rslt;
}

vector<double> getRGB(vector<double> mat2) {
	vector<double> rslt(3, 0);
	for (int i = 0; i < R1; i++) {
		//for (int j = 0; j < C2; j++) {
			//rslt[i] = 0;

			for (int k = 0; k < R2; k++) {
				rslt[i] += yuvMatrix[i][k] * mat2[k];
			}
			rslt[i] = min(255.0, max(0.0, rslt[i]));
		//}
	}
	return rslt;
}
void blur(vector<vector<double>>& tmp) {
	int row = tmp.size(), col = tmp[0].size(), x, y;
	vector<vector<double>> mat(row, vector<double>(col, 0));
	for (int i = 0; i < row; i++) {
		for (int j = 0; j < col; j++) {
			mat[i][j] = tmp[i][j];
		}
	}
	//cout << row << ", " << col << endl;
	double count = 0, sum = 0;
	for (int i = 1; i < row; i++) {
		for (int j = 0; j < col; j++) {
			sum = 0; count = 0;
			for (int k = 0; k < 5; k++) {
				x = i + step[k];
				y = j + step[k + 1];
				if (x < row && x > -1 && y > -1 && y < col) {
					//cout << x << ", " << y << endl;
					sum += mat[x][y];
					count++;
				}
			}
			for (int k = 0; k < 4; k++) {
				x = i + diag[k];
				y = j + diag[k + 1];
				if (x < row && x > -1 && y > -1 && y < col) {
					//cout << x << ", " << y << endl;
					sum += mat[x][y];
					count++;
				}
			}
			tmp[i][j] = sum/count;
		}
		//cout << endl;
	}
}
//maintain the same size of array by applying average
vector<vector<double>> getSample(vector<vector<double>> sample, int num) {
	if (num == 1) return sample;
	vector<vector<double>>ans = sample;
	int len = sample[0].size() - 1, end, prev;
	double avg = 0;
	for (int i = 0; i < sample.size(); i++) {
		for (int j = 0; j <= len; j++) {
			if (j % num == 0) {
				ans[i][j] = sample[i][j];
				prev = j;
			}
			else {
				end = min(len, prev + num);
				//if (prev == len) {
				//	if (len % num) prev = len - len % num;
				//	else prev = len - num;
				//}
				ans[i][j] = (sample[i][prev]*(end - j)  + (j - prev)*sample[i][end]) /(double)num ;
				//ans[i][prev] = ans[i][end] = (sample[i][prev] = sample[i][end]) / 2;
			}
			//if (j) {
			//	avg = (sample[i][j] + sample[i][j - num]) / 2;
			//	for (int k = j - 1; k > j - num; k--) 
			//}
			//end = j;
		}
		////if the last pixel is not selected then we just pretend the last one is selected
		//if (ans[i].back() < 0) {
		//	ans[i].back() = sample[i].back();
		//	avg = (double)(sample[i].back() + sample[i][end]) / 2;
		//	for (int k = sample[i].size() - 2; k > end ; k--) ans[i][k] = avg;
		//}
	}
	return ans;
}
