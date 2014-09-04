package com.imureader;

import org.la4j.factory.Basic2DFactory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

import com.math.MatrixFactory;

public class Gyro {
	  private       int[]          rawOmega   = new int[3];
	  private       BasicVector    omega      = new BasicVector();

	  public        BasicVector    zero     = MatrixFactory.vector3(0, 0, 0);
      public        Matrix  	   sens     = MatrixFactory.diagMatrix3(0.0144, 0.0144, 0.0142);
      
      public final static double D2R = Math.PI/180;
      
	 public BasicVector getZero() {
		return zero;
	}

	public void setZero(BasicVector zero) {
		this.zero = zero;
	}

	public BasicVector getOmegaRad() {
		return (BasicVector) this.omega.multiply(D2R);
	}
	
	public void setData(int[] rawOmega) {
	    this.rawOmega = rawOmega;
	    processRawData();
	  }
	    
	  public BasicVector getOmega() {
	    return this.omega;
	  }
	  
	  public int[] getRawOmega() {
	    return this.rawOmega;
	  }
	  
	  private void processRawData() {
		  BasicVector inizio =  MatrixFactory.vectorFromIntArray(rawOmega,3);
	//	  System.out.println(inizio.toString());
		  this.omega = (BasicVector) sens.multiply(inizio.subtract(zero));
		 // Vector3 inizio = Vector3.fromIntArray(this.rawGyro);
		//  this.gyro = (Vector3) sens.mult(inizio.minus(this.zero));
	  }
	 
	}