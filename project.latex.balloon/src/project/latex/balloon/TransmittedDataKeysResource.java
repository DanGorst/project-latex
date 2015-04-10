/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dgorst
 */
public class TransmittedDataKeysResource {

    private List<String> transmittedDataKeys;
    
    public TransmittedDataKeysResource(String filePath) throws IOException {
        loadTransmittedDataKeys(filePath);
    }

    final void loadTransmittedDataKeys(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Cannot load keys from null file");
        }

        JsonReader reader = null;
        try {
            List<String> dataKeys = new ArrayList<>();
            reader = new JsonReader(new FileReader(filePath));
            reader.beginObject();
            while (reader.hasNext()) {
                reader.nextName();
                reader.beginArray();
                while (reader.hasNext()) {
                    dataKeys.add(reader.nextString());
                }
                reader.endArray();
            }
            reader.endObject();
            reader.close();

            this.transmittedDataKeys = dataKeys;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public List<String> getTransmittedDataKeys() {
        return transmittedDataKeys;
    }
}
