angular.module('regisApp', [])
.service('regisService', function ($http) { 
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
    this.registration= function (data) {
        console.log("Data",data);
        var url = baseUrl + "/public/api/registration";
        return $http.post(url, data, { timeout: timeout, headers: { 'Authorization': 'Bearer ' + profileToken } });
    }
})
.controller('RegistrationController', function($scope, regisService) {
    /** */
    $scope.userRegistration = function(){
        //call api "//public/api/registration" to registration
        var data = {
            "cf":$scope.normalProfile.cf,
            "name":$scope.normalProfile.name,
            "surname":$scope.normalProfile.surname,
            "email":$scope.normalProfile.email,
            "plessoScolastico":$scope.normalProfile.plessoScolastico
        }
        regisService.registration(data).then(function successCallback(response) {
            // this callback will be called asynchronously
            // when the response is available
            console.log("Succes response",response);
            $scope.successMsg="Registrazione eseguita con successo";
            $scope.showNoti=true;
          }, function errorCallback(response) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            console.log("Error response",response, "msg",response.data.errorMsg);
            $scope.errorMsg=response.data.errorMsg;
            $scope.showNoti=true;
          });
    }
    
});