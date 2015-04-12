/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author dgorst
 */
public class DataFolderResource {

    private File dataFolder;

    /**
     * The default constructor is used within the production code. The
     * parameterized constructor allows us to test the class by specifying a
     * particular date.
     *
     * @throws IOException
     */
    public DataFolderResource() throws IOException {
        this(new Date(), "data");
    }

    public DataFolderResource(Date date, String baseUrl) throws IOException {
        // We create a new folder for each flight that the balloon makes. All of our sensor data for the 
        // flight is then put into that folder
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        File newFolder = new File(baseUrl + File.separator + "Flight starting - " + dateFormat.format(date));
        if (!newFolder.mkdirs()) {
            throw new IOException("Unable to create directory to contain sensor data logs");
        }
        this.dataFolder = newFolder;
    }

    public File getDataFolder() {
        return dataFolder;
    }
}
