'use strict';

angular.module('uiApp', [])
    .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {

        $locationProvider.html5Mode(true);

        $routeProvider
            .when('/ui', {
                templateUrl: 'ui/views/main.html',
                controller: 'MainCtrl'
            })
            .when('/undefined', {
              templateUrl: 'ui/views/undefined.html',
              controller: 'UndefinedCtrl'
            })
            .when('/ui/myhome', {
              templateUrl: 'ui/views/myhome.html',
              controller: 'MyhomeCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    }]);
