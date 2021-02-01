angular.module('regisApp', [])
    .service('regisService', function ($http, $location,) {
        /* ... */
        var getUrl = window.location;
        var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        var timeout = 10000;
        var profileToken;
        var tmp = sessionStorage.getItem("profileToken");
        if (tmp) {
            profileToken = tmp;
        }
        //POST request
        this.registration = function (data) {
            console.log("Data", data);
            var url = baseUrl + "/public/api/registration";
            return $http.post(url, data, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
        }
    })
    .controller('RegistrationController', function ($scope, $location, regisService, $window) {
        /** */
        $scope.logout = function () {
            var getUrl = window.location.href;
            var logoutUrl = getUrl.substr(0, getUrl.indexOf('public'));
            logoutUrl += 'logout?target=' + logoutUrl+'backend/game/index.html';
            $window.location.href = logoutUrl ;
        }
        $scope.GetURLParameter = function (parameter) {
            var url;
            var search;
            var parsed;
            var count;
            var loop;
            var searchPhrase;
            url = window.location.href;
            search = url.indexOf("?");
            if (search < 0) {
                return "";
            }
            searchPhrase = parameter + "=";
            parsed = url.substr(search + 1).split("&");
            count = parsed.length;
            for (loop = 0; loop < count; loop++) {
                if (parsed[loop].substr(0, searchPhrase.length) == searchPhrase) {
                    return decodeURI(parsed[loop].substr(searchPhrase.length));
                }
            }
            return "";
        }
        $scope.normalProfile = {};
        $scope.email = $scope.GetURLParameter('email');
        $scope.userRegistration = function () {
            //call api "//public/api/registration" to registration
            var data = {
                "cf": $scope.normalProfile.cf,
                "name": $scope.normalProfile.name,
                "surname": $scope.normalProfile.surname,
                "email": $scope.normalProfile.email,
                "customData": { "plessoScolastico": $scope.normalProfile.plessoScolastico }
            }
            regisService.registration(data).then(function successCallback(response) {
                // this callback will be called asynchronously
                // when the response is available
                console.log("Succes response", response);
                $scope.successMsg = "Registrazione eseguita con successo";
                $scope.showNoti = true;
            }, function errorCallback(response) {
                // called asynchronously if an error occurs
                // or server returns response with an error status.
                console.log("Error response", response, "msg", response.data.errorMsg);
                $scope.errorMsg = response.data.errorMsg;
                $scope.showNoti = true;
            });
        }

        $scope.checkSend = function () {
            var result = ($scope.normalProfile.name && $scope.normalProfile.surname && $scope.normalProfile.email && $scope.normalProfile.cf && $scope.normalProfile.plessoScolastico) ? true : false;
            return result;
        }


    });