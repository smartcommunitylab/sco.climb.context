import { gameService } from '../services';

const state = {
    myGames: null,
    catalogGames: null,
    currentGame: null,
    currentFilters: null
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

    /*disciplines, geographicArea, minScore, maxScore*/
    getCatalogGames({ dispatch, commit }, filter) {
        commit('getCatalogGamesRequest');
        gameService.getCatalogGames(filter)
            .then(
                res => {
                    //todo reset old values
                    commit('getCatalogGamesSuccess', res["content"]);
                    commit('getFiltersSuccess', res.filter);
                    dispatch('alert/success', "Recuperati i tuoi giochi.", { root: true });
                },
                error => {
                    commit('getCatalogGamesFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle informazioni.", { root: true });
                }
            );
    },


    createClass({ commit }, classDefinition) {
        commit('setClassDefinition', classDefinition);
    },

    createHabits({ commit }, habitsDefinition) {
        commit('setHabitsDefinition', habitsDefinition);
    },


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

    getCatalogGamesRequest(state) {
        state.catalogGames = { loading: true };
    },
    getCatalogGamesSuccess(state, catalogGames) {
        state.catalogGames = { items: catalogGames };
    },
    getCatalogGamesFailure(state, error) {
        state.catalogGames = { error };
    },

    getFiltersSuccess(state, filters) {
        state.currentFilters = filters;
    },

    setClassDefinition(state, classDefinition) {
        state.currentGame = { classDefinition: classDefinition };
    },

    setHabitsDefinition(state, habitsDefinition) {
        state.currentGame = { habitsDefinition: habitsDefinition };
    },


};

export const game = {
    namespaced: true,
    state,
    actions,
    mutations
};