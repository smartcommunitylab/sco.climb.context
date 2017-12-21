/* global angular */
angular.module('climbGameUser.services.data', [])
  .factory('dataService', function ($q, $http, configService, loginService) {
    var dataService = {};
    var myProfile;
    var currentDomain;
    
    dataService.getProfile = function () {
      var deferred = $q.defer()
      if (myProfile) deferred.resolve(myProfile);
      else {
        $http({
          method: 'GET',
          url: configService.getURL() + '/api/profile', 
          headers: {
            'Accept': 'application/json',
            'Authorization': 'Bearer ' + loginService.getUserToken()
          },
          timeout: configService.httpTimout(),
        }).then(function (response) {
          myProfile = response.data;
          deferred.resolve(response.data)
        }, function (reason) {
          console.log(reason)
          deferred.reject(reason)
        })
      }      
      return deferred.promise
    }
    dataService.getCurrentDomain = function() {
      return currentDomain;
    }
    dataService.setCurrentDomain = function(domain) {
      currentDomain = domain;
    }

    dataService.getUsersByRole = function (role) {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/role/' + currentDomain + '/users', 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
        },
        timeout: configService.httpTimout(),
        params: {
          role: role
        }
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    dataService.getUserByEmail = function (email) {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/role/' + currentDomain + '/user', 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
        },
        timeout: configService.httpTimout(),
        params: {
          email: email
        }
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }
    
    dataService.removeRole = function (role, user) {
      var deferred = $q.defer()
      $http({
        method: 'DELETE',
        url: configService.getURL() + '/api/role/' + currentDomain + '/' + role, 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
        },
        timeout: configService.httpTimout(),
        params: {
          email: user.email
        }
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    dataService.addRole = function (role, user) {
      var deferred = $q.defer()
      $http({
        method: 'POST',
        url: configService.getURL() + '/api/role/' + currentDomain + '/' + role, 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
        },
        timeout: configService.httpTimout(),
        params: {
          email: user.email,
          instituteId: user.instituteId,
          schoolId: user.schoolId,
          gameId: user.gameId,
        }
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }


    dataService.saveUser = function (user) {
      var deferred = $q.defer()
      $http({
        method: 'POST',
        url: configService.getURL() + '/api/role/' + currentDomain + '/user', 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
        },
        timeout: configService.httpTimout(),
        data: {
          name: user.name,
          surname: user.surname,
          cf: user.cf,
          email: user.email,
        }
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    dataService.removeUser = function (user) {
      var deferred = $q.defer()
      $http({
        method: 'DELETE',
        url: configService.getURL() + '/api/role/' + currentDomain + '/user', 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
        },
        timeout: configService.httpTimout(),
        params: {
          email: user.email
        }
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }


    dataService.getInstitutesList = function () {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/institute/' + currentDomain, 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
        },
        timeout: configService.httpTimout()
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }
    
    dataService.getSchoolsList = function (instituteId) {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/school/' + currentDomain + '/' + instituteId, 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
        },
        timeout: configService.httpTimout()
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    dataService.getGamesList = function (instituteId, schoolId) {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/game/' + currentDomain + '/' + instituteId + '/' + schoolId, 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
        },
        timeout: configService.httpTimout()
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }
    
    return dataService
  })
