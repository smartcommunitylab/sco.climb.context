/* global angular */
angular.module('climbGame.controllers.ownerSelection', [])
  .controller('ownerSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, 
  		$location, $window, loginService, CacheSrv) {
  		$rootScope.isLoading = true;
      $scope.ownerIds = loginService.getAllOwners();
      if($scope.ownerIds.length == 0) {
    		$mdToast.show($mdToast.simple().content($filter('translate')('toast_api_error')))
      } else {
        if(loginService.getOwnerId()) {
        	$state.go('instituteSelection');
        }
        if($scope.ownerIds.length == 1) {
        	loginService.setOwnerId($scope.ownerIds[0]);
        	$state.go('instituteSelection');
        }
      }
      $rootScope.isLoading = false;
      
      $scope.logout = function () {
        var logoutUrl = loginService.logout()
        var baseAppUrl = $location.$$absUrl.replace($location.$$path,'');
        logoutUrl += '?target=' + baseAppUrl;
        $window.location.href = logoutUrl;
      }

      $scope.select = function () {
        if ($scope.selectedOwnerId) {
          loginService.setOwnerId($scope.selectedOwnerId);
          $state.go('instituteSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_owner')))
        }
      }
    }
  )
