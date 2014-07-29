'use strict';

var cradle = require('cradle');

var db = new(cradle.Connection)().database('telemetry');

db.view('telemetryViews/all', {descending: true, limit: 1}, function(err, res) {
    console.log('Get latest:');
    res.forEach(function(key, row, id) {
        console.log('%s: %s %s %s', key, row.altitude, row.latitude, row.longitude);
    });
});

db.view('telemetryViews/altitude', function(err, res) {
    console.log('Altitude data:');
    res.forEach(function(key, row, id) {
        console.log('%s: %s', key, row);
    });
});