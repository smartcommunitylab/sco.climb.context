import { userService } from '../services';
import { router } from '../routes';

const user = JSON.parse(localStorage.getItem('user'));
const state = user
    ? { status: { loggedIn: true }, user }
    : { status: {}, user: null};

const actions = {

    login({commit,dispatch},access_token) {
        commit('loginSuccess', access_token);
        userService.getAccount().then(user => {
                                commit('userLogged', user);
                                dispatch('alert/error', "Utente entrato con successo", { root: true });
                                dispatch('navigation/changePage','/home', { root: true });
                                router.push('/home');
                            })
    },
    // login({ dispatch, commit }) {
    //     commit('loginRequest');
    //     userService.login()
    //         .then(
    //             token => {
    //                 //todo reset old values
    //                 commit('loginSuccess', token);
    //                 userService.getAccount().then(user => {
    //                     commit('userLogged', user);
    //                     dispatch('alert/error', "Utente entrato con successo", { root: true });
    //                     dispatch('navigation/changePage','/home', { root: true });
    //                     router.push('/home');
    //                 })
    //             },
    //             error => {
    //                 commit('loginFailure', error);
    //                 dispatch('alert/error', "Errore nell'accesso alla console.", { root: true });
    //             }
    //         );
    // },
    logout({ commit, dispatch }) {
        userService.logout();
        commit('logout');
        dispatch('navigation/logout',null,{ root: true });
        dispatch('alert/success', "Utente uscito con successo", { root: true });
        router.push('/login');
    },


};

const mutations = {

    loginRequest(state) {
        state.status = { loggingIn: true };
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
    loginFailure(state) {
        state.status = {};
        state.user = null;
      
    },
    logout(state) {
        state.status = {};
        state.user = null;
        
    }
};

export const account = {
    namespaced: true,
    state,
    actions,
    mutations
};