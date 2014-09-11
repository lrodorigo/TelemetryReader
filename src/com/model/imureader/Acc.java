package com.model.imureader;

import com.model.math.MatrixFactory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.dense.BasicVector;

public class Acc {

    public static Matrix DEFAULT_ACC_SENS = MatrixFactory.diagMatrix3(1/1676.453, 1/1690.928, 1/1695.617);
    public static BasicVector DEFAULT_ACC_ZERO = MatrixFactory.vector3(58,  0, -22);


    private       int[]         rawAcc   = new int[3];
	  private       BasicVector    acc      = new BasicVector();
	  public        BasicVector    zero      = DEFAULT_ACC_ZERO;
      public        Matrix  	   sens     = DEFAULT_ACC_SENS;
      
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