package com.application;


import com.controller.Airplane3DImporter;
import com.controller.MainImuAbstractController;
import com.controller.MainImuController;
import com.controller.MainImuFakeController;
import com.controller.instruments.AltimeterController;
import com.fxml.FXMLUtils;
import com.model.user.DataLogger;
import com.model.user.propertyHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScene extends Application implements Initializable {

    public Label statusConsole;

    public CheckBox checkboxDatiSimulati;
    public TextField txtPortaSeriale;
    public GridPane instrumentsPane;
    //  public Rectangle orizzonte;
    @FXML
    private TextField covStatAccTxt;
    @FXML
    private TextField covBiasAccTxt;
    @FXML
    private TextField covMisPressTxt;
    @FXML
    private TextField covMisAccTxt;
    @FXML
    private Label conStatusLbl;
    @FXML
    private Label ris4;
    @FXML
    private Label ris1;
    @FXML
    private Label ris3;
    @FXML
    private Label ris2;
    @FXML
    private LineChart chart;
    @FXML
    private Button startGyroCal;
    @FXML
    private Button stopGyroCal;
    @FXML
    private TextArea calTextArea;
    private XYChart.Series chartSeries;

    private Scene scene;

    //private MainImuController mainImuController;
    private MainImuAbstractController mainImuController;

    private AltimeterController altimeterController;

    private double sceneWidth= 1200;
    private double sceneHeight= 800;


    /*
    public void resizeGridImagesWidth(final double width){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StackPane sp= (StackPane) ((AnchorPane)instrumentsPane.getChildren().get(0)).getChildren().get(0);

                for( Node n: sp.getChildren()) {
                    ImageView iv= (ImageView) n;
                    //iv.setFitWidth((width/3)-50);

                }
            }
        });
    }
    */


    public void resizeGridImagesWidth(final double stageWidth){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StackPane sp= (StackPane) ((AnchorPane)instrumentsPane.getChildren().get(0)).getChildren().get(0);
                for( Node n: sp.getChildren()) {
                    ImageView iv= (ImageView) n;
                    DoubleProperty db= new SimpleDoubleProperty(stageWidth/3 - 50);
                    iv.fitWidthProperty().bind(db);
                }
            }
        });

    }


    @Override
    public void initialize(URL location, ResourceBundle resources)   {
        propertyHandler.getInstance().loadProperties();
        setupGUI();

        FXMLLoader  ff = new FXMLLoader();

        this.mainImuController  = new MainImuController(this);


        FXMLLoader loader = new FXMLLoader(FXMLUtils.getInstance().getSceneURL("instruments/altimeter"));
        try {
            instrumentsPane.getChildren().set(0,(Node) loader.load());
            resizeGridImagesWidth(this.sceneWidth);

            //resizeGridImagesWidth(this.sceneWidth);
            //iv.fitWidthProperty().bind(((AnchorPane)instrumentsPane.getChildren().get(0)).widthProperty());
            //System.out.println(iv);
            //iv.fitWidthProperty().bind(instrumentsPane.widthProperty());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.altimeterController = loader.getController();

    }

    public void setAltimeter(double h) {
        this.altimeterController.setHeight(h);
    }

    public void setStatusConsole(final String t) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusConsole.setText(t);
            }
        });
    }
    public void setRis1(final String t) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ris1.setText(t);
            }
        });
    }

    public void setRis2(final String t) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ris2.setText(t);
            }
        });
    }

    public void setRis3(final String t) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ris3.setText(t);
            }
        });
    }

    public void setRis4(final String t) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ris4.setText(t);
            }
        });
    }

    public void appendCalTextArea(String t) {
        this.calTextArea.appendText(t + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(FXMLUtils.getInstance().getSceneURL("MainScene"));
        scene = new Scene(root, this.sceneWidth, this.sceneHeight);
        scene.getStylesheets().add(FXMLUtils.getInstance().getSceneCSS("MainScene"));

        primaryStage.setTitle("IMU Telemetry Reader v1.0");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                DataLogger.getInstance().closeAll();
                mainImuController.writeProperties();
                Platform.exit();
                System.exit(0);
            }
        });



        /*
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                resizeGridImagesWidth(newSceneWidth.doubleValue());
            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("WINDOW HEIGHT RESIZED");
            }
        });
        */

        primaryStage.show();
    }





    public Label getStatusLbl() {
        return conStatusLbl;
    }

    public void setStatusLabel(final String t) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                conStatusLbl.setText(t);
                //      conStatusLbl.textProperty().setValue();
            }
        });
    }


    private void setupGUI() {
        covBiasAccTxt.setText(Double.toString(propertyHandler.getInstance().covBias));
        covMisAccTxt.setText(Double.toString(propertyHandler.getInstance().covAccMis));
        covStatAccTxt.setText(Double.toString(propertyHandler.getInstance().covAcc));
        covMisPressTxt.setText(Double.toString(propertyHandler.getInstance().covPress));
        setupChart();
    }


    public void pushChartData(final String x, final double y) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chartSeries.getData().add(new XYChart.Data(x, y));
            }
        });
    }


    public void setupChart() {
        this.chart.setTitle("Quota [m]");
        CategoryAxis xAxis= (CategoryAxis) this.chart.getXAxis();
        xAxis.setLabel("Ora");
        NumberAxis yAxis= (NumberAxis) this.chart.getYAxis();
        yAxis.setLabel("Quota");

        this.chartSeries = new XYChart.Series();
        this.chart.getData().add(this.chartSeries);
    }

    public void rotateOrizzonte(final double angle) {
       /* Platform.runLater(new Runnable() {
            @Override
            public void run() {
                orizzonte.setRotate(angle);
            }
        });*/

    }

    public void connettiPress(ActionEvent actionEvent) {
        if (this.checkboxDatiSimulati.isSelected())
            this.mainImuController  = new MainImuFakeController(this);
        else
            this.mainImuController  = new MainImuController(this);

        this.mainImuController.connect();
    }

    @FXML
    public void storeGyroZAxis(ActionEvent actionEvent) {
        mainImuController.storeGyroCalibration(2);
    }

    @FXML
    public void storeGyroYAxis(ActionEvent actionEvent) {
        mainImuController.storeGyroCalibration(1);
    }

    @FXML
    public void storeGyroXAxis(ActionEvent actionEvent) {
        mainImuController.storeGyroCalibration(0);
    }

    @FXML
    public void startGyroCalAction(ActionEvent actionEvent) {
        mainImuController.startGyroCalibration();
    }

    @FXML
    public void stopGyroCalAction(ActionEvent actionEvent) {
        mainImuController.stopGyroCalibration();
    }


    public void gyroZeroAction(ActionEvent actionEvent) {
        mainImuController.doGyroZero();
    }

    public void storePressParam(ActionEvent actionEvent) {
        this.mainImuController.storeAltitudeSettings(Double.valueOf(this.covStatAccTxt.getText()),
                                                        Double.valueOf(this.covBiasAccTxt.getText()),
                                                        Double.valueOf(this.covMisAccTxt.getText()),
                                                        Double.valueOf(this.covMisPressTxt.getText()));
    }



    public void resetAltStatus(ActionEvent actionEvent) {
        this.mainImuController.resetAltitudeFilter();
    }


    public void actionCheckboxDatiSimulati(ActionEvent actionEvent) {
        this.txtPortaSeriale.setDisable(this.checkboxDatiSimulati.isSelected());
    }

    public void import3DModel(ActionEvent actionEvent) {
        Airplane3DImporter airplaneController= new Airplane3DImporter(this.scene);
        airplaneController.importSTLModel();
    }
}