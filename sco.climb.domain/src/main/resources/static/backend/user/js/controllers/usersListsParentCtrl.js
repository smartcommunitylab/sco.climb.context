/* global angular */
angular.module('climbGameUser.controllers.users.lists', [])
.controller('usersListsParentCtrl', ['$scope', '$rootScope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $rootScope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
    $rootScope.title = "Users List";
    /*$rootScope.selectedDomain = $stateParams.currentDomain;
    dataService.setCurrentDomain($stateParams.currentDomain);*/
    $scope.newUser = function() {
      $state.go("home.user-edit");
    }
  }
])
