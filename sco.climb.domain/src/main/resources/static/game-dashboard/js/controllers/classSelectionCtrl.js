/* global angular */
angular.module('climbGame.controllers.classSelection', [])
  .controller('classSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, 
  		$location, $window, loginService, CacheSrv) {
  		$rootScope.isLoading = false;	
      $scope.classes = loginService.getAllClasses()
      loginService.setSingleClass($scope.classes.length == 1);
      if($scope.classes.length == 1) {
      	loginService.setClassRoom($scope.classes[0]);
      	$state.go('home', loginService.getParams('home'))
      }
      
      $scope.logout = function () {
        loginService.logout()
      }
      
      $scope.select = function () {
        if ($scope.selectedClass) {
          CacheSrv.resetLastCheck('calendar')
          CacheSrv.resetLastCheck('notifications')
          loginService.setClassRoom($scope.selectedClass)
          $state.go('home',loginService.getParams('home'))
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_classroom')))
        }
      }
    }
  )
