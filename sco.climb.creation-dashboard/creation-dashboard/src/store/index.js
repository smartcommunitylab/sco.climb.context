import Vue from 'vue';
import Vuex from 'vuex';
import { vuexOidcCreateStoreModule } from 'vuex-oidc';
import { account } from './account.module';
import { navigation } from './navigation.module';
import { alert } from './alert.module';
import { loader } from './loader.module';
import { oidcSettings } from '../services/auth.service'
import { game } from './game.module';
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export const store = new Vuex.Store({
    modules: {
        account,
        oidcStore: vuexOidcCreateStoreModule(oidcSettings),
        navigation,
        alert,
        loader,
        game
    },
    plugins: [createPersistedState({
        storage: window.sessionStorage
    })]
    
});