/* global angular */
angular.module('climbGame.controllers.itinerarySelection', [])
  .controller('itinerarySelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, loginService, CacheSrv, dataService) {
  		$rootScope.isLoading = true;
      $scope.itineraries = []; 
      if(loginService.getItineraryId()) {
      	$state.go('classSelection')
      } else {
        dataService.getItinerary().then(
          	function(data) {
          		$scope.itineraries = data;
          		if($scope.itineraries.length == 1) {
          			loginService.setItineraryId($scope.itineraries[0].objectId)
          			loginService.setAllClasses($scope.itineraries[0].classRooms);
          			$state.go('classSelection')
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

      $scope.select = function () {
        if ($scope.selectedItinerary) {
          loginService.setItineraryId($scope.selectedItinerary.objectId);
          loginService.setAllClasses($scope.selectedItinerary.classRooms);
          $state.go('classSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_itinerary')))
        }
      }
  })