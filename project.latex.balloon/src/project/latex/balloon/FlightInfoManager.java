/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Manages the json file we're using to hold flight info.
 */
public class FlightInfoManager {

    private static final Logger logger = Logger.getLogger(FlightInfoManager.class);

    private final String flightInfoFile;

    public FlightInfoManager(String flightInfoFile) {
        this.flightInfoFile = flightInfoFile;
    }

    public FlightInfo getFlightInfo() {
        Gson gson = new Gson();
        FileReader fileReader = null;
        FlightInfo flightInfo;
        try {
            fileReader = new FileReader(flightInfoFile);
            flightInfo = gson.fromJson(new JsonReader(fileReader), FlightInfo.class);
        } catch (FileNotFoundException ex) {
            // If the file doesn't exist, create a blank flight info object
            flightInfo = new FlightInfo();
            flightInfo.setFlightNumber(0);
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
        return flightInfo;
    }

    public void updateFlightInfo(FlightInfo newInfo) {
        FileWriter fileWriter = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(newInfo);
            fileWriter = new FileWriter(flightInfoFile);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
    }
}
