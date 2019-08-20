/* global angular */
angular.module('climbGame.controllers.gameSelection', [])
  .controller('gameSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, 
  		$location, $window, loginService, CacheSrv, dataService) {
  		$rootScope.isLoading = true;
      $scope.games = []; 
      if(loginService.getGameId()) {
      	$state.go('itinerarySelection')
      } else {
        dataService.getGame().then(
          	function(data) {
          		$scope.games = data;
          		if($scope.games.length == 0) {
          			$mdToast.show($mdToast.simple().content($filter('translate')('toast_api_error')))
          		} else {
                loginService.setSingleGame($scope.games.length == 1);
            		if($scope.games.length == 1) {
            			loginService.setGameId($scope.games[0].objectId)
            			loginService.setAllClasses($scope.games[0].classRooms);
            			$state.go('itinerarySelection')
            		}          			
          		}
          		$rootScope.isLoading = false;
          	}, 
          	function (err) {
          		console.log(err)
          		//Toast the Problem
          		$mdToast.show($mdToast.simple().content($filter('translate')('toast_api_error')))
          	}
          );	
      }
      
      $scope.logout = function () {
        var logoutUrl = loginService.logout()
        var baseAppUrl = $location.$$absUrl.replace($location.$$path,'');
        logoutUrl += '?target=' + baseAppUrl;
        $window.location.href = logoutUrl;
      }

      $scope.select = function () {
        if ($scope.selectedGame) {
          loginService.setGameId($scope.selectedGame.objectId);
          loginService.setAllClasses($scope.selectedGame.classRooms);
          $state.go('itinerarySelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_game')))
        }
      }
  })
