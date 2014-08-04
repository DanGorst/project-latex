var express = require('express');
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var telemetryDb = require('./telemetryDb');

var app = express();

var db = mongoose.connection;
mongoose.connect(telemetryDb.url);

db.on('error', console.error);
db.once('open', function() {
});

var TelemetryDbModel = telemetryDb.telemetryModelClass();

app.use(bodyParser());

app.get('/latest', function(req, res) {
    TelemetryDbModel
        .find()
        .sort('-time')
        .limit(1)
        .exec(function(err, data) {
            if (err) {
                res.send(err);
            }
            // TODO: Handle case where we don't have any data
            res.send(data[0]);
        });
});

app.get('/altitude', function(req, res) {
    TelemetryDbModel
        .find()
        .sort('time')
        .select('time altitude')
        .exec(function(err, data) {
            if (err) {
                res.send(err);
            }
            res.send(data);
        });
});

app.put('/upload', function(req, res) {
    console.log('Handling PUT');
    var dbTelemetryInfo = new TelemetryDbModel(req.body);
        dbTelemetryInfo.save(function(err, dbTelemetryInfo) {
          if (err) {
              res.send(err);
          }
          res.send(dbTelemetryInfo);
        });
});

app.post('/upload', function(req, res) {
    console.log('Handling POST');
    var dbTelemetryInfo = new TelemetryDbModel(req.body);
        dbTelemetryInfo.save(function(err, dbTelemetryInfo) {
          if (err) {
              res.send(err);
          }
          res.send(dbTelemetryInfo);
        });
});

var server = app.listen(4000, function() {
    console.log('Listening on port %d', server.address().port);
});