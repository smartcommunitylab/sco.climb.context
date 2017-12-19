/* global angular */
angular.module('climbGameUser.controllers.home', [])
  .controller('HomeCtrl', function ($rootScope, $scope, $log, $state, $stateParams, $mdToast, $filter, $mdSidenav, 
  		$timeout, $location, $window, dataService, loginService) {
        $scope.goBack = function() {
          $state.go($scope.backStateToGo);
        }
        $scope.$state = $state;
        dataService.getProfile().then(
          function (data) {
            $scope.myProfile = data;
            if ($scope.myProfile.ownerIds.length == 1)  {
              
            }
          }
        );
        if ($stateParams.currentDomain == 'selectDomain') {
          $stateParams.currentDomain = '';
          $scope.title = 'Select domain';
        }
        $scope.selectedDomain = $stateParams.currentDomain;
        dataService.setCurrentDomain($stateParams.currentDomain);
        $scope.changeDomain = function(domain) {
          $scope.selectedDomain = domain;
          dataService.setCurrentDomain(domain);
          $state.go('home.users-lists.list', {currentDomain: domain});
        }
    }
  )
