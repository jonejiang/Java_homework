'use strict';

/* Controllers */

var gamerApp = angular.module('gamerApp', ['ngRoute']);

gamerApp.config(['$locationProvider', '$routeProvider',
    function ($locationProvider, $routeProvider) {

        $locationProvider.hashPrefix('');

        $routeProvider.when("/", {
            templateUrl: 'detail.html'
        }).when("/detail/:id", {
            templateUrl: 'detail.html',
            controller: DetailCntl
        }).otherwise({
            redirectTo: '/'
        });
    }
]);

AppCntl.$inject = ['$scope', '$route'];

function AppCntl($scope, $route, $location) {
    console.log("Routing to " + $route);
    $scope.$route = $route;
}

function DetailCntl($scope, $route, $location, $http) {

    $scope.init = function () {
        console.log("Calling game service for " + $route.current.params.id);
        $http.get("http://localhost:8181/" + $route.current.params.id).then(success, error);
    };

    $scope.init();

    function success(response) {
        var data = response.data;
        console.log(data);
        $scope.selectedGame = data;
    }

    function error(error) {
        console.log("Unexpected error:");
        console.log(error);
    }
}

gamerApp.controller('gamerCtrl', function ($scope, $http) {

    $scope.searchText = "";

    $scope.searchGame = function () {
        $http.get("http://localhost:8181?query=" + $scope.searchText).then(success, error);
    };

    function success(response) {
        $scope.games = response.data;
    }

    function error(error) {
        console.log("Unexpected error: " + error);
    }

});
