'use strict';

var cradle = require('cradle');

var db = new(cradle.Connection)().database('telemetry');

db.save('_design/telemetryViews', {
      all: {
        map: function (doc) {
            emit(doc.time, doc);
        }
      },
      altitude: {
        map: function (doc) {
            emit(doc.time, doc.altitude);
        }
      }
  });