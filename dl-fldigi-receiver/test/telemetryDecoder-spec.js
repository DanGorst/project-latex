describe("Telemetry decoder", function() {
	var decoder = require("../telemetryDecoder");

 	it("correctly decodes a valid string with a matching set of keys", function() {
        var result = decoder.decodeTelemetryData("JCRsYXRleCw1NC4yLDIuNTY=", ["project_name", "latitude", "longitude"]);
    	expect(result).toEqual({project_name: "$$latex", latitude: "54.2", longitude: "2.56"});
  	});
    
    it("throws exception if passed a null string", function() {
        expect(function() { decoder.decodeTelemetryData(null, []) }).toThrow("Cannot decode null data");
    });
    
    it("throws exception if passed null keys", function()   {
        expect(function() { decoder.decodeTelemetryData("", null) }).toThrow("Cannot decode data with null keys");
    });
    
    it("throws exception if passed empty string and empty set of keys", function()   {
        expect(function() { decoder.decodeTelemetryData("", []) }).toThrow("Data doesn't match the keys passed in from the schema");
    });
    
    it("throws exception if passed keys length doesn't match data length", function()   {
        expect(function() { decoder.decodeTelemetryData("", ["someKey", "anotherKey"]) }).toThrow("Data doesn't match the keys passed in from the schema");
        expect(function() { decoder.decodeTelemetryData("JCRsYXRleCw1NC4yLDIuNTY=", ["someKey", "anotherKey"]) }).toThrow("Data doesn't match the keys passed in from the schema");
    });
});