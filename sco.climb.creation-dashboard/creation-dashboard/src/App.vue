<template>
  <v-app>
    <Loader v-if="loading" />

    <app-navigation></app-navigation>
    <v-main>
      <v-container fluid>
        <transition name="fade">
          <div v-if="alert.message" :class="`alert ${alert.type}`">
            {{ alert.message }}
          </div>
        </transition>
        <router-view></router-view>
      </v-container>
    </v-main>
    <!-- app-footer /-->
  </v-app>
</template>

<script>
import AppNavigation from "@/components/AppNavigation";
import { mapActions, mapState } from "vuex";
import Loader from "./components/Loader";
// import Footer from "@/components/Footer";
/*import CardPercorso from "@/components/Footer";*/
export default {
  name: "App",

  components: {
    AppNavigation,
    Loader,
    // "app-footer": Footer,
    /*"card-percorso": CardPercorso */
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
  },
  methods: {
    ...mapActions("alert", { clearAlert: "clear" }),
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
