import { gameService } from '../services';

const state = {
    myGames:null,
    currentGame:null
}

const actions = {

    getAllMyGames({ dispatch, commit }) {
        commit('getAllMyGamesRequest');
        gameService.getAllMyGames()
            .then(
                games => {
                    //todo reset old values
                    commit('getAllMyGamesSuccess', games);
                },
                error => {
                    commit('getAllMyGamesFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle informazioni.", { root: true });
                }
            );
    }


};

const mutations = {

    getAllMyGamesRequest(state) {
        state.myGames = { loading: true };
    },
    getAllMyGamesSuccess(state, myGames) {
        state.myGames = { items: myGames };
    },
    getAllMyGamesFailure(state, error) {
        state.myGames = { error };
    },
};

export const game = {
    namespaced: true,
    state,
    actions,
    mutations
};