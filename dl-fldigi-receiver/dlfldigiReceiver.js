var express = require('express');
var bodyParser = require('body-parser');

var app = express();

function defaultContentTypeMiddleware (req, res, next) {
  req.headers['content-type'] = req.headers['content-type'] || 'application/json';
  next();
}

app.use(defaultContentTypeMiddleware);
app.use(bodyParser());

app.get('/', function(req, res){
  res.send('Request received');
});

app.all('*', function(req, res) {
    // TODO: Handle the telemetry information here
    console.log(req.headers);
    console.log(req.body);
  res.send('Request received');
});

var server = app.listen(3000, function() {
    console.log('Listening on port %d', server.address().port);
});