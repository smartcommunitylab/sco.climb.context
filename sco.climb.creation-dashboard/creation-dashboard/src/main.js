import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import {router} from "./routes.js"
import { store } from './store'
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/vue-loading.css';
import i18n from './i18n'
import VueLuxon from "vue-luxon";
import { setupInterceptors } from './utils/httpInterceptors';
Vue.config.productionTip = false

Vue.use(Loading);
Vue.use(VueLuxon,{
  input: {
      zone: "utc",
      format: "iso"
  },
  output: "short"
});
Vue.config.productionTip = false

new Vue({
  vuetify,
  i18n,
  render: h => h(App),
  created() {
    setupInterceptors(store);
  },
  router,
  store:store
}).$mount('#app')
