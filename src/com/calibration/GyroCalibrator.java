package com.calibration;

import org.la4j.vector.dense.BasicVector;

import com.imureader.IMUReader;
import com.imureader.iDataNotifier;
import com.math.MatrixFactory;

public class GyroCalibrator implements iDataNotifier {
	
	private BasicVector acc;
	private boolean enabled;

    private BasicVector lastComputedValue;

	
	public GyroCalibrator() {
		this.acc = new BasicVector(3);
		this.enabled = false;
	}
	
	public void startIntegration() {
        this.acc = new BasicVector(3);
        this.lastComputedValue = null;
        this.enabled = true;
	}
	
	public void stopIntegration() {
        this.enabled = false;
	}

    public void computeCalibrationData(double angle) {
        this.lastComputedValue = (BasicVector) this.acc.multiply(1/angle);
    }

	public BasicVector getCalibrationData() {
             return this.lastComputedValue;
	}


	
	public void storeCalibrationData(int i) {
		IMUReader.getInstance().getGyro().sens.set(i,i, (1/this.lastComputedValue.get(i)));
	}
	
	@Override
	public void notifyDataUpdate() {
		if (this.enabled) {
			BasicVector rawSpeed = MatrixFactory.vector3FromIntArray(IMUReader.getInstance().getGyro().getRawOmega());
			rawSpeed = (BasicVector) rawSpeed.subtract(IMUReader.getInstance().getGyro().zero);
			this.acc = (BasicVector) this.acc.add(rawSpeed.multiply(IMUReader.SAMPLE_TIME_SEC));
        }
	}

	
	

}
