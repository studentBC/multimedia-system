#include <vector>
#include <iostream>
using namespace std;
#define R1 3            // number of rows in Matrix-1
#define C1 3            // number of columns in Matrix-1
#define R2 3            // number of rows in Matrix-2
#define C2 1            // number of columns in Matrix-2
extern vector<string>parameters;
static double rgbMatrix[3][3] = { {0.299, 0.587, 0.114},{0.596, -0.274, -0.322 },{ 0.211, -0.523, 0.312} };
static double yuvMatrix[3][3] = { {1.000, 0.956, 0.621},{1.000, -0.272, -0.647 },{ 1.000, -1.106, 1.703} };
vector<double> getYUV(vector<double> mat2);
vector<double> getRGB(vector<double> mat2);
vector<vector<double>> getSample(vector<vector<double>> sample, int num);
void blur(vector<vector<double>>& mat2);