#pragma version(1)
#pragma rs java_package_name(com.even.graphiclab.parallelogram)


rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

float rate;	


uchar4 __attribute__((kernel)) invert(uchar4 in, uint32_t x, uint32_t y) {
 	uchar4 out = in;
 	out.r = in.r * rate;
 	out.g = in.g * rate;
 	out.b = in.b * rate;
 	return out;
} 
