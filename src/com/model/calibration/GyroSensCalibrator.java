package com.model.calibration;

import com.model.imureader.IMUReader;
import com.model.imureader.iDataNotifier;
import com.model.math.MatrixFactory;
import org.la4j.vector.dense.BasicVector;

public class GyroSensCalibrator implements iDataNotifier {
	
	private BasicVector acc;
	private boolean enabled;
    private long sampleCount;
    private BasicVector lastComputedValue;

	public GyroSensCalibrator() {
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
        if (this.lastComputedValue.get(i)<0)
            this.lastComputedValue.set(i,-this.lastComputedValue.get(i));

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
