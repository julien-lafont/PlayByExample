'use strict';

var app = angular.module('app', ['ngResource', 'ui', 'ui.bootstrap'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'index',
                controller: 'MainCtrl'
            })
            .when('/questions/add', {
                templateUrl: '/api/questions',
                controller: 'QuestionCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    }])
    .config(['$locationProvider', function($locationProvider) {
        $locationProvider.html5Mode(false).hashPrefix('!');
    }]);