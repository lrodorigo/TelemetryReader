package com.controller;


import com.application.MainScene;
import com.model.modelProxy;
import org.la4j.vector.dense.BasicVector;

public class MainImuController extends MainImuAbstractController {


	public MainImuController(MainScene t) {
        t.getStatusLbl().setText("KLHJDASKDHJKAS");
        t.getStatusLbl().requestLayout();
        this.mainScene = t;
    }

    public void connect() {
        modelProxy.getInstance().connect();
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
