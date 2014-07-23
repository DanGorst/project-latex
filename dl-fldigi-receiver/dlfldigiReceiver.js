var express = require('express');
var bodyParser = require('body-parser');
var telemetryKeys = require('./telemetryKeys.json');

var app = express();

function defaultContentTypeMiddleware (req, res, next) {
  req.headers['content-type'] = req.headers['content-type'] || 'application/json';
  next();
}

app.use(defaultContentTypeMiddleware);
app.use(bodyParser());

app.all('*', function(req, res) {
    var base64data = req.body.data._raw;
    var buffer = new Buffer(base64data, 'base64');
    var decodedDataStringWithChecksum = buffer.toString();
    // Split the string to separate out the checksum at the end
    var decodedDataString = decodedDataStringWithChecksum.split('*')[0];
    // The data is comma-separated, so get the individual values
    var telemetryArray = decodedDataString.split(',');
    
    // Now match up the values with the associated keys in the telemetry schema
    var keys = telemetryKeys.keys;
    var telemetryInfo = {};
    for (var i = 0; i < keys.length; ++i) {
        telemetryInfo[keys[i]] = telemetryArray[i];
    }
    
    console.log(telemetryInfo);
    // TODO: Do something with this data. Send it to a database perhaps?
  res.send('Request received');
});

var server = app.listen(3000, function() {
    console.log('Listening on port %d', server.address().port);
});