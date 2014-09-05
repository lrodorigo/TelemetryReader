package com.application;


import com.controller.MainImuFakeController;
import com.fxml.FXMLUtils;
import com.user.propertyHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class MainScene extends Application implements Initializable {

    public Label statusConsole;
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

  //  private MainImuController mainImuController;


    private MainImuFakeController mainImuController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        propertyHandler.getInstance().loadProperties();
        setupGUI();

        //this.mainImuController  = new MainImuController(this);
        this.mainImuController  = new MainImuFakeController(this);
        System.out.println("Chiamato!");
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
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(FXMLUtils.getInstance().getSceneCSS("MainScene"));
        primaryStage.setTitle("IMU Telemetry Reader v1.0");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
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


    //setupChart();
    // text.setFont(Font.font(null, FontWeight.BOLD, 30));


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

        //
    //    this.chartSeries.setName("My portfolio");
        this.chart.getData().add(this.chartSeries);

//        NumberAxis quota = new NumberAxis();
//        CategoryAxis ora = new CategoryAxis();
//        quota.setLabel("Quota (mainImuController)");
//        ora.setLabel("Ora");
//    //    this.chart = new LineChart<String, Number>(ora, quota);
//        XYChart.Series series = new XYChart.Series();
//        series.setName("My portfolio");
//
//        series.getData().add(new XYChart.Data("Jan", 23));
//        series.getData().add(new XYChart.Data("Feb", 14));
//        series.getData().add(new XYChart.Data("Mar", 15));
//        series.getData().add(new XYChart.Data("Apr", 24));
//        series.getData().add(new XYChart.Data("May", 34));
//        series.getData().add(new XYChart.Data("Jun", 36));
//        series.getData().add(new XYChart.Data("Jul", 22));
//        series.getData().add(new XYChart.Data("Aug", 45));
//        series.getData().add(new XYChart.Data("Sep", 43));
//        series.getData().add(new XYChart.Data("Oct", 17));
//        series.getData().add(new XYChart.Data("Nov", 29));
//        series.getData().add(new XYChart.Data("Dec", 25));
//
//        this.chart.getData().add(series);
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
        System.out.println("AA:A");
        this.mainImuController.storeAltitudeSettings(Double.valueOf(this.covStatAccTxt.getText()),
                Double.valueOf(this.covBiasAccTxt.getText()),
                Double.valueOf(this.covMisAccTxt.getText()),
                Double.valueOf(this.covMisPressTxt.getText()));
    }



    public void resetAltStatus(ActionEvent actionEvent) {
        this.mainImuController.setupAltitudeFilter();
    }
}