package com.math;

import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

public class Quaternion extends BasicVector {

    public final static double R2D = 180/Math.PI;
    public final static double D2R = Math.PI/180;

    public Quaternion(double q0, double q1, double q2, double q3) {
		super(4);
		this.q0Set(q0);
		this.q1Set(q1);
		this.q2Set(q2);
		this.q3Set(q3);
		
	}
	
	
	
	public void setAll(BasicVector v) {
		this.q0Set(v.get(0));
		this.q1Set(v.get(1));
		this.q2Set(v.get(2));
		this.q3Set(v.get(3));
		
	}
	
	public Quaternion(double q0, BasicVector v) {
		super(4);
		this.q0Set(q0);
		this.q1Set(v.get(0));
		this.q2Set(v.get(1));
		this.q3Set(v.get(2));
	}
	
	
	public Quaternion quatMultiply(Quaternion b) {
		   Quaternion a = this;
		   double y0 = a.q0()*b.q0() - a.q1()*b.q1() - a.q2()*b.q2() - a.q3()*b.q3();
	        double y1 = a.q0()*b.q1() + a.q1()*b.q0() + a.q2()*b.q3() - a.q3()*b.q2();
	        double y2 = a.q0()*b.q2() - a.q1()*b.q3() + a.q2()*b.q0() + a.q3()*b.q1();
	        double y3 = a.q0()*b.q3() + a.q1()*b.q2() - a.q2()*b.q1() + a.q3()*b.q0();
	   return a;
	}

    public Basic2DMatrix toRotationMatrix() {
        Basic2DMatrix out = new Basic2DMatrix(3,3);
        out.set(0,0,q0()*q0()+q1()*q1()-q2()*q2()-q3()*q3());
        out.set(0,1,2*(q1()*q2()-q0()*q3()));
        out.set(0,2,2*(q0()*q2()+q1()*q3()));

        out.set(1,0,2*(q1()*q2()+q0()*q3()));
        out.set(1,1,q0()*q0()-q1()*q1()+q2()*q2()-q3()*q3());
        out.set(1,2,2*(q2()*q3()-q0()*q1()));

        out.set(2,0,2*(q1()*q3()-q0()*q2()));
        out.set(2,1,2*(q0()*q1()+q2()*q3()));
        out.set(2,2,q0()*q0()-q1()*q1()-q2()*q2()+q3()*q3());

        return out;
    }


    public BasicVector toEulerAnglesRad() {
        BasicVector out = new BasicVector(3);
        out.set(  0, Math.atan2(  2*( q0()*q1()+q2()*q3() ),1-2*( q1()*q1()+q2()*q2() )   )   );
        out.set(  1, Math.asin(2 * (q0() * q2() - q3() * q1()))   );
        out.set(  2, Math.atan2(  2*( q0()*q3()+q1()*q2() ),1-2*( q2()*q2()+q3()*q3() )   )   );

        return out;

    }
    public BasicVector toEulerAnglesGrad() {
        BasicVector out =  this.toEulerAnglesRad();
        return (BasicVector) out.multiply(R2D);
    }


	public Basic2DMatrix getQuaternionTwistMatrix() {
		Basic2DMatrix out = new Basic2DMatrix(4,3);
		out.set(0,0,-this.q1());
		out.set(0,1,-this.q2());
		out.set(0,2,-this.q3());
		
		out.set(1,0, this.q0());
		out.set(1,1, -this.q3());
		out.set(1,2, this.q2());
		
		out.set(2,0, this.q3());
		out.set(2,1, this.q0());
		out.set(2,2, -this.q1());
		
		out.set(3,0, -this.q2());
		out.set(3,1, this.q1());
		out.set(3,2, -this.q0());
		
		return out;
		
		
		
	}
	
	public void selfNormalize() {
		BasicVector temp = (BasicVector) this.normalize();
		this.setAll(temp);
		
	}
	
	 public Quaternion conjugate() {
	        return new Quaternion(this.q0(), -this.q1(), -this.q2(), -this.q3());
	    }
	
	 
	public Quaternion() {
		super(4);
	}
	
	
	public BasicVector getVector() {
		return (BasicVector) this.slice(0, 3);
	}
	
	public double getScalar() {
		return this.get(0);
	}
	
	public double q0() {
		return this.get(0);
	}
	public double q1() {
		return this.get(1);
	}
	public double q2() {
		return this.get(2);
	}
	public double q3() {
		return this.get(3);
	}
	
	
	public void q0Set(double q0) {
		 this.set(0,q0);
	}
	public void q1Set(double q1) {
		 this.set(1,q1);
	}
	public void q2Set(double q2) {
		 this.set(2,q2);
	}
	public void q3Set(double q3) {
		 this.set(3,q3);
	}
	
}
