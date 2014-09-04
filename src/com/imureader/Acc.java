package com.imureader;

import org.la4j.factory.Basic2DFactory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

import com.math.MatrixFactory;

public class Acc {
	  private       int[]         rawAcc   = new int[3];
	  private       BasicVector    acc      = new BasicVector();
	  public        BasicVector    zero      = MatrixFactory.vector3(58,  0, -22);
      public        Matrix  	   sens     = MatrixFactory.diagMatrix3(1/1676.453, 1/1690.928, 1/1695.617);
      
	 public void setData(int[] rawAcc) {
	    this.rawAcc = rawAcc;
	    processRawData();
	  }
	    
	  public BasicVector getAcc() {
	    return this.acc;
	  }

      public BasicVector getNormAcc() {
          return (BasicVector) this.getAcc().normalize();
      }

	  public int[] getRawAcc() {
	    return this.rawAcc;
	  }
	  
	  private void processRawData() {
		  BasicVector inizio =  MatrixFactory.vectorFromIntArray(rawAcc,3);
		  
		  this.acc = (BasicVector) sens.multiply(inizio.subtract(zero));
		 // Vector3 inizio = Vector3.fromIntArray(this.rawAcc);
		//  this.acc = (Vector3) sens.mult(inizio.minus(this.zero));
	  }
	 
	}