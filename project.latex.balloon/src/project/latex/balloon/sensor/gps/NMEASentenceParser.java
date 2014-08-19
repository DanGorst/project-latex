package project.latex.balloon.sensor.gps;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author will
 */
import java.util.HashMap;
import org.apache.log4j.Logger;

public class NMEASentenceParser {

    private static final Logger logger = Logger.getLogger(GPSSensorController.class);

    private NMEASentenceParser() {
    }

    public static HashMap<String, String> parse(String nmeaSentence) throws SensorReadFailedException {
        HashMap<String, String> nmeaData = new HashMap<>();
        String[] nmeaTokens = nmeaSentence.split(",", -1);

        // Parse sentence according to its type.
        String nmeaType = nmeaTokens[0].substring(1);
        switch (nmeaType) {
            case "GPGGA":
                nmeaData = parseGpgga(nmeaTokens);
                break;
            case "GPRMC":
                nmeaData = parseGprmc(nmeaTokens);
                break;
        }
        return nmeaData;

    }

    private static HashMap<String, String> parseGpgga(String[] gpggaTokens) throws SensorReadFailedException {
        HashMap<String, String> gpggaData = new HashMap<>();
        // Check that we hava a GPS fix.
        if (gpggaTokens[6].equals("0")) {
            throw new SensorReadFailedException("No GPS Fix");
        }
        // Get time as "DDMMYY".
        String time = getStringValueIfAvailable("Time", gpggaTokens[1]);
        if (!time.equals("N/A")) {
            time = time.substring(0, 2) + ":"
                    + time.substring(2, 4) + ":"
                    + time.substring(4, 6);
        }
        
        // Get latitude and longitude in decimal form.
        String latitude = getStringValueIfAvailable("Latitude", gpggaTokens[2]);
        if (!latitude.equals("N/A")) {
            latitude = latitudeToDecimal(latitude, gpggaTokens[3]);
        }
        
        String longitude = getStringValueIfAvailable("Longitude", gpggaTokens[4]);
        if (!longitude.equals("N/A")) {
            longitude = longitudeToDecimal(longitude, gpggaTokens[5]);
        }

        String altitude = getStringValueIfAvailable("Altitude", gpggaTokens[9]);
        
        gpggaData.put("time", time);
        gpggaData.put("latitude", latitude);
        gpggaData.put("longitude", longitude);
        gpggaData.put("altitude", altitude);
        return gpggaData;
    }

    private static HashMap<String, String> parseGprmc(String[] gprmcTokens) throws SensorReadFailedException {
        HashMap<String, String> gprmcData = new HashMap<>();
        // Check that we hava a GPS fix.
        if (gprmcTokens[2].equals("V")) {
            throw new SensorReadFailedException("No GPS Fix");
        }
        // Get date as String "DDMMYY".
        String date = getStringValueIfAvailable("Date", gprmcTokens[9]);
        // Get speed in kph.
        String speed = getStringValueIfAvailable("Speed", gprmcTokens[7]);
        if (!speed.equals("N/A")) {
            speed = String.valueOf(Double.parseDouble(speed)*1.852);
        }

        // Get course over ground as a clockwise angle relative to North.
        String courseOverGround = getStringValueIfAvailable("Course over ground", gprmcTokens[8]);

        gprmcData.put("date", date);
        gprmcData.put("speed", speed);
        gprmcData.put("course over ground", courseOverGround);
        return gprmcData;
    }

    private static String latitudeToDecimal(String latitude, String bearing) {
        double degrees = Double.parseDouble(latitude.substring(0, 2));
        double minutes = Double.parseDouble(latitude.substring(2));
        
        double decimalLatitude = degrees + (minutes / 60);
        if (bearing.equals("S")) {
            decimalLatitude *= -1;
        }
        return String.valueOf(decimalLatitude);
    }

    private static String longitudeToDecimal(String longitude, String bearing) {
        double degrees = Double.parseDouble(longitude.substring(0, 3));
        double minutes = Double.parseDouble(longitude.substring(3));
        
        double decimalLongitude = degrees + (minutes / 60);
        if (bearing.equals("W")) {
            decimalLongitude *= -1;
        }
        return String.valueOf(decimalLongitude);
    }

    private static String getStringValueIfAvailable(String requestedValue, String nmeaToken) {
        String tokenAsString;
        if (nmeaToken.equals("")) {
            tokenAsString = "N/A";
            logger.info(requestedValue + " not currently available");
        } else {
            tokenAsString = nmeaToken;
        }
        return tokenAsString;
    }
}
