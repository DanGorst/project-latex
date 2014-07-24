'use strict';

var decodeTelemetryData = function(base64data, telemetryKeys)    {
    if (base64data === null)    {
        throw "Cannot decode null data";
    }
    if (telemetryKeys === null) {
        throw "Cannot decode data with null keys";
    }
    
    var buffer = new Buffer(base64data, 'base64');
    var decodedDataStringWithChecksum = buffer.toString();
    // Split the string to separate out the checksum at the end
    var decodedDataString = decodedDataStringWithChecksum.split('*')[0];
    // The data is comma-separated, so get the individual values
    var telemetryArray = decodedDataString.split(',');
    
    if (telemetryArray.length != telemetryKeys.length)  {
        throw "Data doesn't match the keys passed in from the schema";
    }
    
    // Now match up the values with the associated keys in the telemetry schema
    var telemetryInfo = {};
    for (var i = 0; i < telemetryKeys.length; ++i) {
        telemetryInfo[telemetryKeys[i]] = telemetryArray[i];
    }
    
    return telemetryInfo;
}

module.exports.decodeTelemetryData = decodeTelemetryData;