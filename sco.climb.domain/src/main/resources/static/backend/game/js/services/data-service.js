angular.module('DataService', [])
    .factory('DataService', ['$q', '$http', '$rootScope', '$timeout', function ($q, $http, $rootScope, $timeout)
    {
        var baseUrl = 'http://192.168.42.60:9090/domain';
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
                $http.get('data/profile.json').success(function (data) {       // MODIFICATO PER IL TESTING IN LOCALE!
                    deferred.resolve(data);
                }).error(function (e) {
                    deferred.reject(e);
                });
                return deferred.promise;
            },
            getData: function(owner, institute, type, school, gameId, itineraryId)
            {
                var fetchUrl;
                if(type === 'school')
                    fetchUrl = baseUrl + "/api/" + type + "/" + owner + "/" + institute.objectId;
                else if(type === 'game' || type === 'route')
                    fetchUrl = baseUrl + "/api/" + type + "/" + owner + "/" + institute.objectId + "/" + school.objectId;
                else if(type === 'itinerary')
                    fetchUrl = baseUrl + "/api/game/" + owner + "/" + gameId + "/" + type;
                else if(type === 'legs')
                    fetchUrl = baseUrl + "/api/game/" + owner + "/" + gameId + "/itinerary/" + itineraryId + "/" + type;
                return $http.get(fetchUrl, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});

                // PER IL TESTING IN LOCALE CON IL LOCAL STORAGE
                /*var data = [];
                var index = 0;
                for(var i = 0; i < localStorage.length; i++)
                {
                    if(localStorage.key(i).includes(type))
                    {
                        data[index] = JSON.parse(localStorage.getItem(localStorage.key(i)));
                        index++;
                    }
                }
                return data;*/
            },
            editData: function(type, element)
            {
                var sendUrl;
                if(type === 'itinerary')
                    sendUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId;
                else if(type === 'legs')      // per le tappe passo alla funzione l'intero oggetto dell'itinerario, quindi l'URL sarà per forza diverso
                {
                    sendUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId + "/" + type;
                    return $http.put(sendUrl, element.legs, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
                }
                else
                    sendUrl = baseUrl + "/api/" + type + "/" + element.ownerId + "/" + element.objectId;

                return $http.put(sendUrl, element, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
            },
            getInstitutesList: function(owner)
            {
                var urlInstituteList = baseUrl + "/api/institute/" + owner;
                return $http.get(urlInstituteList, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
            },
            saveData: function(type, element)           // TODO: da adattare per tutti i tipi di dati
            {
                var postUrl;
                if(type === 'game' || type === 'route' || type === 'stop')
                    postUrl = baseUrl + "/api/" + type + "/" + element.ownerId;
                else if(type === 'school')
                    postUrl = baseUrl + "/api/" + type + "/" + element.ownerId + "/" + element.instituteId;
                else if(type === 'itinerary')
                    postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/" + type;
                else if(type === 'legs')      // per le tappe passo alla funzione l'intero oggetto dell'itinerario, quindi l'URL sarà per forza diverso
                {
                    postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId + "/" + type;
                    return $http.post(postUrl, element.legs, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
                }
                return $http.post(postUrl, element, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});

                //localStorage.setItem(type + '_' + localId, angular.toJson(data));     // PER IL TESTING IN LOCALE CON IL LOCAL STORAGE
            },
            removeData: function(type, element)
            {
                var deleteUrl;
                if(type !== 'itinerary')
                    deleteUrl = baseUrl + "/api/" + type + "/" + element.ownerId + "/" + element.objectId;
                else
                    deleteUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/" + type + "/" + element.objectId;
                return $http.delete(deleteUrl, {headers: {'Autorization': 'Bearer ' + $rootScope.profile.token}});
            },
            logout: logout
        };
    }
]);

/*.service('ShareMedia', function () {        // serve?
    var obj;

    return {
        getObj: function () {
            return obj;
        },
        setObj: function (value) {
            obj = value;
        }
    };
});*/