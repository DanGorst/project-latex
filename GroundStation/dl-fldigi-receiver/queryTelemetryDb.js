'use strict';

var mongoose = require('mongoose');
var telemetryDb = require('./telemetryDb');

var db = mongoose.connection;
mongoose.connect(telemetryDb.url);

db.on('error', console.error);
db.once('open', function() {
});

var TelemetryDbModel = telemetryDb.telemetryModelClass();

TelemetryDbModel
.find()
.sort('-time')
.limit(1)
.exec(function(err, data) {
    if (err) return console.error(err);
    console.log(data);
});

TelemetryDbModel
.find()
.sort('time')
.select('time altitude')
.exec(function(err, data) {
    if (err) return console.error(err);
    console.log(data);
});