import axios from "axios";

export const userService = {
    login,
    logout,
    getAccount,
    getHome,
    getRole,
    update,
    getCompanies,
    changePassword,
    resetPasswordInit,
    resetPasswordFinish
};

function login(username, password) {
    console.log(process.env.VUE_APP_BASE_URL);
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_LOGIN_API, {
        "password": password,
        "rememberMe": true,
        "username": username
    }).then(
        res => {
            if (res && res.data && res.data.id_token) {
                localStorage.setItem('token', res.data.id_token);
                return Promise.resolve(res.data.id_token);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

function getAccount() {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API).then(
        user => {
            if (user && user.data) {
                localStorage.setItem('user', JSON.stringify(user.data));
                return Promise.resolve(user.data);
            }
            return Promise.reject();
        }, () => {
            return Promise.reject();
        }

    )
}
function logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    sessionStorage.clear();
    localStorage.clear();
}
function update(user) {
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API, user).then(
        () => {
            Promise.resolve();
        }, () => {
            Promise.reject();
        }
    )
}
function getRole(user) {
    var role='';
    for (var i = 0; i < user.roles.length; i++) {
        if (user.roles[i].role == 'ROLE_ADMIN') {
            role='ROLE_ADMIN';
        }
        if (user.roles[i].role == 'ROLE_COMPANY_ADMIN' && role!='ROLE_ADMIN') {
            role='ROLE_COMPANY_ADMIN';
        }
        if (user.roles[i].role == 'ROLE_MOBILITY_MANAGER'&& (role!='ROLE_ADMIN'|| role!='ROLE_COMPANY_ADMIN')) {
            role='ROLE_MOBILITY_MANAGER';
        }
    }
    return role;
}

function getCompanies(user) {
    var companies = [];
    for (var i = 0; i < user.roles.length; i++) {
        if (user.roles[i].role != 'ROLE_ADMIN') {
            companies.push(user.roles[i].companyId);
        }
    }
    return companies;
}
function getHome(role) {
    switch (role) {
        case 'ROLE_ADMIN':
            return {
                title: 'Gestione aziende',
                route: '/aziende'
            };
        case 'ROLE_COMPANY_ADMIN':
            return {
                title: 'Gestione campagne',
                route: '/gestionecampagne'
            };
        case 'ROLE_MOBILITY_MANAGER':
            return {
                title: 'Gestione dipendenti',
                route: '/dipendenti'
            };
        case 'ROLE_APP_USER':
            return {
                title: 'aziende',
                route: '/aziende'
            };
        default:
            return {
                title: 'aziende',
                route: '/aziende'
            };
    }
}

function changePassword(oldPassword, newPassword) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API + '/' + process.env.VUE_APP_CHANGE_PASSWORD_API, 
    { 
    "currentPassword": oldPassword, 
    "newPassword": newPassword
 }).then(
        res => {
            if (res && res.data ) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
function resetPasswordInit(username) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API + '/' + process.env.VUE_APP_INIT_RESET_PASSWORD_API,
        username,{
        headers: {
            'Content-Type': 'application/json',
            'Accept':'application/json',
        }}
    ).then(
        res => {
                return Promise.resolve(res.data);

        }, err => {
            return Promise.reject(err);
        }

    )
}

function resetPasswordFinish(key, newPassword) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API + '/' + process.env.VUE_APP_FINISH_RESET_PASSWORD_API, {
        "key": key,
        "newPassword": newPassword
      }).then(
        res => {
               return Promise.resolve(res.data);

        }, err => {
            return Promise.reject(err);
        }

    )
}



