package com.model.imureader;

import com.model.math.MatrixFactory;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

public class Mag {

    public static Matrix DEFAULT_MAG_SENS = new Basic2DMatrix(new double[][]{
                                                                            { 0.69, 0.007, 0.02 },
                                                                            { 0.007, 0.67, -0.019 },
                                                                            { 0.02, -0.019, 0.74 } });
    public static BasicVector DEFAULT_MAG_ZERO = MatrixFactory.vector3(85,-118, -44);


    private       int[]          rawMag   = new int[3];
    private       BasicVector    mag    = new BasicVector();
    public        BasicVector    zero   = DEFAULT_MAG_ZERO;
    public        Matrix  	     sens   = DEFAULT_MAG_SENS;



    public void setData(int[] rawMag) {
	    this.rawMag = rawMag;
	    processRawData();
	  }
	    
	  public BasicVector getMag() {
	    return this.mag;
	  }
	  
	  public int[] getRawMag() {
	    return this.rawMag;
	  }
	  
	  private void processRawData() {
		  BasicVector inizio =  MatrixFactory.vectorFromIntArray(rawMag,3);  
		  this.mag = (BasicVector) sens.multiply(inizio.subtract(zero));
	  }
	 
	}