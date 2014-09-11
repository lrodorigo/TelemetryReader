package com.controller.instruments;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Created by LuiSs on 05/09/2014.
 */
public class AltimeterController {


    public ImageView altLancCorta;
    public ImageView altLancLunga;
    public ImageView altBase;

    public AnchorPane altimeterMainPane;

    public final static double SCALE_FACTOR = 1;
    public final static double CORTA_FACTOR = SCALE_FACTOR/10;

    public AltimeterController() {
        System.out.println("Controller creato");
    }

    public void setHeight(double height) {
        if (height<0)
            height = -height;
        final double lancCorta = height/ SCALE_FACTOR;
        final double lancLunga = ( height - ( ((int)(lancCorta/ SCALE_FACTOR))* SCALE_FACTOR) )/CORTA_FACTOR;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                altLancCorta.setRotate(lancCorta*18);
                altLancLunga.setRotate(lancLunga * 18);
            }
        });

    }

    public void setSize(double size) {

        this.altLancCorta.setFitWidth(size);
        this.altLancCorta.setFitHeight(size);
        this.altLancLunga.setFitWidth(size);
        this.altLancLunga.setFitHeight(size);
        this.altLancCorta.setFitWidth(size);
        this.altBase.setFitHeight(size);
        this.altBase.setFitWidth(size);


    }

}
