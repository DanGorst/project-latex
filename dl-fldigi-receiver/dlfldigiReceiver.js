var express = require('express');
var bodyParser = require('body-parser');

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
    var decodedDataString = buffer.toString();
    var telemetryArray = decodedDataString.split(',');
    console.log(telemetryArray);
    // TODO: Do something with this data. Send it to a database perhaps?
  res.send('Request received');
});

var server = app.listen(3000, function() {
    console.log('Listening on port %d', server.address().port);
});