package com.model;

import com.model.calibration.AccRoughCalibrator;
import com.model.calibration.GyroSensCalibrator;
import com.model.calibration.GyroZeroCalibrator;
import com.model.filtering.AltitudePressureFilter;
import com.model.filtering.ComplementaryFilter;
import com.model.filtering.OpenLoopIntegrator;
import com.model.imureader.IMUReader;
import com.model.imureader.Utils;
import com.model.imureader.iDataNotifier;
import com.model.math.MatrixFactory;
import com.model.math.Quaternion;
import com.model.user.DataLogger;
import com.model.user.propertyHandler;
import org.la4j.vector.dense.BasicVector;

/**
 * Created by LuiSs on 05/09/2014.
 */

public class modelProxy implements iDataNotifier {

    private IMUReader imuReader = IMUReader.getInstance();
    private ComplementaryFilter complementaryFilter;
    private GyroSensCalibrator gyroSensCalibrator;
    private AltitudePressureFilter altitudePressureFilter;


    public OpenLoopIntegrator open;

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

    public void loadProperties() {
        this.imuReader.getAcc().sens=propertyHandler.getInstance().accSens;
        this.imuReader.getAcc().zero=propertyHandler.getInstance().accZero;
        this.imuReader.getMag().sens=propertyHandler.getInstance().magSens;
        this.imuReader.getMag().zero=propertyHandler.getInstance().magZero;
        this.imuReader.getGyro().sens=propertyHandler.getInstance().gyroSens;
    }
    public void connect(String portName) {
        //     t.setStatusLabel("Connessione in corso ...");

        boolean e;

        loadProperties();
        e = imuReader.start(portName, 115200, 20000);

        if (!e) {
            return;
        }

        this.doGyroZero(200);
    //    this.calibrateAcc();

        this.complementaryFilter =  new ComplementaryFilter();
        this.gyroSensCalibrator = new GyroSensCalibrator();
        this.altitudePressureFilter = new AltitudePressureFilter(this.imuReader.getTemp(),imuReader.getPress(),0,this.complementaryFilter);
        this.open = new OpenLoopIntegrator();

        setAltitudeFilter();
        this.imuReader.subscribe(open);
        this.imuReader.subscribe(this.complementaryFilter);
        this.imuReader.subscribe(altitudePressureFilter);


        this.imuReader.subscribe(this);
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


    public void calibrateAcc() {
        String[] nomi = {"NORMALE","LED VERTICALE ALTO ", "LED VERTICALE BASSO", "PULSANTI SU","PULSANTI GIU", "PANCIA IN GIU"};

        AccRoughCalibrator cal= new AccRoughCalibrator();
        cal.startCalibration();
        System.out.println("Calibrazione Acc. Iniziata");
        for (int i=0;i<6;i++) {
            System.out.println("PUNTO "+ nomi[i]);
            cal.waitGoodPointAndAdd();
            if (i<5)
                System.out.println("POSIZIONARSI SU " + nomi[i+1]);
            Utils.sleep(6000);
        }
        cal.stopCalibration();
        System.out.println("ZERO:"+cal.getCalulatedZero());
        System.out.println("SENS:" + cal.getCalulatedSens());
        cal.storeCalibrationValues();
    }

    public void startGyroCalibration() {
        this.imuReader.subscribe(gyroSensCalibrator);
        this.gyroSensCalibrator.startIntegration();
    }

    public void stopGyroCalibration() {
        this.gyroSensCalibrator.stopIntegration();
        this.gyroSensCalibrator.computeCalibrationData(90);
        this.imuReader.unscribe(gyroSensCalibrator);
    }

    public BasicVector getGyroCalibratorData() {
        return this.gyroSensCalibrator.getCalibrationData();
    }


    public void storeGyroCalibration(int i) {
        this.gyroSensCalibrator.storeCalibrationData(i);
        writeAllProperties();
    }

    public void writeAllProperties() {
        propertyHandler prop = propertyHandler.getInstance();
        prop.gyroSens = this.imuReader.getGyro().sens;
        prop.accSens = this.imuReader.getAcc().sens;
        prop.accZero = this.imuReader.getAcc().zero;
        prop.magSens = this.imuReader.getMag().sens;
        prop.magZero = this.imuReader.getMag().zero;
        prop.covAcc = altitudePressureFilter.getCovAcc();
        prop.covBias = altitudePressureFilter.getCovBias();
        prop.covAccMis = altitudePressureFilter.getCovAccMis();
        prop.covPress = altitudePressureFilter.getCovPress();
        prop.write();
    }

    @Override
    public void notifyDataUpdate() {
        DataLogger.getInstance().log("MAG",imuReader.getMag().getMag());
        DataLogger.getInstance().log("MAG_RAW", MatrixFactory.vector3FromIntArray(imuReader.getMag().getRawMag()));
        DataLogger.getInstance().log("MAG_RAW_QUAT", MatrixFactory.vector3FromIntArray(imuReader.getMag().getRawMag()),this.complementaryFilter.getQuaternion());

    }

    public void doGyroZero(int count) {
        System.out.println("Calibrazione");
        BasicVector out;
        GyroZeroCalibrator zero=new GyroZeroCalibrator();
        zero.startIntegration();
        this.imuReader.subscribe(zero);
        while (zero.getSampleNumber()<count);
        zero.stopIntegration();
        zero.storeCalibrationData();
        IMUReader.getInstance().getGyro().setZero(zero.getCalibrationData());
        this.imuReader.unscribe(zero);
        System.out.println("Terminata: "+zero.getCalibrationData().toString());
    }

    public void storeAltitudeSettings(double covAcc, double covBias, double covAccMis, double covPress) {

        if (this.altitudePressureFilter!=null) {
            this.altitudePressureFilter.setCovAcc(covAcc);
            this.altitudePressureFilter.setCovBias(covBias);
            this.altitudePressureFilter.setCovAccMis(covAccMis);
            this.altitudePressureFilter.setCovPress(covPress);
        }

        writeAllProperties();
    }

    public BasicVector getMag() {
        return this.imuReader.getMag().getMag();
    }

    public String getImuString() {
        return this.imuReader.toString();
    }
}
