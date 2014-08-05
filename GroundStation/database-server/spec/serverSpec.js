'use strict';

var server = require('../database-server');

describe('Get latest data', function() {
    it('should return an empty object when there is no data', function() {
        expect(server.getLatestDataToReturn([])).toEqual({});
    });
    
    it('should return first entry when there is data available', function() {
        var input = [2,3,5];
        expect(server.getLatestDataToReturn(input)).toEqual(2);
    });
});