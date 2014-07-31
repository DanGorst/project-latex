var express = require('express');
var bodyParser = require('body-parser');
var telemetryKeys = require('../../telemetryKeys.json');
var decoder = require('./telemetryDecoder');

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
    
        console.log(telemetryInfo);
        // TODO: Do something with this data. Send it to a database perhaps?
    }
    res.send('Request received');
});

var server = app.listen(3000, function() {
    console.log('Listening on port %d', server.address().port);
});