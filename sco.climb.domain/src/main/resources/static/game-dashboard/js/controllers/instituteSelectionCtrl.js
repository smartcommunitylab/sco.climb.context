/* global angular */
angular.module('climbGame.controllers.instituteSelection', [])
  .controller('instituteSelectionCtrl', function ($scope, $state, $mdToast, $filter, loginService, CacheSrv, dataService) {
      $scope.institutes = []; 
      if(loginService.getInstituteId()) {
      	$state.go('schoolSelection')
      } else {
        dataService.getInstitute().then(
          	function(data) {
          		$scope.institutes = data;
          	}, 
          	function (err) {
          		console.log(err)
          		//Toast the Problem
          		$mdToast.show($mdToast.simple().content($filter('translate')('toast_uname_not_valid')))
          	}
        );	
      }

      $scope.select = function () {
        if ($scope.selectedInstitute) {
          loginService.setInstituteId($scope.selectedInstitute.objectId);
          $state.go('schoolSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('class_choose_room')))
        }
      }
  })
