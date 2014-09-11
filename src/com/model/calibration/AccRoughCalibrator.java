package com.model.calibration;

import com.model.imureader.IMUReader;
import com.model.imureader.iDataNotifier;
import com.model.math.MatrixFactory;
import org.la4j.vector.dense.BasicVector;

import java.util.ArrayList;

public class AccRoughCalibrator implements iDataNotifier {

    private ArrayList<BasicVector> points = new ArrayList<BasicVector>();
    private boolean enabled;
    private Boolean puntoDisponibile = new Boolean(false);

    private BasicVector lastCalcSens, lastCalcZero;


    private double x = 1;
    private float norm_prec = 0;

    public static final double NORM_THRESHOLD = 0.02;
    public static final double gMag = 9.806;

    public void addPoint(BasicVector p) {
        this.points.add(p);
    }

    public void startCalibration() {
        this.points = new ArrayList<BasicVector>();
        this.puntoDisponibile = new Boolean(false);
        this.lastCalcSens = null;
        this.lastCalcZero = null;
        this.enabled = true;
    }

    public void stopCalibration() {
        this.enabled = false;
        this.getZeroValues();
        this.getSensValues();
    }

    public void storeCalibrationValues() {
        IMUReader.getInstance().getAcc().zero = this.lastCalcZero;
        IMUReader.getInstance().getAcc().sens = MatrixFactory.diagMatrix3(1/this.lastCalcSens.get(0),
                                                                            1/this.lastCalcSens.get(1),
                                                                            1/this.lastCalcSens.get(2));

    }

    public BasicVector getCalulatedSens() {
        return this.lastCalcSens;
    }

    public BasicVector getCalulatedZero() {
        return this.lastCalcZero;
    }





    public int getPointsNumber() {
        return this.points.size();
    }

    public void waitGoodPointAndAdd() {
        BasicVector punto;
        while (!puntoDisponibile) {
            punto = MatrixFactory.vector3FromIntArray(IMUReader.getInstance().getAcc().getRawAcc());
            if (validaPunto(punto)) {
                this.addPoint(punto);
                System.out.println("Punto Aggiunto");
                synchronized (puntoDisponibile) {
                    puntoDisponibile = Boolean.valueOf(false);
                }
                return;
            }
        }

    }

    private boolean validaPunto(BasicVector p) {
        if (Math.abs(p.get(0))>16200) return true;
        if (Math.abs(p.get(1))>16200) return true;
        if (Math.abs(p.get(2))>16200) return true;
        return false;
    }


    private void getZeroValues() {
        BasicVector max = MatrixFactory.vector3(points.get(0).get(0), points.get(0).get(1), points.get(0).get(2));
        BasicVector min = MatrixFactory.vector3(points.get(0).get(0), points.get(0).get(1), points.get(0).get(2));


        for (BasicVector p:this.points) {
            for (int i=0;i<3;i++){
                if (p.get(i) > max.get(i))
                    max.set(i,p.get(i));
                if (p.get(i) < min.get(i))
                    min.set(i,p.get(i));

            }
        }
        System.out.println("MAX:"+max.toString());
        System.out.println("MIN:"+min.toString());

        this.lastCalcZero =  new BasicVector(max.add(min).divide(2));

    }

    private void getSensValues() {
        BasicVector max = MatrixFactory.vector3(points.get(0).get(0), points.get(0).get(1), points.get(0).get(2));
        BasicVector min = MatrixFactory.vector3(points.get(0).get(0), points.get(0).get(1), points.get(0).get(2));

        for (BasicVector p:this.points) {
            for (int i=0;i<3;i++){
                if (p.get(i) > max.get(i))
                    max.set(i,p.get(i));
                if (p.get(i) < min.get(i))
                    min.set(i,p.get(i));

            }
        }
        this.lastCalcSens = new  BasicVector(max.subtract(min).divide(2*gMag));
    }



    @Override
    public void notifyDataUpdate() {
        if (this.enabled) {
            IMUReader r = IMUReader.getInstance();
            x = (float) (-0.05 * (x + 3 * Math.signum(x) * Math.abs(r.getGyro().getOmega().norm() - norm_prec)));
            norm_prec = (float) r.getGyro().getOmega().norm();
            synchronized (puntoDisponibile) {
                puntoDisponibile = Boolean.valueOf(x < NORM_THRESHOLD);
            }
        }
    }
	
	

}
