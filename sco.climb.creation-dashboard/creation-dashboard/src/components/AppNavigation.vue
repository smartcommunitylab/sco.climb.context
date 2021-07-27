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
    <v-btn text class="hidden-sm-and-down">Accedi</v-btn>
  </v-app-bar>
  <v-navigation-drawer class="hidden-md-and-up" @click="drawer = !drawer"
      v-model="drawer"
      absolute
      temporary
    >
      <v-list
        nav
        dense
      >
        <v-list-item-group
          v-model="group"
          active-class="deep-purple--text text--accent-4"
        >
          <v-list-item>
            <v-list-item-icon>
              <v-icon>mdi-login</v-icon>
            </v-list-item-icon>
            <v-list-item-title>Accedi</v-list-item-title>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-navigation-drawer>
</div>
</template>

<script>
export default {
  name: "AppNavigation",
  data: () => ({
    appTitle: "Dashboard",
    drawer: false,
  }),
};
</script>
