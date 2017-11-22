angular.module('MainDataService', []).factory('MainDataService', function ($http, $q, DataService) {
    var mainDataService = {};

    var domains, institutes, schools, games, itineraries;
    var currentDomain, currentInstitute, currentSchool, currentGame;



    mainDataService.getDomains = function () {
        var deferred = $q.defer();
        
        if (domains == undefined) {
            DataService.getProfile().then(function (profile) {
                domains = profile;
                DataService.setProfileToken(profile.token);
                deferred.resolve(domains);
            });
        } else {
            deferred.resolve(domains);
        }

        return deferred.promise;
    }

    mainDataService.getInstitutes = function (ownerID) {
        var deferred = $q.defer();
        
        if (institutes == undefined || ownerID != currentDomain) { //TODO: fix this!!
            DataService.getInstitutesList(ownerID).then(function (data) {
                institutes = data;
                currentDomain = ownerID;
                deferred.resolve(institutes);                
            });
        } else {
            deferred.resolve(institutes);
        }

        return deferred.promise;
    }

    mainDataService.getSchools = function (instituteID) {
        var deferred = $q.defer();
        
        if (schools == undefined || instituteID != currentInstitute) {
            DataService.getData('school', currentDomain, instituteID).then(function (data) {
                schools = data;
                currentInstitute = instituteID;
                deferred.resolve(schools);
            });
        } else {
            deferred.resolve(schools);
        }

        return deferred.promise;
    }

    mainDataService.getGames = function (schoolID) {
        var deferred = $q.defer();
        
        if (games == undefined || schoolID != currentSchool) {
            DataService.getData('game', currentDomain, currentInstitute, schoolID).then(function (data) {
                games = data;
                currentSchool = schoolID;
                deferred.resolve(games);
            });
        } else {
            deferred.resolve(games);
        }

        return deferred.promise;
    }

    mainDataService.getItineraries = function (gameID) {
        var deferred = $q.defer();
        
        if (itineraries == undefined || gameID != currentGame) {
            DataService.getData('itinerary', currentDomain, currentInstitute, currentSchool, gameID).then(function (data) {
                itineraries = data;
                currentGame = gameID;
                deferred.resolve(games);
            });
        } else {
            deferred.resolve(games);
        }

        return deferred.promise;
    }

    mainDataService.setSelectedDomain = function(domain) {
        currentDomain = domain;
    } /*
    mainDataService.setSelectedInstitute = function(institute) {
        currentInstitute = institute;
    } 
    mainDataService.setSelectedSchool = function(school) {
        currentSchool = school;
    }*/
    mainDataService.setSelectedGame = function(game) {
        currentGame = game;
    }


    return mainDataService;
});