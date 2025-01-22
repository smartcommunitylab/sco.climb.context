var consoleApp = angular.module('console', ['ui.bootstrap',
	'ui.router',
	'ui.sortable',
	'consoleControllers.mainCtrl',
	'consoleControllers.paths',
	'consoleControllers.pathclone',
	'consoleControllers.leg',
	'consoleControllers.games',
	'consoleControllers.gameconfig',
	'consoleControllers.institutes',
	'consoleControllers.schools',
	'consoleControllers.line',
	'DataService',
	'MainDataService',
	'MapsService',
	'ImgurService',
	'PermissionsService',
	// 'ng.oidcclient',
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

consoleApp.directive('stringToNumber', function () {
	return {
		require: 'ngModel',
		link: function (scope, element, attrs, ngModel) {
			ngModel.$parsers.push(function (value) {
				return '' + value;
			});
			ngModel.$formatters.push(function (value) {
				return parseFloat(value);
			});
		}
	};
});
consoleApp.config(function () {
})
consoleApp.config(function ($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise('/institutes-list');
	$stateProvider
		.state('root', {
			url: '',
			abstract: true,
			templateUrl: 'templates/header.html',
			controller: 'MainCtrl'
		})
		.state('root.terms', {
			url: '/terms',
			templateUrl: 'templates/terms.html',
			controller: 'MainCtrl'
		})
		.state('root.registration', {
			url: '/registration',
			templateUrl: 'templates/registration.html',
			controller: 'MainCtrl'
		})
		.state('root.paths-list', {
			url: '/paths-list',
			templateUrl: 'templates/paths/paths-list.html',
			controller: 'PathsCtrl'
		})
		.state('root.path', {
			url: '/path/:idDomain/:idInstitute/:idSchool/:idGame/:idPath',
			templateUrl: 'templates/paths/path.html',
			controller: 'PathCtrl'
		})
		.state('root.pathclone', {
			url: '/paths-list/:idDomain/:idInstitute/:idSchool/:idGame/pathclone',
			templateUrl: 'templates/paths/pathclone.html',
			controller: 'PathCloneCtrl'
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
			url: '/game/:idDomain/:idInstitute/:idSchool/:idGame',
			templateUrl: 'templates/games/game.html',
			controller: 'GameCtrl'
		})
		.state('root.gameconfig', {
			url: '/game/:idDomain/:idInstitute/:idSchool/:idGame/gameconfig',
			templateUrl: 'templates/games/gameconfig.html',
			controller: 'GameConfigCtrl'
		})
		.state('root.game.info', {
			url: '/info',
			templateUrl: 'templates/games/tabs/info.html',
			controller: 'GameInfoCtrl'
		})
		.state('root.game.params', {
			url: '/params',
			templateUrl: 'templates/games/tabs/params.html',
			controller: 'GameParamsCtrl'
		})
		.state('root.game.gamers', {
			url: '/gamers',
			templateUrl: 'templates/games/tabs/gamers.html',
			controller: 'GameGamersCtrl'
		})
		.state('root.game.player', {
			url: '/gamers/:idPlayer',
			templateUrl: 'templates/games/player.html',
			controller: 'PlayerCtrl'
		})
		.state('root.game.calibration', {
			url: '/calibration',
			templateUrl: 'templates/games/tabs/calibration.html',
			controller: 'GameCalibrationCtrl'
		})
		.state('root.game.pedibus', {
			url: '/pedibus',
			templateUrl: 'templates/games/tabs/pedibus.html',
			controller: 'GamePedibusCtrl'
		})
		.state('root.institutes-list', {
			url: '/institutes-list',
			templateUrl: 'templates/institutes/institutes-list.html',
			controller: 'InstitutesListCtrl'
		})
		.state('root.institute', {
			url: '/institute/:idDomain/:idInstitute',
			templateUrl: 'templates/institutes/institute.html',
			abstract: true
		})
		.state('root.institute.info', {
			url: '/info',
			templateUrl: 'templates/institutes/tabs/info.html',
			controller: 'InstituteCtrl'
		})
		.state('root.schools-list', {
			url: '/schools-list',
			templateUrl: 'templates/schools/schools-list.html',
			controller: 'SchoolsListCtrl'
		})
		.state('root.school', {
			url: '/school/:idDomain/:idInstitute/:idSchool',
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
		.state('root.school.children-list', {
			url: '/children-list',
			templateUrl: 'templates/schools/tabs/children-list.html',
			controller: 'ChildrenCtrl'
		})
		.state('root.school.child', {
			url: '/child/:idChild',
			templateUrl: 'templates/schools/child.html',
			controller: 'ChildCtrl'
		})


		.state('root.school.volunteer-list', {
			url: '/volunteer-list',
			templateUrl: 'templates/schools/tabs/volunteer-list.html',
			controller: 'VolunteerListCtrl'
		})
		.state('root.school.volunteer', {
			url: '/volunteer/:idVolunteer',
			templateUrl: 'templates/schools/volunteer.html',
			controller: 'VolunteerCtrl'
		})
}).factory('BearerAuthInterceptor', function ($window, $q, $rootScope) {
	return {
		request: function (config) {
			config.headers = config.headers || {};
			var user = JSON.parse($window.sessionStorage.getItem('user'))
			if (user) {
				config.headers.Authorization = 'Bearer ' + user.access_token;
			}
			return config || $q.when(config);
		},
		requestError: function (rejection) {
			return $q.reject(rejection);
		},
		response: function (response) {
			if (response.status === 403 || response.status === 401) {
				//  Redirect user to login page / signup Page.
				//check if token is present and is valid, otherwise login
				sessionStorage.setItem('state', JSON.stringify(window.location));
				var mgr = new Oidc.UserManager(
					auth_conf);
				mgr.signinRedirect()
			}
			else return response || $q.when(response);
			//return response ;
		},
		responseError: function (rejection) {
			if (rejection.status === 403 || rejection.status === 401) {
				//  Redirect user to login page / signup Page.
				//check if token is present and is valid, otherwise login
				sessionStorage.setItem('state', JSON.stringify(window.location));
				var mgr = new Oidc.UserManager(auth_conf);
				 
				mgr.signinRedirect()
			}
			else return rejection || $q.when(rejection);
			
		}
	};
}).config(function ($httpProvider) {
	$httpProvider.interceptors.push('BearerAuthInterceptor');
}).run(

	function ($rootScope, DataService) {
		google.maps.LatLng.prototype.kmTo = function (a) {
			var e = Math, ra = e.PI / 180;
			var b = this.lat() * ra, c = a.lat() * ra, d = b - c;
			var g = this.lng() * ra - a.lng() * ra;
			var f = 2 * e.asin(e.sqrt(e.pow(e.sin(d / 2), 2) + e.cos(b) * e.cos
				(c) * e.pow(e.sin(g / 2), 2)));
			return f * 6378.137;
		}
		google.maps.Polyline.prototype.inKm = function (n) {
			var a = this.getPath(n), len = a.getLength(), dist = 0;
			for (var i = 0; i < len - 1; i++) {
				dist += a.getAt(i).kmTo(a.getAt(i + 1));
			}
			return dist;
		}
	});