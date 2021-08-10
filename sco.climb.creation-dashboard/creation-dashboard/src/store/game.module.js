import { gameService } from '../services';

const state = {
    myGames: null,
    currentGame: null
}

const actions = {

    getAllMyGames({ dispatch, commit }) {
        commit('getAllMyGamesRequest');
        gameService.getAllMyGames()
            .then(
                games => {
                    //todo reset old values
                    commit('getAllMyGamesSuccess', games);
                    dispatch('alert/success', "Recuperati i tuoi giochi.", { root: true });

                },
                error => {
                    commit('getAllMyGamesFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle informazioni.", { root: true });
                }
            );
    },

    createClass({ commit }, classDefinition) {
        commit('setClassDefinition', classDefinition);
    

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
    setClassDefinition(state, classDefinition) {
        state.currentGame = { classDefinition: classDefinition };
    }

};

export const game = {
    namespaced: true,
    state,
    actions,
    mutations
};