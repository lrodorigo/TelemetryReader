package com.math;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.sparse.CCSMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

public class MatrixFactory {
	public static Factory f = new Basic2DFactory();
	
	public static Matrix identityMatrix(int n) {
		return f.createIdentityMatrix(n);
	}
	
	public static Matrix diagMatrix3(float d1, float d2, float d3) {
		Matrix out =  f.createSquareMatrix(3);
		out.set(0, 0, d1);
		out.set(1, 1, d2);
		out.set(2, 2, d3);
		return out;
	}   

	public static BasicVector vectorFromIntArray(int[] values,int n) {
		double[] temp = new double[n];
		for (int i=0;i<n;i++) 
			temp[i]=(double) values[i];
		return new BasicVector(temp);
	}
	
	public static BasicVector vector3FromIntArray(int[] values) {
		double[] temp = new double[3];
		for (int i=0;i<3;i++) 
			temp[i]=(double) values[i];
		return new BasicVector(temp);
	}

    public static Basic2DMatrix invert(Matrix m) {
        // We will use Gauss-Jordan method for inverting
        MatrixInverter inverter = m.withInverter(LinearAlgebra.GAUSS_JORDAN);
// The 'b' matrix will be dense
        return (Basic2DMatrix) inverter.inverse();
    }
	
	public static BasicVector vector3(double x, double y, double z) {
		double[] temp = new double[3];
		temp[0]=x;temp[1]=y;temp[2]=z;
		BasicVector out = new BasicVector(temp);
		return out;
	}

	public static Matrix diagMatrix3(double d1, double d2, double d3) {
		Matrix out =  f.createSquareMatrix(3);
		out.set(0, 0, d1);
		out.set(1, 1, d2);
		out.set(2, 2, d3);
		return out;
	}


    public static Matrix crossProdMatrix(BasicVector omega) {
        Matrix out =  f.createSquareMatrix(3);
        out.set(0, 1, -omega.get(2));
        out.set(0, 2, omega.get(1));

        out.set(1, 0, omega.get(2));
        out.set(1, 2, -omega.get(0));

        out.set(2, 0, -omega.get(1));
        out.set(2, 1, omega.get(0));
        return out;
    }


	public static BasicVector vectorFromString(String s) {
		
		String[] d = s.split("#");
		BasicVector out=new BasicVector(d.length);
		for (int i=0;i<d.length;i++)
			out.set(i,Double.parseDouble(d[i]));
		return out;
	}
	
	public static String vectorToString(BasicVector v) {
	 String out="";
	 for (int i=0;i<v.length();i++)
		 out += v.get(i) + "#";
	 return out;
	 
	} 

	public static String matrixToString(Matrix m) {
	
		String out="";
		
		for (int i=0;i<m.rows();i++)
			for (int j=0;j<m.columns();j++) {
				if (j==0 && i!=0) 
					  out += ";";
				else if (j!=0)
					out+="#";
				out += m.get(i,j);
			}
			
		return out;
		
	}
	
	
	public static Matrix matrixFromString(String s) {
		String[] righe = s.split(";");
		String[] valori_riga = righe[0].split("#");

		Matrix out = new Basic2DMatrix(righe.length,valori_riga.length);
		
		for (int i=0;i<righe.length;i++){
			valori_riga = righe[i].split("#");
			for (int j=0;j<valori_riga.length;j++){
				out.set(i,j, Double.parseDouble(valori_riga[j]));
			}
		}
		return out;
		
	}
	
	
 
	
}
