import { schoolService } from '../services';

const state = {
    institutes: null,
    schools: null,
}

const actions = {
    getAllSchools({ dispatch, commit }) {
        commit('getAllSchoolsRequest');
        schoolService.getAllSchools()
            .then(
                schools => {
                    //todo reset old values
                    commit('getAllSchoolsSuccess', schools);
                    dispatch('alert/success', "Recuperate le tue scuole.", { root: true });

                },
                error => {
                    commit('getAllSchoolsFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle informazioni.", { root: true });
                }
            );
    },
    getAllInstitutes({ dispatch, commit }) {
        commit('getAllInstitutesRequest');
        schoolService.getAllInstitutes()
            .then(
                institutes => {
                    //todo reset old values
                    commit('getAllInstitutesSuccess', institutes);
                    dispatch('alert/success', "Recuperati i tuoi activities.", { root: true });

                },
                error => {
                    commit('getAllActivitiesFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle informazioni.", { root: true });
                }
            );
    },

   




};

const mutations = {
    getAllInstitutesRequest(state) {
        state.institutes = { loading: true };
    },
    getAllInstitutesSuccess(state, institutes) {
        state.institutes = { items: institutes };
    },
    getAllInstitutesFailure(state, error) {
        state.institutes = { error };
    },
    getAllSchoolsRequest(state) {
        state.schools = { loading: true };
    },
    getAllSchoolsSuccess(state, schools) {
        state.schools = { items: schools };
    },
    getAllSchoolsFailure(state, error) {
        state.schools = { error };
    },
};

export const school
 = {
    namespaced: true,
    state,
    actions,
    mutations
};