angular.module('DataService', []).factory('DataService', ['$q', '$http', '$rootScope', '$timeout',
function ($q, $http, $rootScope, $timeout) {
    var getUrl = window.location;
    var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
    var timeout = 10000;

    var googleApiKey = 'AIzaSyCgNyKWM_SBXNe7dKw1QdywllZpbQ0Jioo';
    var googleImagesApiKey = '006150621137928308267:ye0t8zulymg';
        
    var profileToken;
    var tmp = sessionStorage.getItem("profileToken");
    if (tmp) {
        profileToken = tmp;
    }
    var logout = function () {
        new Oidc.UserManager(auth_conf).signoutRedirectCallback();
    };
    return {
        getBaseUrl: function () {
            return baseUrl;
        },
        getProfile: function () {
            var deferred = $q.defer();
            $http.get(baseUrl + "/api/console/data",{timeout: timeout}).success(function (data) {       
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
            } else if(type === 'volunteers') {
                fetchUrl = baseUrl + "/api/volunteer/" + ownerId + "/" + instituteId + "/" + schoolId;
            } else if(type == 'gamereports') {
                    fetchUrl = baseUrl + "/api/game/report";
            } else if(type == 'players') {
                fetchUrl = baseUrl + "/api/game/" + ownerId + "/" + instituteId + "/" + schoolId + "/player";
            }
            return $http.get(fetchUrl, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        getGameConfData: function(type, data) {
            var fetchUrl;
            if (type === 'gameconfigtemplate') {
                fetchUrl = baseUrl + "/api/game/conf/template";
            } else if (type === 'gameconfigbyid') {
                fetchUrl = baseUrl + "/api/game/conf/" + data.ownerId + "/" + data.confId;
            } else if (type === 'gameconfigsummary') {
                fetchUrl = baseUrl + "/api/game/conf/" + data.ownerId + "/" + data.instituteId + "/" + data.schoolId;
            }
            return $http.get(fetchUrl, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        editData: function(type, element)
        {
            var sendUrl;
            if(type === 'game') {
                sendUrl = baseUrl + "/api/" + type + "/" + element.ownerId + "/" + element.objectId; 
            } else if(type === 'itinerary') {
                sendUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId;
            } else if(type === 'legs') {
                sendUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId + "/legs";
                return $http.put(sendUrl, element.legs, {headers: {'Authorization': 'Bearer ' + profileToken}});
            } else if (type == 'leg_content') {
                postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.itineraryId + "/leg/" + element.legId + "/links";
                return $http.put(postUrl, element.externalUrls, {headers: {'Authorization': 'Bearer ' + profileToken}});
            } else if (type == 'leg') {
                postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.itineraryId + "/leg/" + element.objectId;
                return $http.put(postUrl, element, {headers: {'Authorization': 'Bearer ' + profileToken}});
            } else if(type === 'institute') {
                sendUrl = baseUrl + "/api/institute/" + element.ownerId + "/" + element.objectId;
            } else if(type == 'school') {
                sendUrl = baseUrl + "/api/school/" + element.ownerId + "/" + element.objectId;
            } else if(type == 'stops') {
                sendUrl = baseUrl + "/api/stop/" + element.ownerId + "/" + element.routeId;              	
                return $http.post(sendUrl, element.stops, {headers: {'Authorization': 'Bearer ' + profileToken}});
            } else if(type == 'route') {
                sendUrl = baseUrl + "/api/route/" + element.ownerId + "/" + element.objectId;
            } else if(type == 'child') {
                sendUrl = baseUrl + "/api/child/" + element.ownerId + "/" + element.objectId;
            } else if(type == 'volunteer') {
                sendUrl = baseUrl + "/api/volunteer/" + element.ownerId + "/" + element.objectId;
            } else if (type == 'gameconfigdetail') {
                sendUrl = baseUrl + "/api/game/conf/" + element.ownerId + "/" + element.pedibusGameId;
            } else if (type == 'player') {
                sendUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.instituteId + "/" 
                + element.schoolId + "/player";
            }
            return $http.put(sendUrl, element, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        getInstitutesList: function(owner)
        {
            var urlInstituteList = baseUrl + "/api/institute/" + owner;
            return $http.get(urlInstituteList, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        saveData: function(type, element)           
        {
            var postUrl;
            if(type === 'game' || type === 'stop') {
                postUrl = baseUrl + "/api/" + type + "/" + element.ownerId;
            } else if(type === 'institute') {
                postUrl = baseUrl + "/api/" + type + "/" + element.ownerId;
            } else if(type === 'school') {
                postUrl = baseUrl + "/api/" + type + "/" + element.ownerId + "/" + element.instituteId;
            } else if(type === 'itinerary') {
                postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary";
            } else if(type === 'legs') {
                postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId + "/legs";
                return $http.post(postUrl, element.legs, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
            } else if (type == 'leg') {
                postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.itineraryId + "/leg";
                return $http.post(postUrl, element, {headers: {'Authorization': 'Bearer ' + profileToken}});
            }	else if(type == 'stops') {
                postUrl = baseUrl + "/api/stop/" + element.ownerId + "/" + element.routeId;              	
                return $http.post(postUrl, element.stops, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
            } else if(type == 'route') {
                postUrl = baseUrl + "/api/route/" + element.ownerId;
            } else if(type == 'child') {
                postUrl = baseUrl + "/api/child/" + element.ownerId;
            } else if(type == 'volunteer') {
                postUrl = baseUrl + "/api/volunteer/" + element.ownerId;
            } else if (type == 'gameconfigdetail') {
                postUrl = baseUrl + "/api/game/conf/" + element.ownerId + "/" + element.pedibusGameId;
            } else if (type == 'player') {
                postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.instituteId + "/" 
                + element.schoolId + "/player";
            }
            return $http.post(postUrl, element, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        removeData: function(type, element)
        {
            var deleteUrl;
            if(type == 'itinerary') {
                deleteUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId;
            } else if(type == 'child') {
                deleteUrl = baseUrl + "/api/child/" + element.ownerId + "/" + element.objectId;
            } else if(type == 'volunteer') {
                deleteUrl = baseUrl + "/api/volunteer/" + element.ownerId + "/" + element.objectId;
            } else if(type == 'institute') {
                deleteUrl = baseUrl + "/api/institute/" + element.ownerId + "/" + element.objectId;
            } else if(type == 'player') {
                    deleteUrl = baseUrl + "/api/game/" + element.ownerId + "/player/" + element.objectId;
            } else {
                deleteUrl = baseUrl + "/api/" + type + "/" + element.ownerId + "/" + element.objectId;
            }
            return $http.delete(deleteUrl, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        uploadFile: function(element) 
        {
            var postUrl = baseUrl + '/admin/import/' + element.ownerId + '/' + element.instituteId + '/' + element.schoolId + '?players=' + element.players;
            return $http.post(postUrl, element.formdata, 
                {
                    timeout: 30000,
                    headers: {
                        'Authorization': 'Bearer ' + profileToken,
                        'Content-Type': undefined 
                    },
                    transformRequest: angular.identity
                });
        },
        uploadFileContent: function(element) 
        {
            var postUrl = baseUrl + '/api/game/' + element.ownerId + '/' + element.pedibusGameId + '/itinerary/'
            + element.itineraryId + '/leg/' + element.legId + '/file';
            return $http.post(postUrl, element.formdata, 
                {
                    timeout: 60000,
                    headers: {
                        'Authorization': 'Bearer ' + profileToken,
                        'Content-Type': undefined 
                    },
                    transformRequest: angular.identity
                });
        },
        initGameCall: function (ownerId, pedibusGameId) {
            return $http.get(baseUrl + "/api/game/" + ownerId + "/" + pedibusGameId + "/deploy", 
                {headers: {timeout: timeout, 'Authorization': 'Bearer ' + profileToken}}
            );              
        },
        resetGame: function(ownerId, pedibusGameId) {
            return $http.get(baseUrl + "/api/game/" + ownerId + "/" + pedibusGameId + "/reset", 
                {headers: {timeout: timeout, 'Authorization': 'Bearer ' + profileToken}}
            );
        },
        cloneGame: function(ownerId, instituteId, schoolId, pedibusGameId, itineraryId) {
            return $http.get(baseUrl + "/api/game/" + ownerId + "/" + instituteId 
                    + "/" + schoolId + "/" + pedibusGameId + "/clone/" + itineraryId, 
                {headers: {timeout: timeout, 'Authorization': 'Bearer ' + profileToken}}
            );
        },
        logout: logout,
        setProfileToken: function(token) {
            profileToken = token;
            sessionStorage.setItem("profileToken", profileToken);
        },
        searchOnWikipedia: function (query, start) {
            var deferred = $q.defer();
            var config = {
                params: {
                    format: "json",
                    action: "query",
                    prop: "extracts",
                    exchars: "140",
                    exintro: "",
                    explaintext: "",
                    rawcontinue: "",
                    generator: "search",
                    callback: "JSON_CALLBACK",
                    gsrsearch: query,
                    gsroffset: start
                }
            };
            $http.jsonp('https://it.wikipedia.org/w/api.php',config).then(function(data){
                deferred.resolve(data);
            },err => {
                deferred.reject(err);
            })
            return deferred.promise;
        },
        searchOnYoutube: function (query, pageToken) {
            var deferred = $q.defer();
            $http.get('https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&key=' + googleApiKey + (pageToken ? ("&pageToken="+pageToken) : '') + '&q=' + query).then(function(data){
                deferred.resolve(data);
            },err => {
                deferred.reject(err);
            })
            return deferred.promise;
        },
        searchOnImages: function (query, start) {
            var deferred = $q.defer();
            $http.get('https://www.googleapis.com/customsearch/v1?key=' + googleApiKey + '&cx=' + googleImagesApiKey + (start ? ("&start="+start) : '') + '&num=9&searchType=image&alt=json&q=' + query).then(function(data){
                deferred.resolve(data);
            },err => {
                deferred.reject(err);
            })
            return deferred.promise;
        },
        searchOnContentRepository: function (query, position, distance, schoolId, type, subjects, schoolYears) {
            var deferred = $q.defer();
            // var config = {
            //     params: {
            //         text: query
            //     },
            //     timeout: timeout
            // }
            // if (position) {
            //     config.params.lat = position[0];
            //     config.params.lng = position[1];
            // }
            // if (distance) {
            //     config.params.distance = distance;
            // }
            // if (schoolId) {
            //     config.params.schoolId = schoolId;
            // }
            // if (type) {
            //     config.params.type = type;
            // }
            var config = {
                text: query,
                distance: distance,
                types: type,
                subjects: subjects,
                schoolYears: schoolYears
            }
            if (position) {
                config.lat = position[0];
                config.lng = position[1];
            }
            console.log("config::",config);
            $http({
                url: baseUrl + '/api/game/multimedia', 
                method: "GET",
                params: config
             }).then(function(data){
                deferred.resolve(data);
            });
            // $http.get(baseUrl + '/api/game/multimedia', config).then(function(data){
            //     deferred.resolve(data);
            // })
            return deferred.promise;
        }, 
        updateTerms: function () {
        var url = baseUrl + "/console/user/accept-terms";
        return $http.post(url, new Date(), { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
        },           
        registration: function (data) {
        var url = baseUrl + "/public/api/registration";
        return $http.post(url, data, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
        },
        updateTemplateToGame: function (template) {
            var url = baseUrl + '/api/game/conf/' + template.ownerId + '/' + template.pedibusGameId + '/template/' + template.objectId;
            return $http.put(url, null, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
        },
        updateConfParams: function (game) {
        var url = baseUrl + '/api/game/conf/' + game.ownerId + '/' + game.objectId + '/params';
        return $http.put(url, game.params, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
        },
        getNrOfStudents: function (ownerId, instituteId, schoolId, classes) {
            var filtered = classes.filter(function (el) {
                return el != null;
              });
            var fetchUrl = baseUrl + "/api/game/" + ownerId + "/" + instituteId + "/" + schoolId + "/students?classes=" + filtered;
            return $http.get(fetchUrl, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        getGameById: function (ownerId, id) {
        var fetchUrl = baseUrl + '/api/game/' + ownerId + "/" + id;
        return $http.get(fetchUrl, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
        },
        getModalityMap: function() {
        var fetchUrl = baseUrl + '/api/game/modalitymap';
        return $http.get(fetchUrl, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
        },
        getMultimediaContentTags: function(ownerId, pedibusGameId) {
            var fetchUrl = baseUrl + '/api/game/' + ownerId + "/" + pedibusGameId + "/mctags";
            return $http.get(fetchUrl, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
        },
        getMultimediaContent: function(ownerId, pedibusGameId, itineraryId, legId) {
        var fetchUrl = baseUrl + '/api/game/' + ownerId + "/" + pedibusGameId + "/itinerary/" 
        + itineraryId + "/leg/" + legId + "/content";
        return $http.get(fetchUrl, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
        },
        setMultimediaContent: function(element){
            var postUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.itineraryId + "/leg/" + element.legId + "/content";
            return $http.post(postUrl, element.externalUrls[element.externalUrls.length-1], {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        updateMultimediaContent: function(element){
            console.log("element::",element)
        var url= baseUrl + "/api/game/"+ element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.itineraryId + "/leg/" + element.legId + "/content/" + element.objectId;
        return $http.put(url, element.content, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        updateMultimediaContentPosition: function(element){
            var url= baseUrl + "/api/game/"+ element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.itineraryId + "/leg/" + element.legId + "/content/positions";
            return $http.put(url, element.contents, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        deleteMultimediaContent: function(element){
        var deleteUrl= baseUrl + "/api/game/"+ element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.itineraryId + "/leg/" + element.legId + "/content/" + element.objectId;
        return $http.delete(deleteUrl, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        // PUT /api/game/{ownerId}/{pedibusGameId}/mobility
        // modifica i dati di abitudini di mobilità
        updateParams: function(ownerId,objectId, params){
            var url= baseUrl + "/api/game/"+ ownerId + "/" + objectId + "/mobility";
            return $http.put(url, params, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        }, 
        // PUT /api/game/{ownerId}/{pedibusGameId}/tuning
        // modifica i dati di calibrazione
        updateCalibrazioni: function(element){
            var url= baseUrl + "/api/game/"+ element.ownerId + "/" + element.objectId + "/tuning";
            return $http.put(url, element, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        //todo GET /api/game/{ownerId}/{pedibusGameId}/students
        // da lista studenti in base a lista di classi
        getStudentsByClasses: function (ownerId, pedibusGameId,classes) {
            var filtered = classes.filter(function (el) {
                return el != null;
              });
            var fetchUrl = baseUrl + "/api/game/" + ownerId + "/" + pedibusGameId + "/students?classes=" + filtered;
            return $http.get(fetchUrl, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        //done DELETE /api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}
        // cancella tappa itinerario
        deleteStopFromItinerary: function(element,leg){
            var deleteUrl= baseUrl + "/api/game/"+ element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.itineraryId + "/leg/" + leg.objectId;
            return $http.delete(deleteUrl, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        //done PUT /api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/legs/position
        // modifica ordine tappe
        updateStopsPosition: function(element){
            var url= baseUrl + "/api/game/"+ element.ownerId + "/" + element.pedibusGameId + "/itinerary/" + element.objectId + "/legs/positions";
            return $http.put(url, element.legs, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        //GET /api/game/{ownerId}/{pedibusGameId}/player
        // lista giocatori per gioco
        getStudentsByGame: function (element,classes) {
            var filtered = classes.filter(function (el) {
                return el != null;
              });
            var fetchUrl = baseUrl + "/api/game/" + element.ownerId + "/" + element.objectId + "/player?classes=" + filtered;
            return $http.get(fetchUrl, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
        },
        //post  /api/game/{ownerId}/{pedibusGameId}/players"
        //modifica lista giocatori del gioco


        updatePlayers: function (element,data) {
            var url = baseUrl + "/api/game/" + element.ownerId + "/" + element.objectId + "/players";
            return $http.post(url, data, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
            },
        //todo POST /api/game/{ownerId}/{pedibusGameId}/player
        // crea giocatore
        createPlayer: function (ownerId, pedibusGameId,data) {
            var url = baseUrl + "/api/game/" + ownerId + "/" + pedibusGameId + "/player";
            return $http.post(url, data, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
            },
            //todo PUT /api/game/{ownerId}/player/{id}
            // modifica giocatore
            updateGamer: function(element){
                console.log("element::",element)
            var url= baseUrl + "/api/game/"+ element.ownerId + "/player/" + element.id ;
            return $http.put(url, element.content, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
            },
             //todo DELETE /api/game/{ownerId}/player/{id}
            // cancella giocatore
            deleteGamer: function(element){
                console.log("element::",element)
            var url= baseUrl + "/api/game/"+ element.ownerId + "/player/" + element.objectId ;
            return $http.delete(url, {timeout: timeout, headers: {'Authorization': 'Bearer ' + profileToken}});
            }
                };
}
]);