/* global angular */
angular.module('climbGame.controllers.itinerarySelection', [])
  .controller('itinerarySelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, 
  		$location, $window, loginService, CacheSrv, dataService) {
  		$rootScope.isLoading = true;
      $scope.itineraries = []; 
      if(loginService.getItineraryId()) {
      	$state.go('classSelection',loginService.getParams('classSelection'))
      } else {
        dataService.getItinerary().then(
          	function(data) {
          		$scope.itineraries = data;
          		if($scope.itineraries.length == 0) {
          			$mdToast.show($mdToast.simple().content($filter('translate')('toast_api_error')))
          		} else {
                loginService.setSingleItinerary($scope.itineraries.length == 1);
            		if($scope.itineraries.length == 1) {
            			loginService.setItineraryId($scope.itineraries[0].objectId)
            			$state.go('classSelection',loginService.getParams('classSelection'))
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
        if ($scope.selectedItinerary) {
          loginService.setItineraryId($scope.selectedItinerary.objectId);
          $state.go('classSelection',loginService.getParams('classSelection'))
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_itinerary')))
        }
      }
  })
