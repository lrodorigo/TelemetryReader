package com.filtering;

import org.la4j.vector.dense.BasicVector;

import com.imureader.IMUReader;
import com.imureader.iDataNotifier;
import com.math.MatrixFactory;

public class OpenLoopIntegrator implements iDataNotifier {

	public BasicVector angle = MatrixFactory.vector3(0, 0, 0);
	
	@Override
	public void notifyDataUpdate() {
		// TODO Auto-generated method stub
		this.angle = (BasicVector) this.angle.add(IMUReader.getInstance().getGyro().getOmega().multiply(IMUReader.SAMPLE_TIME_SEC));
 //       System.out.println(this.angle);
    }

}
