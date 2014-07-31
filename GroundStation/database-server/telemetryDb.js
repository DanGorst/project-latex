'use strict';

var mongoose = require('mongoose');

module.exports = {
    url: 'mongodb://localhost/telemetryDb',
    telemetrySchema: function() {
        return new mongoose.Schema({
            payload_name: String, 
            sentence_id: String, 
            time: Date,
            latitude: Number,
            longitude: Number,
            altitude: Number,
            speed: Number,
            heading: Number,
            temp_internal: Number,
            temp_external: Number
        });
    },
    telemetryModelClass: function() {
        return mongoose.model('TelemetryInfo', this.telemetrySchema());
    }
};