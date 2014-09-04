package com.user;

import java.io.*;
import java.util.Properties;

import com.filtering.AltitudePressureFilter;
import org.la4j.matrix.Matrix;
import org.la4j.vector.dense.BasicVector;

import com.imureader.Acc;
import com.imureader.Gyro;
import com.imureader.IMUReader;
import com.imureader.Mag;
import com.imureader.SerialPacketReader;
import com.math.MatrixFactory;

public class propertyHandler {
	
	
	/* PROPERTY LIST */
	public	Matrix gyroSens;
	private final static String GYRO_SENS = "gyroSens";
	
	public	Matrix accSens;
	private final static String ACC_SENS = "accSens";
	
	public	BasicVector accZero;
	private final static String ACC_ZERO = "accZero";

	public	Matrix magSens;
	private final static String MAG_SENS = "magSens";

    public	BasicVector magZero;
    private final static String MAG_ZERO = "magZero";

    public	double covAcc;
    private final static String COV_ACC = "covAcc";
    public	double covBias;
    private final static String COV_BIAS = "covBias";
    public	double covAccMis;
    private final static String COV_ACC_MIS = "covAccMis";
    public	double covPress;
    private final static String COV_PRESS = "covPress";




	
	/* END */
	
	
	private static propertyHandler instance= null;
	private Properties prop;
	public final static String FILE_NAME = "config.properties";
	
	private propertyHandler(){
		
		prop = new Properties();
		
	}
	
	
	public void loadProperties() {
		File configFile = new File(FILE_NAME);
		 
		try {
			if (configFile.exists()) {
			    FileReader reader = new FileReader(configFile);
			    prop.load(reader);
			    reader.close();
	
			    readFromStructure();
			} else
				loadDefaultProperties();

			
		    
		} catch (IOException ex) {
		    // I/O error
		}
	}
	
	
	public void loadDefaultProperties() {
		// TODO Auto-generated method stub
		Gyro g = new Gyro();
		this.gyroSens = g.sens;
		Acc a = new Acc();
		this.accSens = a.sens;
		this.accZero = a.zero;
		Mag m = new Mag();
		this.magSens = m.sens;
		this.magZero = m.zero;


        this.covAcc = AltitudePressureFilter.DEFAULT_COV_ACC;
        this.covBias = AltitudePressureFilter.DEFAULT_COV_BIAS;
        this.covAccMis = AltitudePressureFilter.DEFAULT_COV_ACC_MIS;
        this.covPress = AltitudePressureFilter.DEFAULT_COV_PRESS;

		this.write();

	}


	public void write() {
		File configFile = new File("config.properties");
		
		writeToStructure();
		
		try {
			FileWriter writer = new FileWriter(configFile);
			prop.store(writer, "AppSettings");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void writeToStructure() {
		prop.setProperty(this.GYRO_SENS, MatrixFactory.matrixToString(this.gyroSens));
        prop.setProperty(this.ACC_SENS, MatrixFactory.matrixToString(this.accSens));
        prop.setProperty(this.ACC_ZERO, MatrixFactory.vectorToString(this.accZero));
        prop.setProperty(this.MAG_SENS, MatrixFactory.matrixToString(this.magSens));
        prop.setProperty(this.MAG_ZERO, MatrixFactory.vectorToString(this.magZero));

        prop.setProperty(this.COV_ACC, String.valueOf(this.covAcc));
        prop.setProperty(this.COV_ACC_MIS,String.valueOf(this.covAccMis));
        prop.setProperty(this.COV_BIAS,String.valueOf(this.covBias));
        prop.setProperty(this.COV_PRESS, String.valueOf(this.covPress));


    }
	
	private void readFromStructure() {

		this.gyroSens = MatrixFactory.matrixFromString(prop.getProperty(this.GYRO_SENS));
		
		this.accSens = MatrixFactory.matrixFromString(prop.getProperty(this.ACC_SENS));
		this.accZero = MatrixFactory.vectorFromString(prop.getProperty(this.ACC_ZERO));

		this.magSens = MatrixFactory.matrixFromString(prop.getProperty(this.MAG_SENS));
		this.magZero = MatrixFactory.vectorFromString(prop.getProperty(this.MAG_ZERO));

        this.covAcc = Double.parseDouble(prop.getProperty(this.COV_ACC,Double.toString(AltitudePressureFilter.DEFAULT_COV_ACC)));
        this.covAccMis = Double.parseDouble(prop.getProperty(this.COV_ACC_MIS,Double.toString(AltitudePressureFilter.DEFAULT_COV_ACC_MIS)));
        this.covBias = Double.parseDouble(prop.getProperty(this.COV_BIAS,Double.toString(AltitudePressureFilter.DEFAULT_COV_BIAS)));
        this.covPress = Double.parseDouble(prop.getProperty(this.COV_PRESS,Double.toString(AltitudePressureFilter.DEFAULT_COV_PRESS)));
    }
	


	public static propertyHandler getInstance(){
		if( instance==null )	
			instance= new propertyHandler();
		return instance;
	}
	
	
}
