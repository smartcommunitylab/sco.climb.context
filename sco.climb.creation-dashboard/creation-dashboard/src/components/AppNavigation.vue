<template>
  <div>
    <v-app-bar app color="primary" dark>
      <v-app-bar-nav-icon
        class="hidden-md-and-up"
        @click="drawer = !drawer"
      ></v-app-bar-nav-icon>
      <v-spacer class="hidden-md-and-up"></v-spacer>
      <v-toolbar-title>{{ appTitle }}</v-toolbar-title>
      <v-spacer class="hidden-sm-and-down"></v-spacer>
      <v-btn text class="hidden-sm-and-down" @click="login" v-if="!status.loggedIn">Accedi</v-btn>
      <v-btn text class="hidden-sm-and-down" @click="logout" v-else>Logout</v-btn>
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
  name: "AppNavigation",
computed: {
    ...mapState("account", ["status"]),
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
      ...mapActions("account", {loginAction:"login", logoutAction:"logout"}),
      navigate(method) {
          this[method]();
      },
    login() {
        this.loginAction();
    //   this.$router.push("/home");
    },
    logout() {
        this.logoutAction();
    //   this.$router.push("/home");
    },
  },
};
</script>
