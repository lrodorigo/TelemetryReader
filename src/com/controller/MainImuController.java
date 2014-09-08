package com.controller;


import com.application.MainScene;
import com.model.modelProxy;
import org.la4j.vector.dense.BasicVector;

import java.util.Timer;
import java.util.TimerTask;

public class MainImuController extends MainImuAbstractController {

    private Timer timer ;
    double time;



	public MainImuController(MainScene t) {
        t.setStatusConsole("In attesa...");
        t.getStatusLbl().requestLayout();
        this.mainScene = t;
        this.timer = new Timer();
    }

    public void connect() {
        this.mainScene.setStatusConsole("Connessione in corso...");
        modelProxy.getInstance().connect("COM35");
        this.mainScene.setStatusConsole("Connessione riuscita!");

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        },100,100);

    }

    public void updateGUI() {
        double quota = modelProxy.getInstance().getHeight();

        this.mainScene.setRis1(String.format("%4.2f m",quota));

        this.mainScene.setAltimeter(quota);
    }


    public void startGyroCalibration() {
       mainScene.appendCalTextArea("CALIBRAZIONE IN CORSO");
       mainScene.appendCalTextArea("Effettuare una rotazione di 90° lungo un asse");
        modelProxy.getInstance().startGyroCalibration();
	}
	
	public void stopGyroCalibration() {
        modelProxy.getInstance().stopGyroCalibration();
        mainScene.appendCalTextArea("VALORI CALIBRAZIONE:" + modelProxy.getInstance().getGyroCalibratorData().toString());
	}

    @Override
    public void resetAltitudeFilter() {
        modelProxy.getInstance().resetAltitudeFilter();
    }

    public void storeGyroCalibration(int i) {
        modelProxy.getInstance().storeGyroCalibration(i);
        mainScene.appendCalTextArea("VALORE MEMORIZZATO");
    }


	public void notifyDataUpdate() {

    }

    public BasicVector doGyroZero() {
       return modelProxy.getInstance().doGyroZero();
    }

    public void storeAltitudeSettings(double covAcc, double covBias, double covAccMis, double covPress) {

        modelProxy.getInstance().storeAltitudeSettings(covAcc,covBias,covAccMis,covPress);

    }


}
