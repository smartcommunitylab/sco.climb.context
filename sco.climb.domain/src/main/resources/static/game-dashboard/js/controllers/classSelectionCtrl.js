/* global angular */
angular.module('climbGame.controllers.classSelection', [])
  .controller('classSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, 
  		$location, $window, loginService, CacheSrv) {
  		$rootScope.isLoading = false;	
      $scope.classes = loginService.getAllClasses()
      loginService.setSingleClass($scope.classes.length == 1);
      if($scope.classes.length == 1) {
      	loginService.setClassRoom($scope.classes[0]);
      	$state.go('home')
      }
      
      $scope.logout = function () {
        var logoutUrl = loginService.logout()
        var baseAppUrl = $location.$$absUrl.replace($location.$$path,'');
        logoutUrl += '?target=' + baseAppUrl;
        $window.location.href = logoutUrl;
      }
      
      $scope.select = function () {
        if ($scope.selectedClass) {
          CacheSrv.resetLastCheck('calendar')
          CacheSrv.resetLastCheck('notifications')
          loginService.setClassRoom($scope.selectedClass)
          $state.go('home')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_classroom')))
        }
      }
    }
  )
