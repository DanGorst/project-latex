/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author dgorst
 */
public class ChunkedSerialDataWriter implements DataWriter {

    private static final Logger logger = Logger.getLogger(ChunkedSerialDataWriter.class);
    
    private final int chunkSize;

    private final int delayInMilliseconds;

    private final SerialDataWriter dataWriter;

    private final List<String> chunks;
    
    private final Thread worker;

    public ChunkedSerialDataWriter(int chunkSize, final int delayInMilliseconds, SerialDataWriter dataWriter) {
        this.chunks = new ArrayList<>();
        this.chunkSize = chunkSize;
        this.delayInMilliseconds = delayInMilliseconds;
        this.dataWriter = dataWriter;
        
        worker = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    // Write the next chunk of data if it is available
                    writeNextChunk();
                    try {
                        // Wait for the specified delay before writing the next chunk
                        Thread.sleep(delayInMilliseconds);
                    } catch (InterruptedException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        });
    }

    public List<String> getChunks() {
        return chunks;
    }
    
    public void start() {
        logger.info("Chunked serial data writer started");
        worker.start();
    }

    synchronized void addChunk(String chunk) {
        this.chunks.add(chunk);
    }

    synchronized void writeNextChunk() {
        if (!this.chunks.isEmpty()) {
            String chunk = this.chunks.remove(0);
            logger.info("Next chunk to write: " + chunk);
            this.dataWriter.writeString(chunk);
        }
    }

    List<String> breakDataIntoChunks(String data) {
        List<String> chunkedData = new ArrayList<>();
        for (int i = 0; i < data.length(); i += chunkSize) {
            int endIndex = Math.min(data.length(), i + chunkSize);
            chunkedData.add(data.substring(i, endIndex));
        }
        return chunkedData;
    }

    @Override
    public void writeData(Map<String, Object> dataModel) {
        String csvString = this.dataWriter.convertDataToCsvString(dataModel);
        logger.info(csvString);
        // Add a new line character to the end of our data so that we can separate sentences
        csvString += "\n";
        // Break our data into chunks to pass to our serial data writer
        List<String> newChunks = breakDataIntoChunks(csvString);
        // Add the chunks to our collection
        for (String chunk : newChunks) {
            addChunk(chunk);
        }
    }
}
