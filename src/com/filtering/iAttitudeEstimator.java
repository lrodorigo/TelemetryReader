package com.filtering;

import com.math.Quaternion;
import org.la4j.vector.dense.BasicVector;

/**
 * Created by LuiSs on 29/08/2014.
 */
public interface iAttitudeEstimator {
    public final static BasicVector gVector=new BasicVector(new double[] {0, 0,9.81});
    public Quaternion  getQuaternion();
}

