/* global angular, sessionStorage */
angular.module('climbGame.services.cache', [])
  .factory('CacheSrv', function () {
    var cacheService = {}

    cacheService.getLastCheck = function (page) {
      var timestamp = sessionStorage.getItem(page + '_page_last_check')
      if (timestamp) {
        return timestamp
      }
      return new Date(2017, 1, 1, 0, 0, 0, 0).getTime()
    }

    cacheService.updateLastCheck = function (page) {
      var now = new Date().getTime()
      sessionStorage.setItem(page + '_page_last_check', now)
      return now
    }

    cacheService.resetLastCheck = function (page) {
      sessionStorage.removeItem(page + '_page_last_check')
    }
    
    cacheService.setGameFinishedNotified = function (ownerId, gameId, classRoom, booleanValue) {
    	var key = 'GameFinishedNotified_' + ownerId + '_' + gameId + '_' + classRoom;
    	sessionStorage.setItem(key, booleanValue);
    }
    
    cacheService.isGameFinishedNotified = function (ownerId, gameId, classRoom) {
    	var key = 'GameFinishedNotified_' + ownerId + '_' + gameId + '_' + classRoom;
    	var result = sessionStorage.getItem(key);
    	if(result) {
    		return JSON.parse(result);
    	} else {
    		return false;
    	}
    } 
    
    cacheService.resetGameFinishedNotified = function (ownerId, gameId, classRoom) {
    	var key = 'GameFinishedNotified_' + ownerId + '_' + gameId + '_' + classRoom;
    	sessionStorage.removeItem(key);
    }
    
    return cacheService
  })
