package com.model.filtering;

import com.model.math.Quaternion;
import org.la4j.vector.dense.BasicVector;

/**
 * Created by LuiSs on 29/08/2014.
 */
public interface iAttitudeEstimator {
    public final static double gNorm = 9.806;
    public final static BasicVector gVector=new BasicVector(new double[] {0, 0, gNorm});
    public Quaternion  getQuaternion();
}

