package com.controller;


import com.application.MainScene;
import com.calibration.GyroCalibrator;
import com.filtering.AltitudePressureFilter;
import com.filtering.ComplementaryFilter;
import com.filtering.OpenLoopIntegrator;
import com.imureader.IMUReader;
import com.imureader.iDataNotifier;
import com.user.propertyHandler;
import org.la4j.vector.dense.BasicVector;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainImuController implements iDataNotifier {

    private IMUReader imuReader = IMUReader.getInstance();
	private ComplementaryFilter complementaryFilter;
	private GyroCalibrator gyroCalibrator;
    private AltitudePressureFilter altitudePressureFilter;
	private MainScene mainScene;

	
	public MainImuController(MainScene t) {
        t.getStatusLbl().setText("KLHJDASKDHJKAS");
        t.getStatusLbl().requestLayout();
        this.imuReader = IMUReader.getInstance();
        this.mainScene = t;
    }

    public void connect() {
        //     t.setStatusLabel("Connessione in corso ...");

        boolean e;
        e = imuReader.start("COM33", 115200, 20000);

        if (!e) {
            this.mainScene.setStatusLabel("Errore di connessione");
            return;
        }

        this.mainScene.setStatusLabel("Inizio Calibrazione Giroscopi");

        this.mainScene.getStatusLbl().textProperty().setValue("Calibrazione Giroscopi");
        (new Runnable() {
            @Override
            public void run() {
                IMUReader.getInstance().gyroCalibrateZeroValues(50);
            }
        }).run();

        this.mainScene.setStatusLabel("Calibrazione giroscopi effettuata");


        this.openLoop = new OpenLoopIntegrator();
        this.complementaryFilter =  new ComplementaryFilter();
        this.gyroCalibrator = new GyroCalibrator();

        setupAltitudeFilter();


        this.imuReader.subscribe(this.complementaryFilter);
        this.imuReader.subscribe(gyroCalibrator);
        //  this.imuReader.subscribe(this.openLoop);
        this.imuReader.subscribe(altitudePressureFilter);

        this.imuReader.subscribe(this);
    }

    public void setupAltitudeFilter() {
        this.imuReader.unscribe(this.altitudePressureFilter);
        this.altitudePressureFilter = new AltitudePressureFilter(this.imuReader.getTemp(),imuReader.getPress(),0,this.complementaryFilter);
        this.altitudePressureFilter.setCovAcc(propertyHandler.getInstance().covAcc);
        this.altitudePressureFilter.setCovAccMis(propertyHandler.getInstance().covAccMis);
        this.altitudePressureFilter.setCovBias(propertyHandler.getInstance().covBias);
        this.altitudePressureFilter.setCovPress(propertyHandler.getInstance().covPress);
        this.imuReader.subscribe(this.altitudePressureFilter);
    }


    public void startGyroCalibration() {
       mainScene.appendCalTextArea("CALIBRAZIONE IN CORSO");
       mainScene.appendCalTextArea("Effettuare una rotazione di 90° lungo un asse");
       this.gyroCalibrator.startIntegration();
	}
	
	public void stopGyroCalibration() {
		this.gyroCalibrator.stopIntegration();
        this.gyroCalibrator.computeCalibrationData(90);
        mainScene.appendCalTextArea("VALORI CALIBRAZIONE:" + this.gyroCalibrator.getCalibrationData());
	}

    public void storeGyroCalibration(int i) {
        this.gyroCalibrator.storeCalibrationData(i);
        com.user.propertyHandler.getInstance().write();

    }

	@Override
	public void notifyDataUpdate() {
        double quota = altitudePressureFilter.getState().get(0);

        SimpleDateFormat sdf = new SimpleDateFormat(); // creo l'oggetto

      // CC  // primo pattern: 2009, 12, 09
        sdf.applyPattern("HH:mm:ss");

        this.mainScene.setRis1(complementaryFilter.getQuaternion().toString());
        this.mainScene.setRis3("dh:" + String.valueOf(quota));

        this.mainScene.setRis2(this.complementaryFilter.getQuaternion().toEulerAnglesGrad().toString());
        this.mainScene.setRis4(this.imuReader.toString());
        Date q = new Date();
        this.mainScene.pushChartData(sdf.format(q),quota);
        this.mainScene.rotateOrizzonte(this.complementaryFilter.getQuaternion().toEulerAnglesGrad().get(0));
    }

    public BasicVector doGyroZero() {
        BasicVector out;
        this.mainScene.appendCalTextArea("MANTENENERE LA IMU PIU' FERMA POSSIBILE");
        out=IMUReader.getInstance().gyroCalibrateZeroValues(50);
        this.mainScene.appendCalTextArea("CALIBRAZIONE TERMINATA");
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

/*
    ScheduledService<void> svc = new ScheduledService<>(Duration.seconds(1)) {
        protected Task<Document> createTask() {
            return new Task<Document>() {
                protected Document call() {
                    // Connect to a Server
                    // Get the XML document
                    // Parse it into a document
                    return document;
                }
            }
        }
    }

*/


}
