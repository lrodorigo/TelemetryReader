package com.model.imureader;

import com.model.math.MatrixFactory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.dense.BasicVector;

public class Mag {
	
	  private       int[]          rawMag   = new int[3];
	  private       BasicVector    mag      = new BasicVector();
	  public        BasicVector    zero     = MatrixFactory.vector3(0, 0, 0);;
      public        Matrix  	   sens     = MatrixFactory.diagMatrix3(1, 1, 1);
      
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