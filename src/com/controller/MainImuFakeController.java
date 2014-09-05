package com.controller;


import com.application.MainScene;
import com.model.imureader.Gyro;
import org.la4j.vector.dense.BasicVector;

import java.util.Timer;
import java.util.TimerTask;

public class MainImuFakeController extends MainImuAbstractController  {

    private Timer timer ;
    double time;

	public MainImuFakeController(MainScene t) {
        this.mainScene = t;
        this.timer = new Timer();


    }

    public void connect() {
        System.out.println("Ci sono");
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fakeGUIGenerator();
                time += .1;
            }
        },100,100);
    }

    public void fakeGUIGenerator() {
        this.mainScene.setRis1(String.valueOf(time));
        this.mainScene.rotateOrizzonte(Math.sin(2*Math.PI*time*1/10)*(1/Gyro.D2R));
        if (((long) (time/.1) % 100)== 0)
            this.mainScene.pushChartData(String.valueOf(time),Math.sin(2*Math.PI*time*1/15)*100);

    }

    public void setupAltitudeFilter() {

    }


    public void startGyroCalibration() {

	}

	public void stopGyroCalibration() {

	}

    public void storeGyroCalibration(int i) {

    }

	@Override
	public void notifyDataUpdate() {

    }

    public BasicVector doGyroZero() {
       return new BasicVector(3);
    }




}
