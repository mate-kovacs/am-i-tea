package com.codecool.am_i_tea;

import com.codecool.paintFx.service.PaintService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Application.launch;

public class AmITea extends Application {

    private TextFileService textFileService;
    private ProjectService projectService;
    private ProjectDAO projectDAO;
    private TextFileDAO fileDAO;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        projectDAO = new ProjectDAO();
        fileDAO = new TextFileDAO();
        textFileService = new TextFileService(fileDAO);
        projectService = new ProjectService(projectDAO);

        PaintService.setfileDAO(fileDAO);
        PaintService.setProjectDAO(projectDAO);

        primaryStage.setTitle("Am-I-Tea text editor");

        HTMLEditor editor = new HTMLEditor();
        editor.setVisible(false);

        final Menu fileMenu = new Menu("File");
        final Menu projectMenu = new Menu("Project");

        final MenuItem newFileMenuItem = new MenuItem("New");
        final MenuItem saveFileMenuItem = new MenuItem("Save");
        final MenuItem openFileMenuItem = new MenuItem("Open");

        final MenuItem newProjectMenuItem = new MenuItem("New");
        final MenuItem loadProjectMenuItem = new MenuItem("Load");
        final MenuItem closeProjectMenuItem = new MenuItem("Close");
        final MenuItem exitMenuItem = new MenuItem("Exit");

        newProjectMenuItem.setOnAction(actionEvent -> {
            String projectName = JOptionPane.showInputDialog("Project Name");
            if (projectService.createProject(projectName)) {
                fileMenu.setDisable(false);
                editor.setVisible(false);
                editor.setHtmlText("");
                //todo show editor window and other menus only then
            } else {
                // todo show error message
            }
        });

