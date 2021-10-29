import {router} from '../routes'

const items = [

  {
    step: 1,
    text: "Gruppo partecipanti",
    class: "classDefinition",
    href: "classDefinition"
  },
  {
    step: 2,
    text: "Abitudine mobilitá",
    class: "habitsDefinition",
    href: "habitsDefinition"
  },
  {
    step: 3,
    text: "Percorsi consigliati",
    class: "routeSuggestion",
    href: "routeSuggestion"
  },
  {
    step: 4,
    text: "Creazione percorso",
    class: "personalize",
    href: "routeCreation"
  },
  {
    step: 5,
    text: "Riepilogo",
    class: "summary",
    href: "summary"
  },
]
const state = {
  page: null,
  currentStep: 0,
  lastStep: 0,
  items: items
}
const actions = {
  changePageByStepper({ commit,state,dispatch }, page) {
    if (page.step <= state.lastStep) {
      router.push(page.href);
    commit('changePage', page);
    } else {
      dispatch('alert/error', "Passo non ancora accessibile", { root: true });
    }
  },
  prevStep({ commit,state,dispatch }) {
    var pageFound = state.items.find(page => page.step === (state.currentStep-1))
    if (pageFound) {
      router.push(pageFound.href);
      commit('changePage', pageFound);
      } else {
        dispatch('alert/error', "Pagina non trovata", { root: true });

      }
  },
  nextStep({ commit,state,dispatch }) {
    var pageFound = state.items.find(page => page.step === (state.currentStep+1))
    if (pageFound) {
      router.push(pageFound.href);
      commit('changePage', pageFound);
      } else {
        dispatch('alert/error', "Pagina non trovata", { root: true });

      }
  },
  changePageByName({ commit,state }, pageName) {
    var pageFound = state.items.find(page => page.href === pageName)
    if (!pageFound)
       {
        pageFound ={
        step: 0,
        text: "Home",
        class: "home",
        href: "home"
      }
      //commit('initNavigation');
    }
      router.push(pageFound.href);
      commit('changePage', pageFound);
  }, 
  beginSteps({dispatch}){
    dispatch('changePageByName',items[0].href);
    dispatch('game/initGame', null, { root: true });
  },
  logout({ commit }) {
    commit('logout');
  }
};

const mutations = {
  changePage(state, page) {
      state.page = page;
      state.currentStep = page.step;
      if (state.currentStep > state.lastStep)
          {state.lastStep = state.currentStep;
          return;}
      if (state.currentStep==0){
        state.lastStep = 0;
      }
  },
  initNavigation(state) {
    state.currentStep = 0;
    state.lastStep=0;
    state.page = null;
     state.items = JSON.parse(JSON.stringify(items));
  },
  logout(state) {
    state.currentStep = 0;
    state.lastStep=0;
    state.page = null;
    state.items = JSON.parse(JSON.stringify(items));
  }
};

export const navigation = {
  namespaced: true,
  state,
  actions,
  mutations
};