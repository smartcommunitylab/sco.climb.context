angular.module('climbGame.services.login', [])
	.factory('loginService', function($http, $q, $rootScope, configService) {
		var loginService = {};
		var OWNERID = "USERNAME";
		var INSTITUTEID = "INSTITUTEID";
		var SCHOOLID = "SCHOOLID";
		var GAMEID = "GAMEID";
		var ITINERARYID = "ITINERARYID";
		var USERTOKEN = "USERTOKEN";
		var CLASS = "CLASS";
		var CLASSES = "CLASSES";
		Oidc.Log.logger = console;
Oidc.Log.level = Log.DEBUG;
		loginService.login = function(user) {
			var deferred = $q.defer();
			$http({
				method: 'POST',
				url: configService.getTokenURL(),
				params: {
					username: user.username,
					password: user.password
				}

			}).
				success(function(data, status, headers, config) {
					console.log(data);
					//store owner id and token
					loginService.setOwnerId(data.name);
					loginService.setUserToken(data.token);
					$http({
						method: 'GET',
						url: configService.getGameId() + data.name,
						headers: {
							'Accept': 'application/json',
							'x-access-token': data.token
						}

					}).
						success(function(data, status, headers, config) {
							console.log(data);
							// store gameid
							loginService.setGameId(data[0].gameId);
							//show class List
							loginService.setAllClasses(data[0].classRooms);
							deferred.resolve(data);

						}).
						error(function(data, status, headers, config) {
							//console.log(data);
							loginService.logout();
							deferred.reject(2);
						});
				}).
				error(function(data, status, headers, config) {
					if (status == 403) {
						deferred.reject(1);
					}
					deferred.reject(2);
					//console.log(data);
				});
			return deferred.promise;
		}
		loginService.getOwnerId = function() {
			if (loginService.ownId) {
				return loginService.ownId;
			} else {
				loginService.ownId = sessionStorage.getItem(OWNERID);
				return loginService.ownId;
			}
		}
		loginService.getInstituteId = function() {
			if (loginService.instituteId) {
				return loginService.instituteId;
			} else {
				loginService.instituteId = sessionStorage.getItem(INSTITUTEID);
				return loginService.instituteId;
			}
		}
		loginService.getSchoolId = function() {
			if (loginService.schoolId) {
				return loginService.schoolId;
			} else {
				loginService.schoolId = sessionStorage.getItem(SCHOOLID);
				return loginService.schoolId;
			}
		}
		loginService.getGameId = function() {
			if (loginService.gameId) {
				return loginService.gameId;
			} else {
				loginService.gameId = sessionStorage.getItem(GAMEID);
				return loginService.gameId;
			}
		}
		loginService.getItineraryId = function() {
			if (loginService.itineraryId) {
				return loginService.itineraryId;
			} else {
				loginService.itineraryId = sessionStorage.getItem(ITINERARYID);
				return loginService.itineraryId;
			}
		}
		loginService.getUserToken = function() {
			if (loginService.userToken) {
				return loginService.userToken;
			} else {
				loginService.userToken = sessionStorage.getItem(USERTOKEN);
				return loginService.userToken;
			}
		}
		loginService.getAllOwners = function() {
			return loginService.ownerIds;
		}
		loginService.getAllInstitutes = function() {
			return loginService.instituteIds;
		}
		loginService.getAllSchools = function() {
			return loginService.schoolIds;
		}
		loginService.getAllItineraies = function() {
			return loginService.itineraryIds;
		}
		loginService.getAllClasses = function() {
			if (loginService.classes) {
				return loginService.classes;
			} else {
				loginService.classes = JSON.parse(sessionStorage.getItem(CLASSES));
				return loginService.classes;
			}
		}
		loginService.getClassRoom = function() {
			if (loginService.classRoom) {
				return loginService.classRoom;
			} else {
				loginService.classRoom = sessionStorage.getItem(CLASS);
				return loginService.classRoom;
			}
		}
		loginService.getSingleInstitute = function() {
			return sessionStorage.getItem('singleInstitute') == 'true'; //terrible workaround, boolean saved as text
		}
		loginService.getSingleSchool = function() {
			return sessionStorage.getItem('singleSchool') == 'true';
		}
		loginService.getSingleClass = function() {
			return sessionStorage.getItem('singleClass') == 'true';
		}
		loginService.getSingleItinerary = function() {
			return sessionStorage.getItem('singleItinerary') == 'true';
		}
		loginService.getSingleGame = function() {
			return sessionStorage.getItem('singleGame') == 'true';
		}
		loginService.setOwnerId = function(id) {
			sessionStorage.setItem(OWNERID, id);
			loginService.ownId = id;
		}
		loginService.setInstituteId = function(id) {
			sessionStorage.setItem(INSTITUTEID, id);
			loginService.instituteId = id;
		}
		loginService.setSchoolId = function(id) {
			sessionStorage.setItem(SCHOOLID, id);
			loginService.schoolId = id;
		}
		loginService.setGameId = function(id) {
			sessionStorage.setItem(GAMEID, id);
			loginService.gameId = id;
		}
		loginService.setItineraryId = function(id) {
			sessionStorage.setItem(ITINERARYID, id);
			loginService.itineraryId = id;
		}
		loginService.setUserToken = function(token) {
			sessionStorage.setItem(USERTOKEN, token);
			loginService.userToken = token;
		}
		loginService.setAllOwners = function(ownerIds) {
			loginService.ownerIds = ownerIds;
		}
		loginService.setAllInstitutes = function(instituteIds) {
			loginService.instituteIds = instituteIds;
		}
		loginService.setAllSchools = function(schoolIds) {
			loginService.schoolIds = schoolIds;
		}
		loginService.setAllItineraies = function(itineraryIds) {
			loginService.itineraryIds = itineraryIds;
		}
		loginService.setAllClasses = function(classes) {
			sessionStorage.setItem(CLASSES, JSON.stringify(classes));
			loginService.classes = classes;
		}
		loginService.setSingleInstitute = function(single) {
			sessionStorage.setItem('singleInstitute', single);
		}
		loginService.setSingleSchool = function(single) {
			sessionStorage.setItem('singleSchool', single);
		}
		loginService.setSingleClass = function(single) {
			sessionStorage.setItem('singleClass', single);
		}
		loginService.setSingleItinerary = function(single) {
			sessionStorage.setItem('singleItinerary', single);
		}
		loginService.setSingleGame = function(single) {
			sessionStorage.setItem('singleGame', single);
		}
		loginService.setClassRoom = function(classRoom) {
			sessionStorage.setItem(CLASS, classRoom);
			loginService.classRoom = classRoom;
		}
		loginService.setTitle = function(title) {
			$rootScope.title = title;
		}
		loginService.removeClass = function() {
			sessionStorage.removeItem(CLASS);
			loginService.classRoom = null;
		}
		loginService.removeItinerary = function() {
			sessionStorage.removeItem(ITINERARYID);
			loginService.itineraryId = null;
		}
		loginService.removeGame = function() {
			sessionStorage.removeItem(GAMEID);
			loginService.gameId = null;
		}
		loginService.removeSchool = function() {
			sessionStorage.removeItem(SCHOOLID);
			loginService.schoolId = null;
		}
		loginService.removeInstitute = function() {
			sessionStorage.removeItem(INSTITUTEID);
			loginService.instituteId = null;
		}
		loginService.removeClasses = function() {
			sessionStorage.removeItem(CLASSES);
			loginService.classes = null;
		}
		loginService.logout = async function() {
			loginService.ownId = null;
			loginService.userToken = null;
			loginService.instituteId = null;
			loginService.schoolId = null;
			loginService.gameId = null;
			loginService.itineraryId = null;
			loginService.classRoom = null;
			loginService.classes = null;
			sessionStorage.removeItem(OWNERID);
			sessionStorage.removeItem(INSTITUTEID);
			sessionStorage.removeItem(SCHOOLID);
			sessionStorage.removeItem(GAMEID);
			sessionStorage.removeItem(ITINERARYID);
			sessionStorage.removeItem(USERTOKEN);
			sessionStorage.removeItem(CLASS);
			sessionStorage.removeItem(CLASSES);
			var mgr =  await new Oidc.UserManager(auth_conf)
			mgr.signoutRedirect();
		}
		loginService.getParams = function(state) {
			switch (state) {
				case 'instituteSelection':
					return { owner: loginService.getOwnerId() };

				case 'schoolSelection':
					return { owner: loginService.getOwnerId(), institute: loginService.getInstituteId() };
				case 'gameSelection':
					return { owner: loginService.getOwnerId(), institute: loginService.getInstituteId() , school: loginService.getSchoolId() };

				case 'itinerarySelection':
					return { owner: loginService.getOwnerId(), institute: loginService.getInstituteId() , school: loginService.getSchoolId(), game: loginService.getGameId() };
				case 'classSelection':
					return { owner: loginService.getOwnerId(), institute: loginService.getInstituteId(), school: loginService.getSchoolId(), game: loginService.getGameId(), itinerary: loginService.getItineraryId() };

				default:
				// code block
				return { owner: loginService.getOwnerId(), institute: loginService.getInstituteId(), school: loginService.getSchoolId(), game: loginService.getGameId(), itinerary: loginService.getItineraryId(), class: loginService.getClassRoom()};
			}
		}
		return loginService;
	});
