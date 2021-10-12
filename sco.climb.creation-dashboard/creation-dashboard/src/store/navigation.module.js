
    const items = [

      {
        step:1,
        text: "Gruppo partecipanti",
        class: "classDefinition",
        href: "classDefinition",
        disabled:true
      },
      {
        step:2,
        text: "Abitudine mobilitá",
        class: "habitsDefinition",
        href: "habitsDefinition",
        disabled:true
      },
      {
        step:3,
        text: "Percorsi consigliati",
        class: "routeSuggestion",
        href: "routeSuggestion",
        disabled:true
      },
      {
        step:4,
        text: "Creazione percorso",
        class: "personalize",
        href: "routeCreation",
        disabled:true
      },
      {
        step:5,
        text: "Riepilogo",
        class: "summary",
        href: "summary",
        disabled:true
      },
    ]
    const state = { 
     page: null,
     currentStep:0,
     items: JSON.parse(JSON.stringify(items))}
     const actions = {
    changePage({ commit }, page ) {
        commit('changePage', page );
    },
    nextStep({commit}) {
        commit('nextStep');
    },
    logout({commit}){
        commit('logout');
    }
};

const mutations = {
    changePage(state, page) {
        state.page = page;
    },
    nextStep(state) {
        //check step and definition and increase in case is not defined
        state.currentStep++;
        // state.items.map(item => {
        //     if (item.step===state.currentStep)
        //         {
        //             item.disabled=false;
        //             state.page=item.href
        //         }
        // })
    },
    logout(state){
        state.currentStep=0;
        state.page=null;
        state.items=JSON.parse(JSON.stringify(items));
    }
};

export const navigation = {
    namespaced: true,
    state,
    actions,
    mutations
};