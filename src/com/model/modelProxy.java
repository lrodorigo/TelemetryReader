package com.model;

import com.model.calibration.GyroCalibrator;
import com.model.filtering.AltitudePressureFilter;
import com.model.filtering.ComplementaryFilter;
import com.model.imureader.IMUReader;
import com.model.imureader.iDataNotifier;
import com.model.math.Quaternion;
import com.model.user.propertyHandler;
import org.la4j.vector.dense.BasicVector;

/**
 * Created by LuiSs on 05/09/2014.
 */

public class modelProxy implements iDataNotifier {

    private IMUReader imuReader = IMUReader.getInstance();
    private ComplementaryFilter complementaryFilter;
    private GyroCalibrator gyroCalibrator;
    private AltitudePressureFilter altitudePressureFilter;

    private static modelProxy instance;


    public double getHeight() {
        return this.altitudePressureFilter.getState().get(0);
    }

    public double getPressure() {
        return this.imuReader.getPress();
    }

    public double getTemp() {
        return this.imuReader.getTemp();
    }

    public BasicVector getRollPitchYaw() {
        return this.complementaryFilter.getQuaternion().toEulerAnglesGrad();
    }

    public BasicVector getAcceleration() {
        return this.imuReader.getAcc().getAcc();
    }

    public BasicVector getAngularRate() {
        return this.imuReader.getGyro().getOmega();
    }


    public Quaternion getAttitude() {
        return this.complementaryFilter.getQuaternion();
    }


    private modelProxy() {

    }

    public static modelProxy getInstance() {
        if (instance==null)
            instance = new modelProxy();
        return instance;

    }



    public void connect(String portName) {
        //     t.setStatusLabel("Connessione in corso ...");

        boolean e;
        e = imuReader.start(portName, 115200, 20000);

        if (!e) {
            return;
        }
        (new Runnable() {
            @Override
            public void run() {
                IMUReader.getInstance().gyroCalibrateZeroValues(50);
            }
        }).run();

        this.complementaryFilter =  new ComplementaryFilter();
        this.gyroCalibrator = new GyroCalibrator();
        this.altitudePressureFilter = new AltitudePressureFilter(this.imuReader.getTemp(),imuReader.getPress(),0,this.complementaryFilter);

        setAltitudeFilter();
        this.imuReader.subscribe(this.complementaryFilter);
        this.imuReader.subscribe(gyroCalibrator);
        this.imuReader.subscribe(altitudePressureFilter);
  //      this.imuReader.subscribe(this);
    }

    public void setAltitudeFilter() {
        this.altitudePressureFilter.setCovAcc(propertyHandler.getInstance().covAcc);
        this.altitudePressureFilter.setCovAccMis(propertyHandler.getInstance().covAccMis);
        this.altitudePressureFilter.setCovBias(propertyHandler.getInstance().covBias);
        this.altitudePressureFilter.setCovPress(propertyHandler.getInstance().covPress);
    }

    public void resetAltitudeFilter() {
        this.altitudePressureFilter.resetFilter();
    }



    public void startGyroCalibration() {
        this.gyroCalibrator.startIntegration();
    }

    public void stopGyroCalibration() {
        this.gyroCalibrator.stopIntegration();
        this.gyroCalibrator.computeCalibrationData(90);
    }

    public BasicVector getGyroCalibratorData() {
        return this.gyroCalibrator.getCalibrationData();
    }


    public void storeGyroCalibration(int i) {
        this.gyroCalibrator.storeCalibrationData(i);
        com.model.user.propertyHandler.getInstance().write();
    }

    @Override
    public void notifyDataUpdate() {
   /*

    */
    }

    public BasicVector doGyroZero() {
        BasicVector out;
        out=IMUReader.getInstance().gyroCalibrateZeroValues(50);
        return out;
    }

    public void storeAltitudeSettings(double covAcc, double covBias, double covAccMis, double covPress) {

        if (this.altitudePressureFilter!=null) {
            this.altitudePressureFilter.setCovAcc(covAcc);
            this.altitudePressureFilter.setCovBias(covBias);
            this.altitudePressureFilter.setCovAccMis(covAccMis);
            this.altitudePressureFilter.setCovPress(covPress);
        }

        propertyHandler.getInstance().covAcc = covAcc;
        propertyHandler.getInstance().covBias = covBias;
        propertyHandler.getInstance().covAccMis = covAccMis;
        propertyHandler.getInstance().covPress = covPress;

        propertyHandler.getInstance().write();
    }


}
