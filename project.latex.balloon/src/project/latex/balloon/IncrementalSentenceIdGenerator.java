/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import java.io.IOException;

/**
 * Each time we request an ID, the generator increments a count and returns the
 * next value.
 */
public class IncrementalSentenceIdGenerator implements SentenceIdGenerator {

    private int count = 0;

    private final FlightInfo flightInfo;

    public IncrementalSentenceIdGenerator(FlightInfoManager flightInfoManager) {
        this.flightInfo = flightInfoManager.getFlightInfo();
        
        // Increment the flight number
        int lastFlightNumber = flightInfo.getFlightNumber();
        flightInfo.setFlightNumber(++lastFlightNumber);
        
        flightInfoManager.updateFlightInfo(flightInfo);
    }

    @Override
    public String generateId() {
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(flightInfo.getFlightNumber()).append("_").append(count);
        ++count;
        return idBuilder.toString();
    }

}
