const state = { page: null}
     const actions = {
    changePage({ commit }, page ) {
        commit('changePage', page );
    }, 
};

const mutations = {
    changePage(state, page) {
        state.page = page;
    }
};

export const navigation = {
    namespaced: true,
    state,
    actions,
    mutations
};