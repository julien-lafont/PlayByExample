'use strict';

var app = angular.module('app', ['ngResource', 'ui', 'ui.bootstrap'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/index',
                controller: 'MainCtrl'
            })
            .when('/repo/:owner/:name', {
                templateUrl: 'views/gist.html',
                controller: 'GistCtrl'
            })
            .when('/user/:login', {
                templateUrl: 'views/user.html',
                controller: 'UserCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    }])
    .config(['$locationProvider', function($locationProvider) {
        $locationProvider.html5Mode(true); // .hashPrefix('!')
    }]);