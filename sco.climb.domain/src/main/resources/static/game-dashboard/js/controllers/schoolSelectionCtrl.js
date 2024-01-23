/* global angular */
angular.module('climbGame.controllers.schoolSelection', [])
  .controller('schoolSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, 
  		$location, $window, loginService, CacheSrv, dataService) {
  		$rootScope.isLoading = true;
      $scope.schools = []; 
      if(loginService.getSchoolId()) {
      	$state.go('gameSelection',loginService.getParams('gameSelection'))
      } else {
        dataService.getSchool().then(
          	function(data) {
              $scope.schools = data;
              if($scope.schools.length == 0) {
              	$mdToast.show($mdToast.simple().content($filter('translate')('toast_api_error')))
              } else {
  							loginService.setSingleSchool($scope.schools.length == 1);
            		if($scope.schools.length == 1) {
            			loginService.setSchoolId($scope.schools[0].objectId)
            			$state.go('gameSelection',loginService.getParams('gameSelection'));
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
        loginService.logout();
      }
      
      $scope.select = function () {
        if ($scope.selectedSchool) {
          loginService.setSchoolId($scope.selectedSchool.objectId);
          $state.go('gameSelection',loginService.getParams('gameSelection'))
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_school')))
        }
      }
  })
