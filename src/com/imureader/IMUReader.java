package com.imureader;

import java.util.ArrayList;

import org.la4j.vector.dense.BasicVector;

import com.math.MatrixFactory;

public class IMUReader implements iDataNotifier {
	
	public final static  int  	DATA_SIZE = 28;
	public final static float  SAMPLE_TIME_SEC = 0.040f;
	
	private SerialPacketReader r; 
	private Gyro  gyro;
	private Acc   acc;
	private Mag   mag;
	private ArrayList<iDataNotifier> subscriver;
	private float press;
	private float temp;

	
	public boolean newDataAvailable() {
		return r.newDataAvailable();
	}

    public String toString() {
        String out;
        out = "Omega: " + this.gyro.getOmega().toString() + "\t Acc:" + this.acc.getAcc().toString()+ "\nPress:" + this.press + "\t Temp:"+this.temp;
        return out;
    }

	public Gyro getGyro() {
		 updateData();
		 return this.gyro;
	}
	
	public Acc getAcc() {
		 updateData();
		 return this.acc;
	}
	
	public Mag getMag() {
		 updateData();
		 return this.mag;
	}

	public float getPress() {
		 updateData();
		return press;
	}

	public float getTemp() {
		 updateData();

		return temp;
	}

	private void updateData() {
		byte[] dati;
		
		if (this.r.newDataAvailable()) {
			dati = r.getData();
		    RawDataPacket pack = new RawDataPacket(dati);
			//System.out.println(pack.toString());
		    this.gyro.setData(pack.omega);
		    this.acc.setData(pack.acc);
		    this.mag.setData(pack.mag);
		    this.press  = pack.press;
		    this.temp 	   = pack.temp;
		}
	}
	
	private static IMUReader instance= null;
	
	private IMUReader(){
		this.r = new SerialPacketReader(DATA_SIZE);
		this.gyro = new Gyro();
		this.subscriver = new ArrayList<iDataNotifier>();
		this.acc = new Acc();
		this.mag = new Mag();
	}
	
	public static IMUReader getInstance(){
		if( instance==null )	instance= new IMUReader();
		return instance;
	}
	
	public boolean  start(String comPort, int baud) {
		return this.start(comPort,baud,2000);
	}
	public void notifyToAll() {
		for (iDataNotifier n:this.subscriver) 
			if (n!=null) 
				n.notifyDataUpdate();
		
	}
	public void subscribe(iDataNotifier notifier) {
		this.subscriver.add(notifier);
	}
	
	public void unscribe(iDataNotifier notifier) {
		this.subscriver.remove(notifier);
	}
	
	public void stop() {
		this.r.stop();
	}
	
	public boolean  start(String comPort, int baud, int timeOut) {
		if (!r.start(comPort,baud))
            return false;

		for (int i=0;i<(int) timeOut/100;i++) {
			if (r.isReady()) {
				r.setNotifier(this);
				return true;
			}
            Thread.yield();
            Utils.sleep(100);
		}
		return false;
	}
	
	
	
	public BasicVector gyroCalibrateZeroValues(int count) {
		BasicVector valori = new BasicVector(3);
		System.out.println("ENTRATO");
		for (int i=0;i<count;i++) {
			while (!newDataAvailable());
			valori = (BasicVector) valori.add(MatrixFactory.vector3FromIntArray(this.getGyro().getRawOmega()));
		}
		System.out.println("USCITO");
		valori = (BasicVector) valori.multiply( ((double)1/count) );
		this.gyro.setZero(valori);
		return valori;
	}
	
	
	public void CalibrateAcc() {
		AccelerometerCalibrator c = new AccelerometerCalibrator();
		
		
		  long startTime;

	    for (int i=0;i<6;i++) {
	    	System.out.println("Posizionarsi in posizione "+ (i+1));
	    	Utils.sleep(3000);
	    	System.out.println("Mantenere la posizione pi� fermamente possibile");	 
	    	startTime = System.currentTimeMillis();
			while ((System.currentTimeMillis()-startTime) < 5000) {
				c.waitGoodPoint(this);
				c.addPoint(this);
				System.out.println("Punto Preso");
			}
	    }
		
		System.out.println("ZERO:"+c.getZeroValues().toString());
		System.out.println("SENS:"+c.getSensValues().toString());
	}

	@Override
	public void notifyDataUpdate() {
		this.updateData();		
		this.notifyToAll();
	}
	
	
	
}
