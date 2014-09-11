package com.model.imureader;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Enumeration;
//import SerialPort.SerialPort;


public class SerialPacketReader implements Runnable {

	
	private byte[]   data;
	private int     dataSize;
	private boolean running;
	private boolean started;
	private boolean newData;
	private iDataNotifier notifier=null;
	private SerialPort serial;
	
	long lastData;

	public final static char    START_CHAR = 'S';
	public final static char    END_CHAR = 'E';
	
	private Thread readThread;
	
	public SerialPacketReader(int dataSize) {
		this.dataSize = dataSize;
		this.started = false;
		
	}
	
	public synchronized boolean start(String serialPortName, int baudRate) {
		CommPortIdentifier port = this.getPortByName(serialPortName);
		
		try {
		//	System.out.println("in");

			this.serial = (SerialPort) port.open("name", 1500);
            this.serial.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			this.serial.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            this.serial.setInputBufferSize(64000);

			this.readThread = new Thread(this,"SERIAL-READER");
			this.running = true;
		//	System.out.println("runn");
		//	serialWrite(100);
			
			this.readThread.start();
            return true;
			
		} catch (PortInUseException e) {
			System.out.println("PORT " + serialPortName + " IS IN USE");
			return false;
		} catch (UnsupportedCommOperationException e) {
            return false;
        } catch (Exception e) {
            return false;
		}  
		    
		
	}
	
	
	private CommPortIdentifier getPortByName(String name) {
	    Enumeration ports = CommPortIdentifier.getPortIdentifiers();
	    
	    while (ports.hasMoreElements()) {
	      CommPortIdentifier port = (CommPortIdentifier)ports.nextElement();	 
	      if (port.getPortType() == CommPortIdentifier.PORT_SERIAL && port.getName().equals(name)) 
	    	  return port;
	      
	    }
	    
	    System.out.println("ERROR!! UNABLE TO FIND PORT "+name);
	    return null;
	}
	
	public byte[] getLastData() {
		 synchronized (this.data) {
				return this.data;			
		}
	}
	
	public void stop() {
		this.running = false;
		this.serial.close();
	}
	
	private void serialWrite(int data) {
		try {
			serial.getOutputStream().write(data);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	@Override
	public void run() {
		int data_in;
		
		InputStream inStream = null;
		try {
			inStream = this.serial.getInputStream();
		} catch (IOException e1) {
			System.out.println("ERROR WITH SERIAL PORT STREAM");
		}
		
		
		System.out.println("Starting data reception");
		this.data = new byte[this.dataSize];
		byte[] dati = new byte[this.dataSize+1];
		int ricevuti;
		
		
		while (this.running) {
			try {
				while(inStream.read()!=((int)START_CHAR) && this.running);
				ricevuti = 0;			
				inStream.read(dati, 0, this.dataSize+1);
		
				if (dati[this.dataSize] == SerialPacketReader.END_CHAR) {			
					synchronized (this.data) {		
						for (int i=0;i<dataSize;i++) 
							this.data[i] = dati[i] ;
						this.started = true;
						this.newData=true;
						if (this.notifier!=null)
							this.notifier.notifyDataUpdate();
					}
				} else 
						System.out.println("Invalid Sequence");
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("USCITO!");
	}


	public boolean newDataAvailable() {
		return this.newData;
	}
	
	public static  String intToString(byte[] dati) {
		String out = "--";
		for (int i=0;i<dati.length;i++) {
			out += (dati[i]) + "\n";
		}
		return out;			
	}
	
	public synchronized boolean isReady()  {
		return this.started;
	}
	private static String byteToString(byte[] bytes) {
		String out = "--";
		for (int i=0;i<bytes.length;i++) {
			out += bytes[i] + "\n";
		}
		return out;			
	}
	
	public  byte[] getData() {

		synchronized (this.data) {
			this.newData=false;
			return this.data;
		}
		
		
	}
	
	
	public iDataNotifier getNotifier() {
		return notifier;
	}

	public void setNotifier(iDataNotifier notifier) {
		this.notifier = notifier;
	}
	
	
	
	private int intFromTwoByteArray(byte byteOne , byte byteTwo ) {
		byte[] arr = {byteTwo, byteOne };
		ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
		return (int) wrapped.getShort(); // 1
	}
	
	

}
