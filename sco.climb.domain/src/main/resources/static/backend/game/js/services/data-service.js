angular.module('DataService', []).factory('DataService', ['$q', '$http', '$rootScope', '$timeout', 
function ($q, $http, $rootScope, $timeout) {
		var getUrl = window.location;
		var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
    // var baseUrl = 'http://192.168.42.60:9090/domain';
    var logout = function () {
          var data = $q.defer();
          $http.post('logout', {}).success(function () {
              data.resolve();
          }, function () {
              data.resolve();
          });
          return data.promise;
      };
      return {
          getProfile: function () {
              var deferred = $q.defer();
              $http.get(baseUrl + "/console/data").success(function (data) {       
                  deferred.resolve(data);
              }).error(function (e) {
                  deferred.reject(e);
              });
              return deferred.promise;
          },
          getData: function(type, ownerId, instituteId, schoolId, routeId, gameId, itineraryId)
          {
              var fetchUrl;
              if(type === 'school') {
                  fetchUrl = baseUrl + "/api/school/" + ownerId + "/" + instituteId;
              } else if(type === 'game') {
                  fetchUrl = baseUrl + "/api/game/" + ownerId + "/" + instituteId + "/" + schoolId;
              } else if(type === 'route') {
              	fetchUrl = baseUrl + "/api/route/" + ownerId + "/" + instituteId + "/" + schoolId;
              } else if(type == 'stops') {
              	fetchUrl = baseUrl + "/api/stop/" + ownerId + "/" + routeId;
              } else if(type === 'itinerary') {
                  fetchUrl = baseUrl + "/api/game/" + ownerId + "/" + gameId + "/itinerary";
              } else if(type === 'legs') {
                  fetchUrl = baseUrl + "/api/game/" + ownerId + "/" + gameId + "/itinerary/" + itineraryId + "/legs";
              } else if(type === 'children') {
              	fetchUrl = baseUrl + "/api/child/" + ownerId + "/" + instituteId + "/" + schoolId;
              } else if(type === 'classes') {
              	fetchUrl = baseUrl + "/api/game/" + ownerId + "/" + instituteId + "/" + schoolId + "/classes";
              }
              return $http.get(fetchUrl, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});

              // PER IL TESTING IN LOCALE CON IL LOCAL STORAGE
              /*
							 * var data = []; var index = 0; for(var i = 0; i <
							 * localStorage.length; i++) {
							 * if(localStorage.key(i).includes(type)) { data[index] =
							 * JSON.parse(localStorage.getItem(localStorage.key(i)));
							 * index++; } } return data;
							 */
          },
          editData: function(type, element)
          {
              var sendUrl;
              if(type === 'itinerary') {
                sendUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId;
              } else if(type === 'legs') {
              	sendUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId + "/" + type;
                return $http.put(sendUrl, element.legs, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
              } else if(type == 'school') {
              	sendUrl = baseUrl + "/api/school/" + element.ownerId + "/" + element.objectId;
              } else if(type == 'stops') {
              	sendUrl = baseUrl + "/api/stop/" + element.ownerId + "/" + element.routeId;              	
             	 	return $http.post(sendUrl, element.stops, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
              } else if(type == 'route') {
              	sendUrl = baseUrl + "/api/route/" + element.ownerId + "/" + element.objectId;
              } else if(type == 'child') {
              	sendUrl = baseUrl + "/api/child/" + element.ownerId + "/" + element.objectId;
              }
              return $http.put(sendUrl, element, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
          },
          getInstitutesList: function(owner)
          {
              var urlInstituteList = baseUrl + "/api/institute/" + owner;
              return $http.get(urlInstituteList, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
          },
          saveData: function(type, element)           
          {
              var postUrl;
              if(type === 'game' || type === 'stop') {
              	postUrl = baseUrl + "/api/" + type + "/" + element.ownerId;
              } else if(type === 'school') {
                postUrl = baseUrl + "/api/" + type + "/" + element.ownerId + "/" + element.instituteId;
              } else if(type === 'itinerary') {
                postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary";
              } else if(type === 'legs') {
                postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId + "/" + type;
                return $http.post(postUrl, element.legs, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
              } else if(type == 'stops') {
              	postUrl = baseUrl + "/api/stop/" + element.ownerId + "/" + element.routeId;              	
              	return $http.post(postUrl, element.stops, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
              } else if(type == 'route') {
              	postUrl = baseUrl + "/api/route/" + element.ownerId;
              } else if(type == 'child') {
              	postUrl = baseUrl + "/api/child/" + element.ownerId;
              }
              return $http.post(postUrl, element, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});

              // localStorage.setItem(type + '_' + localId,
							// angular.toJson(data)); // PER IL TESTING IN LOCALE CON IL
							// LOCAL STORAGE
          },
          removeData: function(type, element)
          {
              var deleteUrl;
              if(type == 'itinerary') {
              	deleteUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId;
              } else if(type == 'child') {
              	deleteUrl = baseUrl + "/api/child/" + element.ownerId + "/" + element.objectId;
              } else {
              	deleteUrl = baseUrl + "/api/" + type + "/" + element.ownerId + "/" + element.objectId;
              }
              return $http.delete(deleteUrl, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
          },
          logout: logout
      };
  }
]);

/*
 * .service('ShareMedia', function () { // serve? var obj;
 * 
 * return { getObj: function () { return obj; }, setObj: function (value) { obj =
 * value; } }; });
 */