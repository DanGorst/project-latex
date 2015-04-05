/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.consumer;

import java.util.Map;

/**
 * Interface for any classes which make use of the data model once it has been
 * populated.
 */
public interface DataModelConsumer {

    /**
     * Consume the data model. The consumer shouldn't make any changes to it, as
     * other consumers could be using the model afterwards.
     *
     * @param dataModel
     */
    public void consumeDataModel(final Map<String, Object> dataModel);
}
