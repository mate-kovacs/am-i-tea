package com.codecool.am_i_tea.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {

    private Properties properties;

    private String path;
    private String fileName;
    private LoggerService logger;

    public PropertyUtil(Properties properties, LoggerService loggerService) {
        this.properties = properties;
        this.logger = loggerService;
    }

    public void initializeProperties() {
        path = logger.getPath();

        switch (System.getProperty("os.name")) {
            case "Linux":
                fileName = "config.properties";
                break;
            case "Windows":
                fileName = "config.txt";
                break;
            default:
                System.out.println("This program is only designed to work under Windows or Linux operation systems!");
                break;
        }
        createConfigFile();
        readConfigProperties();
    }

    public void setLocationProperty(String location) {
        properties.setProperty("path", location);
        writeConfigProperties();
    }

    public String getLocationProperty() {
        return properties.getProperty("path");
    }

    private void readConfigProperties() {
        try {
            FileReader reader = new FileReader(path + File.separator + fileName);
            properties.load(reader);
            System.out.println("Properies loaded successfully!");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Failed to load properties!");
        }
        System.out.println(properties.getProperty("path"));
    }

    private void writeConfigProperties() {
        try {
            FileWriter writer = new FileWriter(path + File.separator + fileName);
            properties.store(writer, null);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void createConfigFile() {
        File configFolder = new File(path);
        File configFile = new File(path + File.separator + fileName);
        if (!configFolder.exists()) {
            if (configFolder.mkdirs()) {
                System.out.println("Config directory created!");
            } else {
                System.out.println("Failed to create config directory!");
            }
        }
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    System.out.println("Config file created!");
                    initializeConfigFileProperties();
                } else {
                    System.out.println("Failed to create config file!");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void initializeConfigFileProperties() {
        String homeFolder = System.getProperty("user.home");
        String defaultPath = homeFolder + File.separator + "AmITea";
        properties.setProperty("path", defaultPath);
        writeConfigProperties();
    }
}
