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

    dataService.removeAuth = function (authKey, user) {
      var deferred = $q.defer()
      $http({
        method: 'DELETE',
        url: configService.getURL() + '/api/role/' + currentDomain + '/auth/' + authKey, 
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
          pedibusGameId: user.gameId,
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

    dataService.getGame = function (gameId) {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/game/' + currentDomain + '/' + gameId, 
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
    
    dataService.uploadVolunteerFile = function(element) {
    	var deferred = $q.defer()
      var postUrl = configService.getURL() + '/admin/volunteer/csv/' + currentDomain + '/' 
    		+ element.instituteId + '/' + element.schoolId;
      $http.post(postUrl, element.formdata, 
      {
          timeout: configService.httpTimout(),
          headers: {
              'Authorization': 'Bearer ' + loginService.getUserToken(),
              'Content-Type': undefined 
          },
          transformRequest: angular.identity
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise    	
    }   
    
    dataService.getAuthText = function (user, authKey) {
    	var deferred = $q.defer();
    	var authText = "";
    	var ownerId = null;
    	var role = null;
    	var instituteId = null;
    	var schoolId = null;
    	var gameId = null;
    	var auths = user.roles[authKey];
    	if(auths && auths.length > 0) {
    		var auth = auths[0];
    		ownerId = auth.ownerId;
    		role = auth.role;
      	if(role == "school-owner") {
      		instituteId = auth.instituteId;
      		schoolId = auth.schoolId;
      	} else if(role == "teacher") {
      		instituteId = auth.instituteId;
      		schoolId = auth.schoolId;
      		gameId = auth.gameId;
      	} else if(role == "volunteer") {
      		instituteId = auth.instituteId;
      		schoolId = auth.schoolId;
      	}  else if(role == "game-editor") {
      		instituteId = auth.instituteId;
      		schoolId = auth.schoolId;
      	}    		
    	}
    	authText = ownerId + " - " + role;
    	if(instituteId) {
    		dataService.getInstitutesList().then(function (data) {
					var institutesList = data;
					institutesList.forEach(function(institute) {
						if(institute.objectId == instituteId) {
							authText = authText + " - " + institute.name;
						}
					});
					dataService.getSchoolsList(instituteId).then(function (data) {
						var schoolsList = data;
						schoolsList.forEach(function(school) {
							if(school.objectId == schoolId) {
								authText = authText + " - " + school.name;
							}
						});
						if(gameId) {
							dataService.getGame(gameId).then(function (data) {
								authText = authText + " - " + data.gameName;
								deferred.resolve(authText);
							});
						} else {
							deferred.resolve(authText);
						}
					});
    		});
    	} else if(gameId) {
				dataService.getGame(gameId).then(function (data) {
					authText = authText + " - " + game.name;
					deferred.resolve(authText);
				});
    	} else {
    		deferred.resolve(authText);
    	}
    	return deferred.promise
    }
    
    dataService.removeUserFromDomain = function (email) {
      var deferred = $q.defer()
      $http({
        method: 'DELETE',
        url: configService.getURL() + '/api/role/' + currentDomain + '/domain', 
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
    
    dataService.isDomainOwner = function(user) {
    	for (const [key, value] of Object.entries(user.roles)) {
    		const tokens = key.split('__');
    		var domain = tokens[0];
    		var role = tokens[1];
    		if((domain == currentDomain) && (role == 'owner')) {
    			return true;
    		}
    	}
    	return false;
    }
     
    return dataService
  })
