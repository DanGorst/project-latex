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
import project.latex.balloon.sensor.SensorReadFailedException;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class PolledSentenceParser {

    private static final Logger logger = Logger.getLogger(PolledSentenceParser.class);

    private PolledSentenceParser() {
    }

    public static HashMap<String, String> parse(String polledSentence) throws SensorReadFailedException {
        HashMap<String, String> polledData = new HashMap<>();
        String[] polledTokens = polledSentence.split(",", -1);

        if (polledTokens[0].equals("$PUBX") && polledTokens.length == 21) {
            // Do the checksum for the given data.
            try {
                String expectedChecksum = calculateChecksum(polledSentence);
                String receivedChecksum = polledTokens[20].substring(2, 4);

                if (!receivedChecksum.equals(expectedChecksum)) {
                    throw new SensorReadFailedException("Invalid checksum for GPS data.");
                }
            } catch (StringIndexOutOfBoundsException ex) {
                throw new SensorReadFailedException("Invalid checksum format, "
                        + "cannot do checksum" + ex);
            }

            polledData = parseTokens(polledTokens);
        } else {
            throw new SensorReadFailedException("Invalid polled sentence,"
                    + " cannot parse.");
        }

        return polledData;

    }

    private static String calculateChecksum(String data) {
        int checksum = 0;
        if (data.startsWith("$")) {
            data = data.substring(1, data.length());
        }

        int end = data.indexOf('*');
        if (end == -1) {
            end = data.length();
        }

        for (int i = 0; i < end; i++) {
            checksum = checksum ^ data.charAt(i);
        }

        String hex = Integer.toHexString(checksum);
        if (hex.length() == 1) {
            hex = "0" + hex;
        }
        return (hex.toUpperCase());
    }

    private static HashMap<String, String> parseTokens(String[] polledTokens) throws SensorReadFailedException {
        HashMap<String, String> parsedData = new HashMap<>();

        // Check that we hava a GPS fix.
        if (polledTokens[8].equals("NF")) {
            logger.info("No GPS Fix");
            return parsedData;
        }

        // Get time as "HH:MM:SS".
        String time = polledTokens[2];
        try {
            time = time.substring(0, 2) + ":"
                    + time.substring(2, 4) + ":"
                    + time.substring(4, 6);
        } catch (StringIndexOutOfBoundsException ex) {
            throw new SensorReadFailedException("GPS time data unavailable"
                    + "or corrupted:" + ex);
        }

        // Get latitude and longitude in decimal form.
        String latitude = latitudeToDecimal(polledTokens[3], polledTokens[4]);
        String longitude = longitudeToDecimal(polledTokens[5], polledTokens[6]);
        // Get altitude in meters.
        String altitude = polledTokens[7];
        // Get speed in kph.
        String speed = polledTokens[11];
        // Get heading as a clockwise angle relative to North.
        String heading = polledTokens[12];

        parsedData.put("time", time);
        parsedData.put("latitude", latitude);
        parsedData.put("longitude", longitude);
        parsedData.put("altitude", altitude);
        parsedData.put("speed", speed);
        parsedData.put("heading", heading);
        return parsedData;
    }

    private static String latitudeToDecimal(String latitude, String heading) {
        double degrees = Double.parseDouble(latitude.substring(0, 2));
        double minutes = Double.parseDouble(latitude.substring(2));

        double decimalLatitude = degrees + (minutes / 60);
        if (heading.equals("S")) {
            decimalLatitude *= -1;
        }
        return String.valueOf(decimalLatitude);
    }

    private static String longitudeToDecimal(String longitude, String heading) {
        double degrees = Double.parseDouble(longitude.substring(0, 3));
        double minutes = Double.parseDouble(longitude.substring(3));

        double decimalLongitude = degrees + (minutes / 60);
        if (heading.equals("W")) {
            decimalLongitude *= -1;
        }
        return String.valueOf(decimalLongitude);
    }
}
