const state = {
    type: null,
    message: null
};

const actions = {
    success({ commit }, message) {
        commit('success', message);
    },
    error({ commit }, message) {
        commit('error', message);
    },
    info({ commit }, message) {
        commit('info', message);
    },
    warning({ commit }, message) {
        commit('warning', message);
    },
    clear({ commit }) {
        commit('clear');
    }
};

const mutations = {
    success(state, message) {
        state.type = 'success';
        state.message = message;
    },
    error(state, message) {
        state.type = 'error';
        state.message = message;
    },
    info(state, message) {
        state.type = 'info';
        state.message = message;
    },
    warning(state, message) {
        state.type = 'warning';
        state.message = message;
    },
    clear(state) {
        state.type = null;
        state.message = null;
    }
};

export const alert = {
    namespaced: true,
    state,
    actions,
    mutations
};