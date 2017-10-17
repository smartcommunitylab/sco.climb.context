/* global angular */
angular.module('climbGame.controllers.classSelection', [])
  .controller('classSelectionCtrl', ['$scope', '$state', '$mdToast', '$filter', 'loginService', 'CacheSrv',
    function ($scope, $state, $mdToast, $filter, loginService, CacheSrv) {
      $scope.classes = loginService.getAllClasses()
      if(loginService.getClassRoom()) {
      	$state.go('home')
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
  ])
