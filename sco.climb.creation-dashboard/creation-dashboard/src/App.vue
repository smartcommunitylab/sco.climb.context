<template>
  <v-app>
    <Loader v-if="loading" />

    <v-main >
      <v-container fluid style="margin: 0; width: 100%">
      <!-- <a v-if="access_token" href @click.prevent="signOut">Sign out</a>
      <a v-else href @click.prevent="authenticateOidc">Sign in</a> -->
        <top-bar v-if="status.loggedIn"></top-bar>
        <transition name="fade">
          <div v-if="alert.message" :class="`alert ${alert.type}`">
            {{ alert.message }}
          </div>
        </transition>
        <router-view></router-view>
      </v-container>
    </v-main>
   <!-- <app-footer /> -->
  </v-app>
</template>

<script>
import { mapActions, mapState } from "vuex";
import Loader from "./components/Loader";
// import Footer from "@/components/Footer";
import TopBar from './components/TopBar.vue';
export default {
  name: "App",

  components: {
    Loader,
    // "app-footer": Footer,
    TopBar,
  },

  data: () => ({
    appTitle: "Dashboard",
    drawer: false,
  }),
  computed: {
    ...mapState({
      alert: (state) => state.alert,
    }),
    ...mapState("loader", ["loading"]),
    ...mapState("account", ["status"]),
    ...mapState("alert", ["message"]),
    ...mapState("oidcStore", ["access_token"]),
  },
  methods: {
    ...mapActions("alert", { clearAlert: "clear" }),
     ...mapActions('oidcStore', [
      'authenticateOidc',
      'removeOidcUser'
    ]),
    userLoaded: function (e) {
      console.log('I am listening to the user loaded event in vuex-oidc', e.detail)
    },
    oidcError: function (e) {
      console.log('I am listening to the oidc oidcError event in vuex-oidc', e.detail);

    },
    automaticSilentRenewError: function (e) {
      console.log('I am listening to the automaticSilentRenewError event in vuex-oidc', e.detail)
    },
    signOut: function () {
      this.removeOidcUser().then(() => {
        this.$router.push('/')
      })
    }
  },
  watch: {
    // eslint-disable-next-line no-unused-vars
    message(newAlert, oldAlert) {
      setTimeout(() => this.clearAlert(), 2500);
    },
    // eslint-disable-next-line no-unused-vars
    $route(to, from) {
      // clear alert on location change
      setTimeout(() => this.clearAlert(), 2500);
    },
  },
  mounted () {
    window.addEventListener('vuexoidc:userLoaded', this.userLoaded)
    window.addEventListener('vuexoidc:oidcError', this.oidcError)
    window.addEventListener('vuexoidc:automaticSilentRenewError', this.automaticSilentRenewError)
  },
  destroyed () {
    window.removeEventListener('vuexoidc:userLoaded', this.userLoaded)
    window.removeEventListener('vuexoidc:oidcError', this.oidcError)
    window.removeEventListener('vuexoidc:automaticSilentRenewError', this.automaticSilentRenewError)

  }
};
</script>
<style scoped>
.alert {
  padding: 20px;
  color: white;
  position: fixed;
  position: fixed; /* or absolute */
  top: 50%;
  margin: 10px;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
  border-radius: 25px;
}
.alert-success {
  background-color: rgba(15, 112, 183);
}
.alert-danger {
  background-color: #dc3545;
}

</style>


