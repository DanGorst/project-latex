var express = require('express');
var bodyParser = require('body-parser');
var telemetryKeys = require('../../telemetryKeys.json');
var decoder = require('./telemetryDecoder');
var http = require('http');

var app = express();

function defaultContentTypeMiddleware (req, res, next) {
  req.headers['content-type'] = req.headers['content-type'] || 'application/json';
  next();
}

app.use(defaultContentTypeMiddleware);
app.use(bodyParser());

function postTelemetryInfo(telemetryInfo)  {
  var infoString = JSON.stringify(telemetryInfo);
  var headers = {
    'Content-Type': 'application/json',
    'Content-Length': infoString.length
  };
  var options = {
    host: 'localhost',
    port: 4000,
    path: '/upload',
    method: 'POST',
    headers: headers
  };

  var req = http.request(options, function(res) {
    res.setEncoding('utf-8');

    var responseString = '';

    res.on('data', function(data) {
      responseString += data;
    });

    res.on('end', function() {
      var resultObject = JSON.parse(responseString);
      console.log(resultObject);
    });
  });

  req.on('error', function(e) {
    console.log(e);
  });

  req.write(infoString);
  req.end();
}

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
        
        postTelemetryInfo(telemetryInfo);
    }
    res.send('Request received');
});

var server = app.listen(3000, function() {
    console.log('Listening on port %d', server.address().port);
});