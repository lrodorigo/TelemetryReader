package com.model.filtering;

import com.model.imureader.IMUReader;
import com.model.imureader.iDataNotifier;
import com.model.math.MatrixFactory;
import com.model.math.Quaternion;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

public class ComplementaryFilter implements iDataNotifier, iAttitudeEstimator {
	
	private IMUReader imuReader;
	private Quaternion q;
	private BasicVector b;
    private double kpA=2;
    private double ki=.5;

    private double sigmaA=0.2*0.2;


    public static BasicVector gDirection = MatrixFactory.vector3(0,0,1);
//2° 47' 11"	58° 19' 40"	24,402.2 nT	24,373.3 nT	1,186.3 nT	39,553.2 nT	46,475.0 nT

    private BasicVector magDirection =  MatrixFactory.vector3(0,0,1);
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

    public void setMagReference(BasicVector magReference) {

    }

	public void step(BasicVector omega) {
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
        double term = (imuReader.getAcc().getAcc().norm()-iAttitudeEstimator.gNorm);
        double kG = Math.exp(-((term*term)/(2*this.sigmaA)));
     //   System.out.println(kG);
        return (BasicVector) epsAcc.multiply(kG);
    }
	
	@Override
	public void notifyDataUpdate() {
			this.step(imuReader.getGyro().getOmegaRad());
	}
	
	
	
}
