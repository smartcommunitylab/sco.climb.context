import axios from "axios";

export const userService = {
    login,
    logout,
    getProfile,


};

function login() {
    // console.log(process.env.VUE_APP_BASE_URL);
    //return Promise.resolve({token:'xxxx'});
}

function getProfile() {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_PROFILE_API
        // {
        //     headers: {
        //       'Authorization': `Bearer ${access_token.access_token}`
        //     }
        //   }
    //     ,{
    //     headers: { Authorization: `Bearer ${access_token}` }
    // }
    ).then(
        user => {
            if (user) {
                localStorage.setItem('user', JSON.stringify(user));
                return Promise.resolve(user);
            }
            return Promise.reject();
        }, err => {
            return Promise.reject(err);
        }

    )

    // dataService.getProfile = function () {
    //     var deferred = $q.defer()
    //     if (myProfile) deferred.resolve(myProfile);
    //     else {
    //       $http({
    //         method: 'GET',
    //         url: configService.getURL() + '/api/profile', 
    //         headers: {
    //           'Accept': 'application/json',
    //           'Authorization': 'Bearer ' + loginService.getUserToken()
    //         },
    //         timeout: configService.httpTimout(),
    //       }).then(function (response) {
    //         myProfile = response.data;
    //         deferred.resolve(response.data)
    //       }, function (reason) {
    //         console.log(reason)
    //         deferred.reject(reason)
    //       })
    //     }      
    //     return deferred.promise
    //   }

}

function logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    sessionStorage.clear();
    localStorage.clear();
}



