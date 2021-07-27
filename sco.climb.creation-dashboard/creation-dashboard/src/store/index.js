import Vue from 'vue';
import Vuex from 'vuex';

import { account } from './account.module';
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export const store = new Vuex.Store({
    modules: {
        account
    },
    plugins: [createPersistedState({
        storage: window.sessionStorage
    })]
    
});