package com.model.calibration;

import com.model.imureader.IMUReader;
import com.model.imureader.iDataNotifier;
import com.model.math.MatrixFactory;
import org.la4j.vector.dense.BasicVector;

public class GyroZeroCalibrator implements iDataNotifier {

	private BasicVector acc;
	private boolean enabled;
    private Long sampleCount;
    private BasicVector lastComputedValue;

	public GyroZeroCalibrator() {
		this.acc = new BasicVector(3);
		this.enabled = false;
	}
	
	public void startIntegration() {
        this.acc = new BasicVector(3);
        this.sampleCount = new Long(0);

        this.lastComputedValue = null;
        this.enabled = true;
	}
	
	public void stopIntegration() {
        this.enabled = false;
        computeCalibrationData();
	}

    public void computeCalibrationData() {
        this.lastComputedValue = (BasicVector) this.acc.divide(this.sampleCount.doubleValue());
    }

	public BasicVector getCalibrationData() {
             return this.lastComputedValue;
	}

	public void storeCalibrationData() {
		IMUReader.getInstance().getGyro().setZero(this.lastComputedValue);
	}

    public long getSampleNumber() {
        synchronized (sampleCount) {
            return sampleCount.longValue();
        }
    }

	@Override
	public void notifyDataUpdate() {
		if (this.enabled) {
			BasicVector rawSpeed = MatrixFactory.vector3FromIntArray(IMUReader.getInstance().getGyro().getRawOmega());
			this.acc = (BasicVector)  this.acc.add(rawSpeed);
            synchronized (sampleCount) {
                sampleCount++;
            }
        }
	}

	
	

}
