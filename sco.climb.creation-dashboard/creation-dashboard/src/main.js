import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import {router} from "./routes.js"
import { store } from './store'
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/vue-loading.css';
import i18n from './i18n'
Vue.use(Loading);
Vue.config.productionTip = false

new Vue({
  vuetify,
  i18n,
  render: h => h(App),
  router,
  store:store
}).$mount('#app')
