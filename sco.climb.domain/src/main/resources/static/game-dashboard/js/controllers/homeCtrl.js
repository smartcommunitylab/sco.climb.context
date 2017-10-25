/* global angular */
angular.module('climbGame.controllers.home', [])
  .controller('HomeCtrl', function ($rootScope, $scope, $log, $state, $mdToast, $filter, $mdSidenav, 
  		$timeout, $location, $window, loginService, CacheSrv) {
      $state.go('home.class')
      // $state.go('home.stats')

      $scope.go = function (path) {
        $scope.closeSideNavPanel()
        $state.go(path)
      }

      $scope.isCurrentState = function (state) {
        return $state.includes(state)
      }

      $scope.logout = function () {
        CacheSrv.resetLastCheck('calendar')
        CacheSrv.resetLastCheck('notifications')
        var logoutUrl = loginService.logout()
        logoutUrl = logoutUrl + '?target=' + $location.path('/').absUrl()
        $window.location.href = logoutUrl
      }

      $scope.changeClass = function (path) {
        loginService.removeClass()
        $state.go('classSelection')
      }

      $scope.changeItinerary = function (path) {
      	loginService.removeClass()
      	loginService.removeClasses()
        loginService.removeItinerary()
        $state.go('itinerarySelection')
      }
      
      $scope.changeGame = function (path) {
      	loginService.removeClass()
      	loginService.removeClasses()
        loginService.removeItinerary()
        loginService.removeGame()
        $state.go('gameSelection')
      }
      
      $scope.changeSchool = function (path) {
      	loginService.removeClass()
      	loginService.removeClasses()
        loginService.removeItinerary()
        loginService.removeGame()
        loginService.removeSchool()
        $state.go('schoolSelection')
      }
      
      $scope.changeInstitute = function (path) {
      	loginService.removeClass()
      	loginService.removeClasses()
        loginService.removeItinerary()
        loginService.removeGame()
        loginService.removeSchool()
        loginService.removeInstitute()
        $state.go('instituteSelection')
      }
      
      $scope.openSideNavPanel = function () {
        $mdSidenav('leftMenu').open()
      }

      $scope.closeSideNavPanel = function () {
        $mdSidenav('leftMenu').close()
      }

      $scope.convertFields = function (obj) {
        var convertibleFields = ['_bonus_', '_record_', '_performance_', 'target', '_totalKm_']
        angular.forEach(convertibleFields, function (field) {
          if (obj && obj[field]) {
            obj[field] = Math.floor(obj[field] / 1000)
          }
        })
        return obj
      }
    }
  )
