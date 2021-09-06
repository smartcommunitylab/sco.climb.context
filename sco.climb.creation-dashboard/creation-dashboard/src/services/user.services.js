// import axios from "axios";

export const userService = {
    login,
    logout,
    getAccount

};

function login() {
    // console.log(process.env.VUE_APP_BASE_URL);
    return Promise.resolve({token:'xxxx'});
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



