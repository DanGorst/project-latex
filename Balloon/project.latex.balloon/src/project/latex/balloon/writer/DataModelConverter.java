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
        
        return stringBuilder.toString();
    }
    
    String getValueString(Object value)  {
        if (value == null)  {
            // Return a placeholder value which shouldn't cause an error but will be obviously be a placeholder
            return "99.99";
        }
        return value.toString();
    }
}
