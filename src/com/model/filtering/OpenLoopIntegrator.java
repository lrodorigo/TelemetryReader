package com.model.filtering;

import com.model.imureader.IMUReader;
import com.model.imureader.iDataNotifier;
import com.model.math.MatrixFactory;
import org.la4j.vector.dense.BasicVector;

public class OpenLoopIntegrator implements iDataNotifier {

	public BasicVector angle = MatrixFactory.vector3(0, 0, 0);
	
	@Override
	public void notifyDataUpdate() {
		this.angle = (BasicVector) this.angle.add(IMUReader.getInstance().getGyro().getOmega().multiply(IMUReader.SAMPLE_TIME_SEC));
    }

}