        loadProjectMenuItem.setOnAction(actionEvent -> {
            List<String> projects = projectService.getAllProjects();

            ListView<String> projectList = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList(projects);
            projectList.setItems(items);

            StackPane temporaryWindow = new StackPane();
            temporaryWindow.getChildren().addAll(projectList);
            Scene tempScene = new Scene(temporaryWindow, 200, 320);
            Stage tempWindow = new Stage();
            tempWindow.setTitle("Projects");
            tempWindow.setScene(tempScene);

            tempWindow.setX(primaryStage.getX() + 12);
            tempWindow.setY(primaryStage.getY() + 28);

            projectList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                    String projectName = projectList.getSelectionModel().getSelectedItem();
                    projectService.loadProject(projectName);
                    fileMenu.setDisable(false);
                    editor.setVisible(false);
                    editor.setHtmlText("");
                    tempWindow.close();
                }
            });

            tempWindow.show();
        });

        closeProjectMenuItem.setOnAction(actionEvent -> {
            // todo save current file (files?) before closing them
            System.out.println("Project closed!");

            fileDAO.setCurrentFile(null);
            projectDAO.setCurrentProject(null);
            fileMenu.setDisable(true);
            saveFileMenuItem.setDisable(true);
            editor.setVisible(false);
            editor.setHtmlText("");
        });

        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        projectMenu.getItems().addAll(newProjectMenuItem,
                loadProjectMenuItem,
                closeProjectMenuItem,
                exitMenuItem);

        newFileMenuItem.setOnAction(actionEvent -> {
            String fileName = JOptionPane.showInputDialog("File Name");
            if (textFileService.createNewTextFile(projectDAO.getCurrentProject().getPath(), fileName)) {
                saveFileMenuItem.setDisable(false);
                editor.setVisible(true);
                editor.setHtmlText("");
            } else {
                // todo show error message
            }

        });

        saveFileMenuItem.setOnAction(actionEvent -> textFileService.saveTextFile(projectDAO.getCurrentProject().getPath(), fileDAO.getCurrentFile().getName(), editor));
        saveFileMenuItem.setDisable(true);

        openFileMenuItem.setOnAction(actionEvent -> {
            List<String> files = textFileService.getAllFilesOfProject(projectDAO.getCurrentProject().getName());

            ListView<String> fileList = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList(files);
            fileList.setItems(items);

            StackPane temporaryWindow = new StackPane();
            temporaryWindow.getChildren().addAll(fileList);
            Scene tempScene = new Scene(temporaryWindow, 200, 320);
            Stage tempWindow = new Stage();
            tempWindow.setTitle(projectDAO.getCurrentProject().getName());
            tempWindow.setScene(tempScene);

            tempWindow.setX(primaryStage.getX() + 12);
            tempWindow.setY(primaryStage.getY() + 28);

            fileList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                    String fullFileName = fileList.getSelectionModel().getSelectedItem();
                    String fileName = fullFileName.split("\\.")[0];
                    textFileService.openTextFile(fileName, projectDAO.getCurrentProject().getPath(), editor);
                    saveFileMenuItem.setDisable(false);
                    editor.setVisible(true);
                    tempWindow.close();
                }
            });

            tempWindow.show();
        });


        fileMenu.getItems().addAll(newFileMenuItem, saveFileMenuItem, openFileMenuItem);
        fileMenu.setDisable(true);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(projectMenu, fileMenu);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Node node = editor.lookup(".top-toolbar");
        if (node instanceof ToolBar) {
            ToolBar bar = (ToolBar) node;

            Button drawButton = new Button("Draw");
            ImageView drawImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/draw.png")));
            drawButton.setMinSize(26.0, 22.0);
            drawButton.setMaxSize(26.0, 22.0);
            drawImageView.setFitHeight(16);
            drawImageView.setFitWidth(16);
            drawButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            drawButton.setGraphic(drawImageView);
            drawButton.setMinSize(26.0, 22.0);
            drawButton.setMaxSize(26.0, 22.0);
            drawButton.setTooltip(new Tooltip("Draw"));

            Button linkButton = new Button("Hyperlink");
            ImageView linkImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/hyperlink.png")));
            linkButton.setMinSize(26.0, 22.0);
            linkButton.setMaxSize(26.0, 22.0);
            linkImageView.setFitHeight(16);
            linkImageView.setFitWidth(16);
            linkButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            linkButton.setGraphic(linkImageView);
            linkButton.setTooltip(new Tooltip("Hypermilnk"));

            linkButton.setOnAction(actionEvent -> {
                String url = JOptionPane.showInputDialog("Enter URL");
                WebView webView = (WebView) editor.lookup("WebView");
                String selected = (String) webView.getEngine().executeScript("window.getSelection().toString();");
                String hyperlinkHtml = "<a href=\"" + url.trim() + "\" title=\"" + selected + "\" target=\"_blank\">" + selected + "</a>";
                webView.getEngine().executeScript(getInsertHtmlAtCursorJS(hyperlinkHtml));
            });

            drawButton.setOnAction(actionEvent -> {


                try {
                    Scene drawScene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("paint.fxml")));
                    drawScene.getRoot().setStyle("-fx-background-color: transparent ;");
                    StackPane wrapper = new StackPane();
                    wrapper.getChildren().add(primaryStage.getScene().getRoot());
                    wrapper.getChildren().add(drawScene.getRoot());

                    Scene scene = new Scene(wrapper, 640, 480);
                    primaryStage.setScene(scene);



                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                primaryStage.setWidth(640);
                primaryStage.setHeight(480);
                primaryStage.show();
            });

            bar.getItems().addAll(linkButton, drawButton);
        }
        Scene root = new Scene(new VBox(), 640, 480);
        ((VBox) root.getRoot()).getChildren().addAll(menuBar, editor);
        primaryStage.setScene(root);
        primaryStage.show();
    }

    private String getInsertHtmlAtCursorJS(String html) {
        return "insertHtmlAtCursor('" + html + "');"
                + "function insertHtmlAtCursor(html) {\n"
                + " var range, node;\n"
                + " if (window.getSelection && window.getSelection().getRangeAt) {\n"
                + " window.getSelection().deleteFromDocument();\n"
                + " range = window.getSelection().getRangeAt(0);\n"
                + " node = range.createContextualFragment(html);\n"
                + " range.insertNode(node);\n"
                + " } else if (document.selection && document.selection.createRange) {\n"
                + " document.selection.createRange().pasteHTML(html);\n"
                + " document.selection.clear();"
                + " }\n"
                + "}";
    }
}
