import { userService } from '../services';
import { router } from '../routes';

const user = JSON.parse(localStorage.getItem('user'));
const state = user
    ? { status: { loggedIn: true }, user, role: userService.getRole(user), home: userService.getHome(userService.getRole(user)) }
    : { status: {}, user: null, role: null, home: null };

function isCompanyAdmin(role){
    if (role==='ROLE_COMPANY_ADMIN')
    return true
    return false
}
const actions = {

    login({ dispatch, commit }, { username, password }) {
        commit('loginRequest', { username });

        userService.login(username, password)
            .then(
                token => {
                    //todo reset old values
                    commit('loginSuccess', token);
                    userService.getAccount().then(user => {
                        commit('userLogged', user);
                        var role = userService.getRole(user);
                        commit('roleUser', role);
                        var page = userService.getHome(role);
                        commit('homeUser', page);
                        var userCompanies = userService.getCompanies(user);
                        if (userCompanies.length > 0){
                            dispatch('company/getCompanyById', userCompanies[0], { root: true });
                            if (isCompanyAdmin(role))
                            {
                            dispatch('company/initCompanyAdmin', userCompanies[0], { root: true }); 
                            dispatch('campaign/getAll',userCompanies[0], { root: true });
                            dispatch('location/getAllLocations',userCompanies[0], { root: true });
                            }
                            dispatch('employee/getAll', userCompanies[0], { root: true });

                            
                        }
                        dispatch('navigation/changePage', page, { root: true });
                        router.push(page.route);
                    })
                },
                error => {
                    commit('loginFailure', error);
                    dispatch('alert/error', "Errore nell'accesso alla console.", { root: true });
                }
            );
    },
    logout({ commit, dispatch }) {
        userService.logout();
        commit('logout');
        dispatch('alert/success', "Utente uscito con successo", { root: true });
        dispatch('company/logout', null, { root: true });
        dispatch('campaign/logout', null, { root: true });
        dispatch('employee/logout', null, { root: true });
        dispatch('location/logout', null, { root: true });
        dispatch('stat/logout', null, { root: true });
        router.push('/login');
    },
    setDefaultCompany({ dispatch }, user) {
        var userCompanies = userService.getCompanies(user);
        if (userCompanies.length > 0)
            dispatch('company/getCompanyById', userCompanies[0], { root: true });
    },
    changePassword({ commit, dispatch },{oldPassword,newPassword}){
        commit('changePassword');
        userService.changePassword(oldPassword,newPassword).then(function(){
            commit('changePasswordSuccess');
            dispatch('alert/success', "Password cambiata con successo", { root: true });

        } ,function(error){
            commit('changePasswordFailure', error);
            dispatch('alert/error', "Errore, verificare la password e riprovare.", { root: true });
        })
    },
    resetPasswordInit({ commit, dispatch },username){
        commit('resetPasswordInit');
        userService.resetPasswordInit(username).then(function(){
            commit('resetPasswordInitSuccess');
            dispatch('alert/success', "Verificare la mail per confermare il cambio di password", { root: true });

        } ,function(error){
            commit('resetPasswordInitFailure', error);
            dispatch('alert/error', "Errore nella reimpostazione della password.", { root: true });
        })
    },
    resetPasswordFinish({ commit, dispatch },{key,newPassword}){
        commit('resetPasswordFinish');
        userService.resetPasswordFinish(key,newPassword).then(function(){
            commit('resetPasswordFinishSuccess');
            dispatch('alert/success', "Password cambiata con successo", { root: true });

        } ,function(error){
            commit('cresetPasswordFinishFailure', error);
            dispatch('alert/error',"Errore nella reimpostazione della password." , { root: true });
        })
    },

};

const mutations = {

    loginRequest(state, user) {
        state.status = { loggingIn: true };
        state.user = user;
    },
    loginSuccess(state, token) {
        console.log('logged and token')
        state.status = { loggedIn: true };
        state.token = token;
    },
    userLogged(state, user) {
        console.log('logged and user')
        state.status = { loggedIn: true };
        state.user = user;
    },
    roleUser(state, role) {
        console.log('role')
        state.role = role;
    },
    homeUser(state, home) {
        console.log('home')
        state.home = home;
    },
    loginFailure(state) {
        state.status = {};
        state.user = null;
        state.role = null;
        state.home = null;
    },
    logout(state) {
        state.status = {};
        state.user = null;
        state.role = null;
        state.home = null;
    },
    changePassword() {
        
    },
    changePasswordSuccess() {
       
    },
    changePasswordFailure() {

    },
    resetPasswordInit() {
        
    },
    resetPasswordInitSuccess() {
       
    },
    resetPasswordInitFailure() {

    },
    resetPasswordFinish() {
        
    },
    resetPasswordFinishSuccess() {
       
    },
    resetPasswordFinishFailure() {

    }
};

export const account = {
    namespaced: true,
    state,
    actions,
    mutations
};