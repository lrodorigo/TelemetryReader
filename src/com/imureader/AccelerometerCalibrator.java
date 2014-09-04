package com.imureader;

import java.util.ArrayList;

import org.la4j.vector.dense.BasicVector;

import com.math.MatrixFactory;

public class AccelerometerCalibrator {
	
	private ArrayList<BasicVector> points = new ArrayList<BasicVector>();
	public final double gMag = 9.810;
	
	public void addPoint(BasicVector p) {
		this.points.add(p);
	}
	
	public void addPoint(IMUReader r) {
		this.points.add(MatrixFactory.vectorFromIntArray(r.getAcc().getRawAcc(), 3));
	}
	
	public int count() {
		return this.points.size();
	}
	
	public void waitGoodPoint(IMUReader r) {
		float x=1;
		float norm_prec = 0;
		while (Math.abs(x)>0.05) {
			while (!r.newDataAvailable());
			x = (float) (-0.05*(x + 3*Math.signum(x)*Math.abs(r.getGyro().getOmega().norm() -norm_prec) ));
			norm_prec = (float) r.getGyro().getOmega().norm();
	//		System.out.println(x);
		}
		
	}
	
	public BasicVector getZeroValues() {
		BasicVector max = MatrixFactory.vector3(points.get(0).get(0), points.get(0).get(1), points.get(0).get(2));
		BasicVector min = MatrixFactory.vector3(points.get(0).get(0), points.get(0).get(1), points.get(0).get(2));
		
		
		for (BasicVector p:this.points) {
			for (int i=0;i<3;i++){
				if (p.get(i) > max.get(i)) 
					max.set(i,p.get(i));
				if (p.get(i) < min.get(i)) 
					min.set(i,p.get(i));

			}
		}
		System.out.println("MAX:"+max.toString());
		System.out.println("MIN:"+min.toString());
		
		return new BasicVector(max.add(min).divide(2));

	}
	
	public BasicVector getSensValues() {
		BasicVector max = MatrixFactory.vector3(points.get(0).get(0), points.get(0).get(1), points.get(0).get(2));
		BasicVector min = MatrixFactory.vector3(points.get(0).get(0), points.get(0).get(1), points.get(0).get(2));
		
		
		for (BasicVector p:this.points) {
			for (int i=0;i<3;i++){
				if (p.get(i) > max.get(i)) 
					max.set(i,p.get(i));
				if (p.get(i) < min.get(i)) 
					min.set(i,p.get(i));

			}
		}
		
		return new BasicVector(max.subtract(min).divide(2*gMag));

	}
	
	
}
