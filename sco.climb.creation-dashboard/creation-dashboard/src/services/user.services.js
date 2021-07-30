// import axios from "axios";

export const userService = {
    login,
    logout,
    getAccount

};

function login() {
    // console.log(process.env.VUE_APP_BASE_URL);
    return Promise.resolve({token:'xxxx'})
    // todo 
    //return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_LOGIN_API, {
    //     "password": password,
    //     "rememberMe": true,
    //     "username": username
    // }).then(
    //     res => {
    //         if (res && res.data && res.data.id_token) {
    //             localStorage.setItem('token', res.data.id_token);
    //             return Promise.resolve(res.data.id_token);
    //         }
    //         else return Promise.reject(null);
    //     }, err => {
    //         return Promise.reject(err);
    //     }

    // )
}

function getAccount() {
    return Promise.resolve({user: {
        username:'Matteo'
    }})

    // return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API).then(
    //     user => {
    //         if (user && user.data) {
    //             localStorage.setItem('user', JSON.stringify(user.data));
    //             return Promise.resolve(user.data);
    //         }
    //         return Promise.reject();
    //     }, () => {
    //         return Promise.reject();
    //     }

    // )
}
function logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    sessionStorage.clear();
    localStorage.clear();
}



