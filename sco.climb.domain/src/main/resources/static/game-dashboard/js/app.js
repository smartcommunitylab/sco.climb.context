//  'use strict';
/* global angular */
angular.module('climbGame', [
	'ngAnimate',
	'ui.router',
	'ngMaterial',
	'ngAria',
	'ngMessages',
	'leaflet-directive',
	'ng-drag-scroll',
	'pascalprecht.translate',
	'climbGame.controllers.home',
	'climbGame.controllers.map',
	'climbGame.controllers.calendar',
	'climbGame.controllers.stats',
	'climbGame.controllers.excursions',
	'climbGame.controllers.notifications',
	'climbGame.controllers.login',
	'climbGame.controllers.ownerSelection',
	'climbGame.controllers.instituteSelection',
	'climbGame.controllers.schoolSelection',
	'climbGame.controllers.gameSelection',
	'climbGame.controllers.itinerarySelection',
	'climbGame.controllers.classSelection',
	'climbGame.services.cache',
	'climbGame.services.data',
	'climbGame.services.conf',
	'climbGame.services.map',
	'climbGame.services.login',
	'climbGame.services.profile',
	'climbGame.services.map',
	'climbGame.services.calendar',
	'climbGame.services.classSelection'
])

	.factory('401-403-Error', ['$q', '$injector', '$location', '$window',
		function($q, $injector, $location, $window) {
			var sessionRecoverer = {
				responseError: function(response) {
					if ((response.status == 401) || (response.status == 403)) {
						var mdToast = $injector.get('$mdToast');
						var traslator = $injector.get('$filter');
						mdToast.show(mdToast.simple().content(traslator('translate')('toast_session_expired')));
						setTimeout(function() {
							var loginService = $injector.get('loginService');
							var logoutUrl = loginService.logout();
							var baseAppUrl = $location.$$absUrl.replace($location.$$path, '');
							logoutUrl += '?target=' + baseAppUrl;
							$window.location.href = logoutUrl;
						}, 3000);
					}
					return $q.reject(response);
				}
			};
			return sessionRecoverer;
		}])

	.config(['$httpProvider', function($httpProvider) {
		$httpProvider.interceptors.push('401-403-Error');
	}])

	.config(function($mdThemingProvider) {
		$mdThemingProvider.theme('default')
			.primaryPalette('light-blue', {
				'default': '300'
			})
			.accentPalette('deep-orange', {
				'default': '500'
			})
	})
	.config(function($mdDateLocaleProvider) {

		// Can change week display to start on Monday.
		$mdDateLocaleProvider.firstDayOfWeek = 1;
		// Optional.

		$mdDateLocaleProvider.parseDate = function(dateString) {
			var m = moment(dateString, 'DD/MM/YYYY', true);
			return m.isValid() ? m.toDate() : new Date(NaN);
		};

		$mdDateLocaleProvider.formatDate = function(date) {
			var m = moment(date);
			return m.isValid() ? m.format('DD/MM/YYYY') : '';
		};

	})
  .config(['$translateProvider', function ($translateProvider) {
    // $translateProvider.translations('it', {});
    $translateProvider.preferredLanguage('it')
    $translateProvider.useStaticFilesLoader({
      prefix: 'i18n/',
      suffix: '.json'
    })

    // $translateProvider.useSanitizeValueStrategy('sanitize');
    // $translateProvider.useSanitizeValueStrategy('sanitizeParameters');
    $translateProvider.useSanitizeValueStrategy('escapeParameters')
  }])

	.config(['$stateProvider', '$urlRouterProvider',
	function($stateProvider, $urlRouterProvider) {
		//  $urlRouterProvider.otherwise('/')
		$urlRouterProvider.otherwise(function($injector, $location) {
			var $state = $injector.get('$state')
			$state.go('login')
			return $location.path()
		})

		$stateProvider
			.state('login', {
				url: '/login',
				views: {
					'@': {
						templateUrl: 'templates/login.html',
						controller: 'loginCtrl'
					}
				},
			})
			.state('ownerSelection', {
				url: '/owner',
				views: {
					'@': {
						templateUrl: 'templates/owner-selection.html',
						controller: 'ownerSelectionCtrl'
					}
				}
			})
			.state('instituteSelection', {
				url: '/institute/:owner',
				views: {
					'@': {
						templateUrl: 'templates/institute-selection.html',
						controller: 'instituteSelectionCtrl'
					}
				}
				
			})
			.state('schoolSelection', {
				url: '/school/:owner/:institute',
				views: {
					'@': {
						templateUrl: 'templates/school-selection.html',
						controller: 'schoolSelectionCtrl'
					}
				}

			})
			.state('gameSelection', {
				url: '/game/:owner/:institute/:school',
				views: {
					'@': {
						templateUrl: 'templates/game-selection.html',
						controller: 'gameSelectionCtrl'
					}
				}
			})
			.state('itinerarySelection', {
				url: '/itinerary/:owner/:institute/:school/:game',
				views: {
					'@': {
						templateUrl: 'templates/itinerary-selection.html',
						controller: 'itinerarySelectionCtrl'
					}
				}
			})
			.state('classSelection', {
				url: '/classSelection/:owner/:institute/:school/:game/:itinerary',
				views: {
					'@': {
						templateUrl: 'templates/class-selection.html',
						controller: 'classSelectionCtrl'
					}
				}
			})
			.state('home', {
				url: '/:owner/:institute/:school/:game/:itinerary/:class',
				views: {
					'@': {
						templateUrl: 'templates/home.html/',
						controller: 'HomeCtrl'
					}
				}
			})
						.state('home.class', {
				url: 'class/:owner/:institute/:school/:game/:itinerary/:class',
				views: {
					'content@home': {
						templateUrl: 'templates/calendar.html',
						controller: 'calendarCtrl'
					}
				}
			})
			.state('home.map', {
				url: 'map/:owner/:institute/:school/:game/:itinerary/:class',
				views: {
					'content@home': {
						templateUrl: 'templates/map.html',
						controller: 'mapCtrl'
					}
				}
			})

			.state('home.excursions', {
				url: 'excursions/:owner/:institute/:school/:game/:itinerary/:class',
				views: {
					'content@home': {
						templateUrl: 'templates/excursions.html',
						controller: 'excursionsCtrl'
					}
				}
			})
			.state('home.notifications', {
				url: 'notifications/:owner/:institute/:school/:game/:itinerary/:class',
				views: {
					'content@home': {
						templateUrl: 'templates/notifications.html',
						controller: 'notificationsCtrl'
					}
				}
			})
			.state('home.stats', {
				url: 'stats/:owner/:institute/:school/:game/:itinerary/:class',
				views: {
					'content@home': {
						templateUrl: 'templates/stats.html',
						controller: 'statsCtrl'
					}
				}
			})
	}
])

	// take all whitespace out of string
	.filter('nospace', function() {
		return function(value) {
			return (!value) ? '' : value.replace(/ /g, '')
		}
	})

	// replace uppercase to regular case
	.filter('humanizeDoc', function() {
		return function(doc) {
			if (doc) {
				if (doc.type === 'directive') {
					return doc.name.replace(/([A-Z])/g, function($1) {
						return '-' + $1.toLowerCase()
					})
				}
				return doc.label || doc.name
			}
		}
	})
	.run(function($rootScope, $state) {
		$rootScope.$on('$stateChangeStart', function(e, toState, toParams, fromState, fromParams) {
			if (toState.name != 'login' && toState.name != 'ownerSelection') {


				if (!toParams) {
					e.preventDefault();
					$state.go('login');
					return;
				}
				else if (toState.name == 'instituteSelection' && !toParams.owner) {
					console.log(JSON.stringify(toState));
					e.preventDefault();
					$state.go('login');
					return;
				} else if (toState.name == 'schoolSelection' && !toParams.owner && !toParams.institute) {
					console.log(JSON.stringify(toState));
					e.preventDefault();
					$state.go('login');
					return;
				} else if (toState.name == 'gameSelection' && !toParams.owner && !toParams.institute && !toParams.school) {
					console.log(JSON.stringify(toState));
					e.preventDefault();
					$state.go('login');
					return;
				} else if (toState.name == 'itinerarySelection' & !toParams.owner && !toParams.institute && !toParams.school && !toParams.game) {
					console.log(JSON.stringify(toState));
						e.preventDefault();
					$state.go('login');
					return;
				} else if (toState.name == 'classSelection' && !toParams.owner && !toParams.institute && !toParams.school && !toParams.itinerary) {
					console.log(JSON.stringify(toState));
					e.preventDefault();
					$state.go('login');
					return;
				} else if (!toParams.owner && !toParams.institute && !toParams.school && !toParams.itinerary && !toParams.class) {
					console.log(JSON.stringify(toState));
					e.preventDefault();
					$state.go('login');
					return;
				}
			}
		})
	});
