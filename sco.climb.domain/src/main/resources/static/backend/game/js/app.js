var consoleApp = angular.module('console', ['ui.bootstrap',
'ui.router',
'ui.sortable',
'consoleControllers.mainCtrl',
'consoleControllers.paths',
'consoleControllers.leg',
'consoleControllers.games',
'consoleControllers.schools',
'consoleControllers.line',
'DataService',
'MapsService',
'ImgurService',
'ngUpload',
'checklist-model',
'textAngular',
'naif.base64',
'angular-loading-bar',
'angularSpinner',
'fundoo.services'
]);

// Text editor toolbar config
consoleApp.config(['$provide', function ($provide) {
    $provide.decorator('taOptions', ['$delegate', function (taOptions) {
        taOptions.toolbar = [
                      ['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p', 'pre', 'quote'],
                      ['bold', 'italics', 'underline', 'strikeThrough', 'ul', 'ol', 'redo', 'undo'],
                      ['html', 'insertImage', 'insertLink', 'insertVideo', 'indent', 'outdent'],
                      ['wordcount', 'charcount']
                  ];
        return taOptions;
    }]);
}]);


consoleApp.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/games-list');
    $stateProvider
        .state('root', {
            url: '',
            abstract: true,
            templateUrl: 'templates/header.html',
            controller: 'MainCtrl'
        })
        .state('root.paths-list', {
            url: '/paths-list',
            templateUrl: 'templates/paths/paths-list.html',
            controller: 'PathsCtrl'
        })
        .state('root.path', {
            url: '/path/:idPath',
            templateUrl: 'templates/paths/path.html',
            controller: 'PathCtrl'
        })
        .state('root.path.info', {
            url: '/info',
            templateUrl: 'templates/paths/tabs/info.html',
            controller: 'InfoCtrl'
        })
        .state('root.path.legs', {
            url: '/legs',
            templateUrl: 'templates/paths/tabs/legs-list.html',
            controller: 'LegsListCtrl'
        })
        .state('root.path.map', {
            url: '/map',
            templateUrl: 'templates/paths/tabs/path-shape.html',
            controller: 'MapCtrl'
        })
        .state('root.path.leg', {
            url: '/leg/:idLeg',
            templateUrl: 'templates/paths/leg.html',
            controller: 'LegCtrl'
        })
        .state('root.games-list', {
            url: '/games-list',
            templateUrl: 'templates/games/games-list.html',
            controller: 'GamesListCtrl'
        })
        .state('root.game', {
            url: '/game/:idGame',
            templateUrl: 'templates/games/game.html',
            controller: 'GameCtrl'
        })
        .state('root.game.info', {
            url: '/info',
            templateUrl: 'templates/games/info.html',
            controller: 'GameInfoCtrl'
        })
        .state('root.schools-list', {
            url: '/schools-list',
            templateUrl: 'templates/schools/schools-list.html',
            controller: 'SchoolsListCtrl'
        })
        .state('root.school', {
            url: '/school/:idSchool',
            templateUrl: 'templates/schools/school.html',
            controller: 'SchoolCtrl'
        })
        .state('root.school.info', {
            url: '/info',
            templateUrl: 'templates/schools/tabs/info.html',
            controller: 'SchoolInfoCtrl'
        })
        .state('root.school.lines-list', {
            url: '/lines-list',
            templateUrl: 'templates/schools/tabs/lines-list.html',
            controller: 'LinesListCtrl'
        })
        .state('root.school.line', {
            url: '/line/:idLine',
            templateUrl: 'templates/schools/line.html',
            controller: 'LineCtrl'
        })
});

consoleApp.run(['$rootScope', '$q', '$location', 'DataService',
  function ($localize, $rootScope, $q, $location, DataService, CodeProcessor, ValidationService) {
        $rootScope.logout = function (url) {
            DataService.logout().then(function () {
                window.location.reload();
            });
        };
  }]);