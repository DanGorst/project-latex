/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import java.util.UUID;

/**
 *
 * @author dgorst
 */
public class UUIDSentenceIDGenerator implements SentenceIdGenerator {

    @Override
    public String generateId() {
        //return UUID.randomUUID().toString();
        return "123";
    }

}
