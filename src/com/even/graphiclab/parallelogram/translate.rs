#pragma version(1)
#pragma rs java_package_name(com.even.graphiclab.parallelogram)


rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

int d;	


uchar4 __attribute__((kernel)) invert(uchar4 in, uint32_t x, uint32_t y) {
	const void* p = rsGetElementAt(gIn, x + d, y);
	uchar4 out;
	if(p!=0){
 		out = *((uchar4*)p);
 	}else{
 		out = in;
 	}
 	return out;
} 