package com.imureader;

import java.util.Date;

        
        
public class RawDataPacket {
  public   Date   timeStamp;
  public   int[]  omega;
  public   int[]  acc;
  public   int[]  mag;
  public   float  press;
  public   float  temp;
  public   int      id;   
  
  public final int PACKET_SIZE = 28;
  
   public RawDataPacket(byte[] packet) {
           
     for (int i=0;i<PACKET_SIZE;i++)
       packet[i] = (byte) (packet[i]&0xff);
     
      Date d=new Date();
      this.timeStamp = d;
      this.omega = new int[3];
      this.acc = new int[3];
      this.mag = new int[3];
      
      this.omega[0] =  Utils.bytesToInt(packet[0],packet[1] ) ;
      this.omega[1] =  Utils.bytesToInt(packet[2],packet[3] ) ;
      this.omega[2] =  Utils.bytesToInt(packet[4],packet[5] ) ;
      this.acc[0]   =  Utils.bytesToInt(packet[6],packet[7] ) ;
      this.acc[1]   =  Utils.bytesToInt(packet[8],packet[9] ) ;
      this.acc[2]   =  Utils.bytesToInt(packet[10],packet[11] ) ;
      this.mag[0]   =  Utils.bytesToInt(packet[12],packet[13] ) ;
      this.mag[1]   =  Utils.bytesToInt(packet[14],packet[15] ) ;
      this.mag[2]   =  Utils.bytesToInt(packet[16],packet[17] ) ;

      this.press    =  ( (float) Utils.bytesToInt(packet[18],packet[19],packet[20],packet[21]) )/100;
      this.temp     =  ((float) Utils.bytesToInt(packet[22],packet[23] ))/100 ;
      this.id       =  Utils.bytesToInt(packet[24],packet[25],packet[26],packet[27]);
   }
   
   public String toString() {
     String out;
     out = "#" + this.id + "\n";
     out += "Omega:" + Utils.toString3(this.omega) + "\t Acc:"+  Utils.toString3(this.acc) + "\t Mag:"+ Utils.toString3(this.mag) + "\nPress:"+this.press+"\tTemp:"+this.temp;
     return out;
   }
   
}