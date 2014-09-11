package com.model.user;

import com.model.math.MatrixFactory;
import org.la4j.vector.dense.BasicVector;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by LuiSs on 09/09/2014.
 */
public class DataLogger   {

    HashMap<String, PrintWriter> files;

    public static String SEPARATOR = ",";
    public static String FORMAT = "%4.4f";

    private static DataLogger instance = null;


    private DataLogger(){
        this.files = new HashMap<>();
    }

    private void logInit(String fileName) {
        if (this.files.get(fileName)==null)
            this.addFile(fileName);
    }

    public void log(String fileName, double... args ) {
        logInit(fileName);
        String toWrite = String.valueOf(args[0]);

        for (int i=1;i<args.length;i++)
            toWrite += SEPARATOR+String.valueOf(args[i]);

        this.files.get(fileName).println(toWrite);
    }

    public void log(String fileName, BasicVector... vectors) {
        logInit(fileName);
        this.files.get(fileName).println(MatrixFactory.vectorToString(SEPARATOR,vectors));
    }



   public void closeAll() {
        for (PrintWriter p : this.files.values())
            p.close();
    }

   public void addFile(String fileName) {
       //fileName += "_"+new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())+".txt";
       String fileName_txt = fileName+ "_"+new SimpleDateFormat("ss").format(new Date())+".txt";
       try {
           this.files.put(fileName,   new PrintWriter(fileName_txt,"UTF-8"));
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }
   }

    public void removeFile(String fileName) {
        this.files.remove(fileName);
    }

    public static DataLogger getInstance() {
        if( instance==null )
            instance= new DataLogger();
        return instance;
    }


}

