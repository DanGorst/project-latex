'use strict';

/* Controllers */

angular.module('projectLatexApp.controllers', [])
  .controller('TelemetryCtrl', ['$scope', '$http', '$interval', function($scope, $http, $interval) {
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
      
      function formatDateString(dateString) {
          var date = new Date(dateString);
          return date.toString();
      }
      
      function queryDatabaseServer()    {
        $http.get('http://localhost:4000/latest')
        .success(function(data) {
            $scope.payload_name = data.payload_name;
            $scope.time = formatDateString(data.time);
            $scope.altitude = data.altitude;
            $scope.latitude = data.latitude;
            $scope.longitude = data.longitude;
            $scope.speed = data.speed;
            $scope.heading = data.heading;
            $scope.temp_internal = data.temp_internal;
            $scope.temp_external = data.temp_external;
        })
        .error(function(err) {
            showNoData();
        });
      }
      
      // Until we get data from the database, indicate that we don't have any
      showNoData();
    
      $interval(queryDatabaseServer, 1000);
  }]);
