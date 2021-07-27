<template>
  <v-app>
    <app-navigation></app-navigation>

    <v-main>
    <v-container fluid>
      <router-view></router-view>
    </v-container>
    </v-main>
  </v-app>
</template>

<script>
import AppNavigation from '@/components/AppNavigation';
export default {
  name: 'App',

  components: {
    AppNavigation,
  //   HelloWorld,
  },

  data: () => ({
            appTitle: 'Dashboard',
            drawer: false
  }),
};
</script>
