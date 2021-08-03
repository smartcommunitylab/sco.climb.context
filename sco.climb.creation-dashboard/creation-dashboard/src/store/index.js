import Vue from 'vue';
import Vuex from 'vuex';

import { account } from './account.module';
import { navigation } from './navigation.module';
import { alert } from './alert.module';
import { loader } from './loader.module';
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export const store = new Vuex.Store({
    modules: {
        account,
        navigation,
        alert,
        loader
    },
    plugins: [createPersistedState({
        storage: window.sessionStorage
    })]
    
});