package com.controller;

import com.application.MainScene;
import com.model.user.propertyHandler;
import org.la4j.vector.dense.BasicVector;

/**
 * Created by LuiSs on 05/09/2014.
 */
public abstract class MainImuAbstractController {
    protected MainScene mainScene;
    public abstract void connect() ;
    public abstract void startGyroCalibration();
    public abstract void stopGyroCalibration();
    public abstract void resetAltitudeFilter();
    public abstract void storeGyroCalibration(int i);

    public abstract void notifyDataUpdate();
    public abstract BasicVector doGyroZero();

    public void storeAltitudeSettings(double covAcc, double covBias, double covAccMis, double covPress) {
        propertyHandler.getInstance().covAcc = covAcc;
        propertyHandler.getInstance().covBias = covBias;
        propertyHandler.getInstance().covAccMis = covAccMis;
        propertyHandler.getInstance().covPress = covPress;
        propertyHandler.getInstance().write();
    }
}
