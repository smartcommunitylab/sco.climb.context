/* global angular */
angular.module('climbGame.controllers.schoolSelection', [])
  .controller('schoolSelectionCtrl', function ($scope, $state, $mdToast, $filter, loginService, CacheSrv, dataService) {
      $scope.schools = []; 
      dataService.getSchool().then(
      	function(data) {
      		$scope.schools = data;
      	}, 
      	function (err) {
      		console.log(err)
      		//Toast the Problem
      		$mdToast.show($mdToast.simple().content($filter('translate')('toast_uname_not_valid')))
      	}
      );	

      $scope.select = function () {
        if ($scope.selectedSchool) {
          loginService.setSchoolId($scope.selectedSchool.objectId);
          $state.go('gameSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('class_choose_room')))
        }
      }
  })
