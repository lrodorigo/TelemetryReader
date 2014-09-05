package com.model.imureader;


public class Utils {
    

  public static String toString3(int[] data) {
    return data[0] + "," + data[1] + "," + data[2];
    
  }
    public static String toString3(short[] data) {
    return data[0] + "," + data[1] + "," + data[2];
    
  }
    
  public static void print3(int[] data) {
    System.out.println(toString3(data));
  }

  public static void print3(short[] data) {
	  System.out.println(toString3(data));
  }


  public static void print3(float[] data) {
	  System.out.println(data[0] + "," + data[1] + "," + data[2]);
  }
  
  public static int bytesToInt(byte hb, byte lb) {
   int out = ((int)hb << 8) | ((int)lb & 0xFF);
   return out;
  }
  
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
   public static int bytesToInt(byte b1, byte b2, byte b3, byte b4 ) {
    int out = ((0xFF & b1) << 24) | ((0xFF & b2) << 16) |   ((0xFF & b3) << 8) | (0xFF & b4); 
  return out;
  }
  
}
