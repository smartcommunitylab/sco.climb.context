/* global angular */
angular.module('climbGame.controllers.itinerarySelection', [])
  .controller('itinerarySelectionCtrl', function ($scope, $state, $mdToast, $filter, loginService, CacheSrv, dataService) {
      $scope.itineraries = []; 
      dataService.getItinerary().then(
      	function(data) {
      		$scope.itineraries = data;
      	}, 
      	function (err) {
      		console.log(err)
      		//Toast the Problem
      		$mdToast.show($mdToast.simple().content($filter('translate')('toast_uname_not_valid')))
      	}
      );	

      $scope.select = function () {
        if ($scope.selectedItinerary) {
          loginService.setItineraryId($scope.selectedItinerary.objectId);
          loginService.setAllClasses($scope.selectedItinerary.classRooms);
          $state.go('classSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('class_choose_room')))
        }
      }
  })
