/* global angular */
angular.module('climbGame.controllers.home', [])
  .controller('HomeCtrl', function ($rootScope, $scope, $log, $state, $mdToast, $filter, $mdSidenav, 
  		$timeout, $location, $window, loginService, CacheSrv) {
       $state.go('home.homepage')
       //$state.go('home.stats')

       $scope.go = function (path, params) {
        $scope.closeSideNavPanel()
        $state.go(path, params)
      }

      $scope.isCurrentState = function (state) {
        return $state.includes(state)
      }

      $scope.logout = function () {
        CacheSrv.resetLastCheck('calendar');
        CacheSrv.resetLastCheck('notifications');
        loginService.logout()
        // var logoutUrl = loginService.logout()
        // var baseAppUrl = $location.$$absUrl.replace($location.$$path,'');
        // logoutUrl += '?target=' + baseAppUrl;
        // $window.location.href = logoutUrl;
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
      $scope.backHome = function () {
        $state.go('home.homepage');
      }
      $scope.isHome = function() {
       return ($state.current.name === 'home.homepage')
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

      $scope.showChangeOption = function(type) {
        switch (type) {
          case 'institute':
            return !loginService.getSingleInstitute();
          case 'school':
            return !loginService.getSingleSchool();
          case 'class':
            return !loginService.getSingleClass();
          case 'game':
            return !loginService.getSingleGame();
          case 'itinerary':
            return !loginService.getSingleItinerary();
        }
      }

    }
  )
