/* global angular */
angular.module('climbGame.controllers.schoolSelection', [])
  .controller('schoolSelectionCtrl', function ($scope, $state, $mdToast, $filter, loginService, CacheSrv, dataService) {
      $scope.schools = []; 
      if(loginService.getSchoolId()) {
      	$state.go('gameSelection')
      } else {
        dataService.getSchool().then(
          	function(data) {
          		$scope.schools = data;
          	}, 
          	function (err) {
          		console.log(err)
          		//Toast the Problem
          		$mdToast.show($mdToast.simple().content($filter('translate')('toast_api_error')))
          	}
        );	
      }

      $scope.select = function () {
        if ($scope.selectedSchool) {
          loginService.setSchoolId($scope.selectedSchool.objectId);
          $state.go('gameSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_school')))
        }
      }
  })
