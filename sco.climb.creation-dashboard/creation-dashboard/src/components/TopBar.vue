<template>
  <div>
    <v-app-bar app color="primary" dark height="56px">
      <v-toolbar-title class="pt-4">
        <p v-html="$t('topbar.title')"></p>
      </v-toolbar-title>
      <v-spacer class="hidden-sm-and-down"></v-spacer>
      <!-- <v-btn text class="hidden-sm-and-down" @click="login" v-if="!status.loggedIn">Accedi</v-btn>
      <v-btn text class="hidden-sm-and-down" @click="logout" v-else>Logout</v-btn> -->
      <v-btn text class="hidden-sm-and-down"  v-if="status.loggedIn" href @click.prevent="logout">Sign out</v-btn>
      <v-btn text class="hidden-sm-and-down"  v-else href @click.prevent="login">Sign in</v-btn>
    </v-app-bar>
    <v-navigation-drawer
      class="hidden-md-and-up"
      @click="drawer = !drawer"
      v-model="drawer"
      absolute
      temporary
    >
      <v-list nav dense>
        <v-list-item-group
          v-model="group"
          active-class="deep-purple--text text--accent-4"
        >
          <template v-for="(item, index) in items">
            <v-list-item :key="index">
              <v-list-item-icon>
                <v-icon>{{ item.icon }}</v-icon>
              </v-list-item-icon>
              <v-list-item-title @click="navigate(item.action)">{{ item.title }}</v-list-item-title>
            </v-list-item>
          </template>
        </v-list-item-group>
      </v-list>
    </v-navigation-drawer>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
export default {
  name: "TopBar",
computed: {
    ...mapState("account", ["status"]),
    ...mapState("oidcStore", ["access_token"]),

  },
  created() {},
  data: () => ({
    appTitle: "Dashboard",
    drawer: false,
    group: null,
    items: [
      { title: "Menu", icon: "mdi-menu", action: "menu" },
      { title: "Accedi", icon: "mdi-login", action: "login" },
      { title: "Step 1", icon: "mdi-pencil", action: "login" },
    ],
  }),
  methods: {
    ...mapActions("account", {logoutAction:"logout"}),
    ...mapActions('oidcStore', [
      'authenticateOidc',
      'removeOidcUser'
    ]),
      navigate(method) {
          this[method]();
      },
    login() {
        this.authenticateOidc();
    //   this.$router.push("/home");
    },
    logout() {
        this.logoutAction();
        this.removeOidcUser();
    //   this.$router.push("/home");
    },
  },
};
</script>
