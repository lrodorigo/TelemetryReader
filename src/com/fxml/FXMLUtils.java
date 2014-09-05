package com.fxml;

import java.net.URL;

/**
 * Created by LuiSs on 24/08/2014.
 */
public class FXMLUtils {
    private static FXMLUtils ourInstance = new FXMLUtils();

    public static FXMLUtils getInstance() {
        return ourInstance;
    }

    private FXMLUtils() {
    }

  public  URL getSceneURL(String sceneName) {
        return getClass().getResource("../fxml/"+sceneName+".fxml");
    }

  public  String getSceneCSS(String cssName) {
        return "com/fxml/"+cssName+".css";
    }
}
