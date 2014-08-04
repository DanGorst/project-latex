'use strict';


// Declare app level module which depends on filters, and services
angular.module('projectLatexApp', [
  'ngRoute',
  'projectLatexApp.filters',
  'projectLatexApp.services',
  'projectLatexApp.directives',
  'projectLatexApp.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/telemetry', {templateUrl: 'partials/telemetry.html', controller: 'TelemetryCtrl'});
  $routeProvider.otherwise({redirectTo: '/telemetry'});
}]);
