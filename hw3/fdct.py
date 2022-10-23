# Python program showing 
# Graphical representation of 
# cos() function 
import math
import numpy as np

input =[
    [188, 180, 155, 149, 179, 116, 86, 96],
    [168, 179, 168, 174, 180, 111, 86, 95],
    [150, 166, 175, 189, 165, 101, 88, 97],
    [163, 165, 179, 184, 135, 90, 91, 96],
    [170, 180, 178, 144, 102, 87, 91, 98],
    [175, 174, 141, 104, 85, 83, 88, 96],
    [153, 134, 105, 82, 83, 87, 92, 96],
    [117, 104, 86, 80, 86, 90, 92, 103]
]
Qt =[
    [16, 11, 10, 16, 24, 40, 51, 61],
    [12, 12, 14, 19, 26, 58, 60, 55],
    [14, 13, 16, 24, 40, 57, 69, 56],
    [14, 17, 22, 29, 51, 87, 80, 62],
    [18, 22, 37, 56, 68, 109, 103, 77],
    [24, 35, 55, 64, 81, 104, 113, 92],
    [49, 64, 78, 87, 103, 121, 120, 101],
    [72, 92, 95, 98, 112, 100, 103, 99]
]
def getSuv(u: int, v: int)-> int:
    s = 0.0
    for i in range(0, 8):
        for j in range(0, 8):
            if u == 0 and v == 0:
                s+=input[i][j]/4 * (1/2)
            elif u == 0 or v == 0:
                s+=(input[i][j]*math.cos((2*i+1)*u*np.pi/16)*math.cos((2*j+1)*v*np.pi/16))/4 * (1/math.sqrt(2))
            else:
                s += (input[i][j]*math.cos((2*i+1)*u*np.pi/16)*math.cos((2*j+1)*v*np.pi/16))/4
    return s 

ans = 0.0
for i in range(0,8):
    for j in range(0, 8):
        print('{:>4}'.format(round(getSuv(i,j))), end = ", ")
    print()
print()              
print()              
for i in range(0,8):
    for j in range(0, 8):
        print('{:>4}'.format(round(getSuv(i,j)/Qt[i][j])), end = ", ")
    print()


