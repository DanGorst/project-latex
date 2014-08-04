'use strict';

/* Controllers */

angular.module('projectLatexApp.controllers', [])
  .controller('TelemetryCtrl', ['$scope', function($scope) {
      function showNoData() {
          var noDataString = "No data";
          $scope.payload_name = noDataString;
          $scope.time = noDataString;
          $scope.altitude = noDataString;
          $scope.latitude = noDataString;
          $scope.longitude = noDataString;
          $scope.speed = noDataString;
          $scope.heading = noDataString;
          $scope.temp_internal = noDataString;
          $scope.temp_external = noDataString;
      }
      
      // Until we get data from the database, indicate that we don't have any
      showNoData();
  }]);
