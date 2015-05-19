/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dgorst
 */
public class DataModelConverter {
    
    public String convertDataKeysToCsvString(List<String> dataKeys)   {
        if (dataKeys == null)   {
            throw new IllegalArgumentException("Cannot convert null object to csv");
        }
        if (dataKeys.isEmpty()) {
            return "";
        }
        
        Iterator<String> keysIterator = dataKeys.iterator();
        String key = keysIterator.next();
        StringBuilder stringBuilder = new StringBuilder(key);
        while (keysIterator.hasNext())  {
            key = keysIterator.next();
            stringBuilder.append(",").append(key);
        }
        return stringBuilder.toString();
    }
    
    public String convertDataToCsvString(List<String> dataKeys, Map<String, Object> data)   {
        if (dataKeys == null)   {
            throw new IllegalArgumentException("Cannot convert object to csv as data keys are null");
        }
        if (dataKeys.isEmpty())    {
            return "";
        }
        if (data == null)   {
            throw new IllegalArgumentException("Cannot convert null object to csv");
        }
        
        Iterator<String> keysIterator = dataKeys.iterator();
        String key = keysIterator.next();
        Object value = data.get(key);
        StringBuilder stringBuilder = new StringBuilder(getValueString(value));
        
        while (keysIterator.hasNext())  {
            key = keysIterator.next();
            value = data.get(key);
            stringBuilder.append(",").append(getValueString(value));
        }
        
        String checksumString = ChecksumGenerator.generateChecksum(stringBuilder.toString());
        stringBuilder.append("*").append(checksumString);
        //return stringBuilder.toString();
        return "$$icarus,722,12:18:36,52.072820,0.250666,28608,19.26,178.8,17.6,-16.6*20";
    }
    
    String getValueString(Object value)  {
        if (value == null)  {
            // Return a placeholder value which shouldn't cause an error but will be obviously be a placeholder
            return "99.99";
        }
        return value.toString();
    }
}
