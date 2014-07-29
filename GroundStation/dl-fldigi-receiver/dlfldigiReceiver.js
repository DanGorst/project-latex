var express = require('express');
var bodyParser = require('body-parser');
var telemetryKeys = require('./telemetryKeys.json');
var decoder = require('./telemetryDecoder');
var cradle = require('cradle');

var db = new(cradle.Connection)().database('telemetry');

var app = express();

function defaultContentTypeMiddleware (req, res, next) {
  req.headers['content-type'] = req.headers['content-type'] || 'application/json';
  next();
}

app.use(defaultContentTypeMiddleware);
app.use(bodyParser());

app.all('*', function(req, res) {
    console.log('Handling ' + req.method + ' request');
    if (req.method === 'PUT')  {
        var base64data = req.body.data._raw;
        var keys = telemetryKeys.keys;
    
        var telemetryInfo = decoder.decodeTelemetryData(base64data, keys);
    
        // Our data originally has time as a string, but we convert it into a date.
        // This should allow us to sort our data by time later on
        var timeComponents = telemetryInfo.time.split(':');
        // Our string only contains a time, not a date. For now, we're just using
        // today's date
        var date = new Date();
        date.setHours(timeComponents[0]);
        date.setMinutes(timeComponents[1]);
        date.setSeconds(timeComponents[2]);
        telemetryInfo.time = date;
        
        db.save(telemetryInfo, function(err, res) {
            if (err) {
                console.error(err);
            }
            console.log(res);
        });
    }
    res.send('Request received');
});

var server = app.listen(3000, function() {
    console.log('Listening on port %d', server.address().port);
});