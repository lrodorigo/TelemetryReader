package com.filtering;

import com.math.MatrixFactory;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

import com.imureader.IMUReader;
import com.imureader.Utils;
import com.imureader.iDataNotifier;
import com.math.Quaternion;

public class ComplementaryFilter implements iDataNotifier, iAttitudeEstimator {
	
	private IMUReader imuReader;
	private Quaternion q;
	private BasicVector b;
    private double kpA=5;
    private double ki=2;

    public static BasicVector gDirection = MatrixFactory.vector3(0,0,1);

    private static Matrix Qacc  = new Basic2DMatrix(new double[][]{{ 1.0, 0.0, 0.0 },
                                                                    { 0.0, 1.0, 0.0 },
                                                                    { 0.0, 0.0, 0.0 }});

	public  Quaternion getQuaternion() {
		return this.q;
	}
	
	public ComplementaryFilter(Quaternion q0) {
		this.q = q0;
		this.b = new BasicVector(3);
		this.imuReader = IMUReader.getInstance();
	}
	
	public ComplementaryFilter() {
		this.q = new Quaternion(1, 0, 0, 0);
		this.b = new BasicVector(3);
		this.imuReader = IMUReader.getInstance();

	}

	public void step(BasicVector omega) {
	//	System.out.println(this.q.toString());
        BasicVector eps = computeError();
        BasicVector omega_bar = (BasicVector) omega.subtract(this.b).add(eps.multiply(kpA));

		BasicVector m = (BasicVector) this.q.getQuaternionTwistMatrix().multiply(omega_bar).multiply(.5*this.imuReader.SAMPLE_TIME_SEC);
		BasicVector out = (BasicVector) this.q.add(m);
		this.q.setAll(out);
		this.q.selfNormalize();

        BasicVector delta_b = (BasicVector)  eps.multiply(-ki*this.imuReader.SAMPLE_TIME_SEC);
        this.b = (BasicVector) this.b.add(delta_b);

	}


    private BasicVector computeError() {
        Matrix R = this.q.toRotationMatrix();
        Matrix R_t = R.transpose();
        BasicVector epsAcc = (BasicVector) ( MatrixFactory.crossProdMatrix(imuReader.getAcc().getNormAcc()) ).multiply(R_t).multiply(gDirection);
        epsAcc = (BasicVector) R_t.multiply(Qacc).multiply(R).multiply(epsAcc);
        return epsAcc;
    }
	
	@Override
	public void notifyDataUpdate() {
			this.step(imuReader.getGyro().getOmegaRad());
	}
	
	
	
}
