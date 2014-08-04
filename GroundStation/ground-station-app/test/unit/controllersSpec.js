'use strict';

/* jasmine specs for controllers go here */

describe('controllers', function(){
  beforeEach(module('projectLatexApp.controllers'));


  it('should be defined', inject(function($controller) {
    //spec body
    var telemetryCtrl = $controller('TelemetryCtrl', { $scope: {} });
    expect(telemetryCtrl).toBeDefined();
  }));
});
