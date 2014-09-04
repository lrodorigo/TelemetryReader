package com.filtering;

import com.imureader.*;
import com.math.MatrixFactory;
import com.math.Quaternion;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

/**
 * Created by LuiSs on 25/08/2014.
 */
public class AltitudePressureFilter implements iDataNotifier {

    public double temp;

    private float T0, p0, h0;
    private double alfa, gamma;

    private Basic2DMatrix A;
    private Basic2DMatrix C;
    private Basic2DMatrix F;
    private Basic2DMatrix P;

    private Basic2DMatrix G;
    private iAttitudeEstimator attitudeEstimator;
    private BasicVector state;

    private double measPressOffset;

    private final static double a =    0.0065;
    public final static double R = 8.314;
    public final static double M = 0.02896;
    public final static double g = 9.806;

    public final static double DEFAULT_COV_ACC = .7;
    public final static double DEFAULT_COV_BIAS = 1e-4;
    public final static double DEFAULT_COV_PRESS = .1*.1;
    public final static double DEFAULT_COV_ACC_MIS = .3*.3;

    public  double covAcc    = DEFAULT_COV_ACC;
    public  double covBias   = DEFAULT_COV_BIAS;
    public  double covPress  = DEFAULT_COV_PRESS;
    public  double covAccMis = DEFAULT_COV_ACC_MIS;


    public final static double beta = (M*g)/(R*a);

    public AltitudePressureFilter() {

    }
    public AltitudePressureFilter(float t0, float p0, float h0, iAttitudeEstimator attitudeEstimator) {
        this.setT0(T0);
        this.attitudeEstimator = attitudeEstimator;
        this.p0 = p0;
        this.h0 = h0;

    //    double dt = IMUReader.getInstance().
        updateZeroParameters();
    }


    private PrintWriter printWriter;


    private void updateZeroParameters() {
        double dt = IMUReader.SAMPLE_TIME_SEC;
        //this.alfa = T0/a;
        this.gamma = a/T0;
        this.A = new Basic2DMatrix(new double[][]{
                                                { 1, dt, .5*dt*dt, 0 },
                                                { 0, 1, dt, 0 },
                                                { 0, 0, 1,0 },
                                                { 0, 0,0,1 } });
        this.C = new Basic2DMatrix(2,4);
        this.C.set(0,0,-gamma*beta*p0);
        this.C.set(1,2,1);
        this.C.set(1,3,1);
        this.P = (Basic2DMatrix) MatrixFactory.identityMatrix(4).multiply(.1);

        generateCovariances();

        this.state = new BasicVector(4);

        if (printWriter !=null)
            printWriter.close();

        try {
            printWriter =new PrintWriter("LOG "+(new Date()).getSeconds()+".txt","UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }




    }

    private void generateCovariances() {
        this.F = new Basic2DMatrix(new double[][]{  { 0, 0, 0,  0 },
                                                    { 0, 0, 0, 0 },
                                                    { 0, 0, covAcc,  0 },
                                                    { 0, 0, 0 , covBias} });

        this.G = new Basic2DMatrix(new double[][]{  {covPress,  0 },
                                                    {  0, covAccMis} });
    }

    private void prediction() {

        this.state = (BasicVector) this.A.multiply(this.state);
        this.P = (Basic2DMatrix) (A.multiply(P).multiply(A.transpose())).add(F);

    }



    public double getCovAcc() {
        return covAcc;
    }

    public void setCovAcc(double covAcc) {
        this.covAcc = covAcc;
        generateCovariances();
    }

    public double getCovBias() {
        return covBias;
    }

    public void setCovBias(double covBias) {
        this.covBias = covBias;
        generateCovariances();
    }

    public double getCovPress() {
        return covPress;
    }

    public void setCovPress(double covPress) {
        this.covPress = covPress;
        generateCovariances();
    }

    public double getCovAccMis() {
        return covAccMis;
    }

    public void setCovAccMis(double covAccMis) {
        this.covAccMis = covAccMis;
        generateCovariances();
    }



    private void correction(double measPressure, BasicVector measAcc, Quaternion attitude) {

        double acc_z_mis = (attitude.toRotationMatrix().multiply(measAcc)).subtract(iAttitudeEstimator.gVector).get(2);
        BasicVector meas = new BasicVector(new double[] {measPressure - p0, acc_z_mis});
        BasicVector residual = (BasicVector) meas.subtract(this.C.multiply(this.state));

        Basic2DMatrix K = (Basic2DMatrix) P.multiply(C.transpose()).multiply(      MatrixFactory.invert    (C.multiply(P).multiply(C.transpose()).add(G)   ));
        this.state = (BasicVector) this.state.add(K.multiply(residual));
        this.P = (Basic2DMatrix) ( MatrixFactory.identityMatrix(4).subtract(K.multiply(C)) ).multiply(P);

        this.printWriter.println(String.valueOf(measPressure-p0) +";"+ String.valueOf(acc_z_mis));


      //  System.out.println(this.F.toString());

    }

    public BasicVector getState() {
        return this.state;
    }

    public float getT0() {
        return T0;
    }

    public void setT0(float t0) {
        this.T0 = t0+273.16f;
        updateZeroParameters();

    }

    public float getP0() {
        return p0;
    }

    public void setP0(float p0) {
        this.p0 = p0;
        updateZeroParameters();

    }

    public float getH0() {
        return h0;

    }

    public void setH0(float h0) {
        this.h0 = h0;
        updateZeroParameters();

    }

    public void setZeroParameters(float h0, float p0, float T0) {
        this.h0 = h0;
        this.p0 = p0;
        this.setT0(T0);
        updateZeroParameters();

    }


    @Override
    public void notifyDataUpdate() {
        this.prediction();
        this.correction(IMUReader.getInstance().getPress(),IMUReader.getInstance().getAcc().getAcc(),this.attitudeEstimator.getQuaternion());
    }
}
