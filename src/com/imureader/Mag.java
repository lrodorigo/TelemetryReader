package com.imureader;

import org.la4j.factory.Basic2DFactory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

import com.math.MatrixFactory;

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