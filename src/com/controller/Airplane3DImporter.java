package com.controller;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

import java.io.File;

/**
 * Created by Quirino on 08/09/2014.
 */
public class Airplane3DImporter {

    private Scene scene;
    private static final String MESH_FILENAME =
            "../../../graphic/altimeter_lancetta_lunga.png"
            "../../../graphic/Gilder.stp";

    private static final double MODEL_SCALE_FACTOR = 400;
    private static final double MODEL_X_OFFSET = 0; // standard
    private static final double MODEL_Y_OFFSET = 0; // standard

    private static final int VIEWPORT_SIZE = 800;

    private static final Color lightColor = Color.rgb(244, 255, 250);
    private static final Color jewelColor = Color.rgb(0, 190, 222);

    private Group root;
    private PointLight pointLight;

    public Airplane3DImporter(Scene scene){
        this.scene= scene;
    }

    public MeshView[] loadMeshViews() {
        File file = new File(MESH_FILENAME);
        StlMeshImporter importer = new StlMeshImporter();
        importer.read(file);
        Mesh mesh = importer.getImport();

        return new MeshView[] { new MeshView(mesh) };
    }

    private Group buildScene(){
            MeshView[] meshViews = loadMeshViews();
            for (int i = 0; i < meshViews.length; i++) {
                meshViews[i].setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
                meshViews[i].setTranslateY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
                meshViews[i].setTranslateZ(VIEWPORT_SIZE / 2);
                meshViews[i].setScaleX(MODEL_SCALE_FACTOR);
                meshViews[i].setScaleY(MODEL_SCALE_FACTOR);
                meshViews[i].setScaleZ(MODEL_SCALE_FACTOR);

                PhongMaterial sample = new PhongMaterial(jewelColor);
                sample.setSpecularColor(lightColor);
                sample.setSpecularPower(16);
                meshViews[i].setMaterial(sample);

                meshViews[i].getTransforms().setAll(new Rotate(38, Rotate.Z_AXIS), new Rotate(20, Rotate.X_AXIS));
            }

            pointLight = new PointLight(lightColor);
            pointLight.setTranslateX(VIEWPORT_SIZE*3/4);
            pointLight.setTranslateY(VIEWPORT_SIZE/2);
            pointLight.setTranslateZ(VIEWPORT_SIZE/2);
            PointLight pointLight2 = new PointLight(lightColor);
            pointLight2.setTranslateX(VIEWPORT_SIZE*1/4);
            pointLight2.setTranslateY(VIEWPORT_SIZE*3/4);
            pointLight2.setTranslateZ(VIEWPORT_SIZE*3/4);
            PointLight pointLight3 = new PointLight(lightColor);
            pointLight3.setTranslateX(VIEWPORT_SIZE*5/8);
            pointLight3.setTranslateY(VIEWPORT_SIZE/2);
            pointLight3.setTranslateZ(0);

            Color ambientColor = Color.rgb(80, 80, 80, 0);
            AmbientLight ambient = new AmbientLight(ambientColor);

            root = new Group(meshViews);
            root.getChildren().add(pointLight);
            root.getChildren().add(pointLight2);
            root.getChildren().add(pointLight3);
            root.getChildren().add(ambient);

            return root;
    }

    public void importSTLModel(){
        Group group= this.buildScene();

        this.addCamera();



        /*
        group.setScaleX(2);
        group.setScaleY(2);
        group.setScaleZ(2);
        group.setTranslateX(50);
        group.setTranslateY(50);
         */
    }

    private PerspectiveCamera addCamera() {
        PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
        System.out.println("Near Clip: " + perspectiveCamera.getNearClip());
        System.out.println("Far Clip:  " + perspectiveCamera.getFarClip());
        System.out.println("FOV:       " + perspectiveCamera.getFieldOfView());

        scene.setCamera(perspectiveCamera);
        return perspectiveCamera;
    }
}
