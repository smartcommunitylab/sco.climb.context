/* global angular */
angular.module('climbGame.controllers.instituteSelection', [])
  .controller('instituteSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, 
  		$location, $window, loginService, CacheSrv, dataService) {
  		$rootScope.isLoading = true;
  		$scope.institutes = []; 
      if(loginService.getInstituteId()) {
      	$state.go('schoolSelection',loginService.getParams('schoolSelection'))
      } else {
        dataService.getInstitute().then(
          	function(data) {
							$scope.institutes = data;
							if($scope.institutes.length == 0) {
	          		//Toast the Problem
	          		$mdToast.show($mdToast.simple().content($filter('translate')('toast_api_error')))
							} else {
								loginService.setSingleInstitute($scope.institutes.length == 1);
	          		if($scope.institutes.length == 1) {
	          			loginService.setInstituteId($scope.institutes[0].objectId)
									$state.go('schoolSelection',loginService.getParams('schoolSelection'))
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
        if ($scope.selectedInstitute) {
          loginService.setInstituteId($scope.selectedInstitute.objectId);
          $state.go('schoolSelection',loginService.getParams('schoolSelection'))
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_institute')))
        }
      }
  })
