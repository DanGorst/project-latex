'use strict';

/* jasmine specs for controllers go here */

describe('controllers', function(){
    var scope, ctrl, $httpBackend;
    
    beforeEach(module('projectLatexApp.controllers'));
    
    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        $httpBackend = _$httpBackend_;
        scope = $rootScope.$new();
        ctrl = $controller('TelemetryCtrl', { $scope: scope });
    }));

    it('should be defined', inject(function($controller) {
        //spec body
        expect(ctrl).toBeDefined();
    }));
    
    it('should display the no data labels when query to server fails', function() {
        $httpBackend.expectGET('http://localhost:4000/latest').respond({});
        
        $httpBackend.flush();
        
        expect(scope.payload_name).toEqual('No data');
        expect(scope.time).toEqual('No data');
        expect(scope.altitude).toEqual('No data');
        expect(scope.latitude).toEqual('No data');
        expect(scope.longitude).toEqual('No data');
        expect(scope.speed).toEqual('No data');
        expect(scope.heading).toEqual('No data');
    });
    
    it('should display data when it is available', function() {
        $httpBackend.expectGET('http://localhost:4000/latest').respond({
            payload_name:"$$latex",
            time:"12:19:03",
            altitude:12345,
            latitude:54.2,
            longitude:-2.6,
            speed:1.23,
            heading:0.34
        });
        
        $httpBackend.flush();
        
        expect(scope.payload_name).toEqual('$$latex');
        expect(scope.altitude).toEqual(12345);
        expect(scope.latitude).toEqual(54.2);
        expect(scope.longitude).toEqual(-2.6);
        expect(scope.speed).toEqual(1.23);
        expect(scope.heading).toEqual(0.34);
    });
});
