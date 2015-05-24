/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

/**
 * Each time we request an ID, the generator increments a count and returns the
 * next value.
 */
public class IncrementalSentenceIdGenerator implements SentenceIdGenerator {

    private int count = 0;

    @Override
    public String generateId() {
        String id = Integer.toString(count);
        ++count;
        return id;
    }

}
