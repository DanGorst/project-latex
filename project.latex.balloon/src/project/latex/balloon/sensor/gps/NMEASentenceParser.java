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

public abstract class NMEASentenceParser {

    public static HashMap<String, Object> parse(String NMEASentence) throws SensorReadFailedException {
        String[] NMEATokens = NMEASentence.split(",", -1);
        HashMap<String, Object> NMEAdata = new HashMap<>();

        // Parse sentence according to its type.
        String NMEAType = NMEATokens[0].substring(2);
        switch (NMEAType) {
            case "GPGGA":
                NMEAdata = parseGPGGA(NMEATokens);
                break;
            case "GPRMC":
                NMEAdata = parseGPRMC(NMEATokens);
                break;
        }

        return NMEAdata;

    }

    static HashMap<String, Object> parseGPGGA(String[] GPGGATokens) throws SensorReadFailedException {
        HashMap<String, Object> GPGGAData = new HashMap<>();
        // Check that we hava a GPS fix.
        if (GPGGATokens[6].equals("0")) {
            throw new SensorReadFailedException("No GPS Fix");
        }
        // Parse time.
        String time = GPGGATokens[1].substring(0, 2) + ":"
                + GPGGATokens[1].substring(2, 4) + ":"
                + GPGGATokens[1].substring(4, 6) + ":";
        GPGGAData.put("Time", time);
        // Parse latitude, longitude, altitude.
        double latitude = latitudeToDecimal(GPGGATokens[2], GPGGATokens[3]);
        double longitude = longitudeToDecimal(GPGGATokens[4], GPGGATokens[5]);
        double altitude = Double.parseDouble(GPGGATokens[9]);
        GPGGAData.put("Latitude", latitude);
        GPGGAData.put("Longitude", longitude);
        GPGGAData.put("Altitude", altitude);
        return GPGGAData;
    }

    static HashMap<String, Object> parseGPRMC(String[] GPRMCTokens) throws SensorReadFailedException {
        HashMap<String, Object> GPRMCData = new HashMap<>();
        // Check that we hava a GPS fix.
        if (GPRMCTokens[2].equals("V")) {
            throw new SensorReadFailedException("No GPS Fix");
        }
        // Parse date.
        GPRMCData.put("Date", GPRMCTokens[9]);
        // Parse speed from knots to MPH
        double speed = Double.parseDouble(GPRMCTokens[7])*1.15078;
        GPRMCData.put("Speed", speed);
        return GPRMCData;
    }

    static double latitudeToDecimal(String latitude, String bearing) {
        int degrees = Integer.parseInt(latitude.substring(0,2));
        double minutes = Double.parseDouble(latitude.substring(2));
        double decimalLatitude = degrees + (minutes/60);
        if  (bearing.equals("S")) {
            decimalLatitude *= -1;
        }
        return decimalLatitude;
    }

    static double longitudeToDecimal(String longitude, String bearing) {
        int degrees = Integer.parseInt(longitude.substring(0,3));
        double minutes = Double.parseDouble(longitude.substring(3));
        double decimalLongitude = degrees + (minutes/60);
        if  (bearing.equals("W")) {
            decimalLongitude *= -1;
        }
        return decimalLongitude;
    }
}
