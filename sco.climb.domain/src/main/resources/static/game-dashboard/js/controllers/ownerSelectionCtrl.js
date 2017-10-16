/* global angular */
angular.module('climbGame.controllers.ownerSelection', [])
  .controller('ownerSelectionCtrl', function ($scope, $state, $mdToast, $filter, loginService, CacheSrv) {
      $scope.ownerIds = loginService.getAllOwners();

      $scope.select = function () {
        if ($scope.selectedOwnerId) {
          loginService.setOwnerId($scope.selectedOwnerId);
          $state.go('instituteSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('class_choose_room')))
        }
      }
    }
  )
