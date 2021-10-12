import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';

Vue.use(Vuetify);

export default new Vuetify({
    theme: {
        themes: {
          light: {
            primary: '#034432',
            secondary: '#b0bec5',
            yellow: '#FFB60A',
            green: '#00C86E',
            accent: '#8c9eff',
            alert: '#b71c1c',
          },
        },
      },
    
});
