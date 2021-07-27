
    <template>
      <v-app-bar
      app
      color="primary"
      dark
    >
     <v-app-bar-nav-icon class="hidden-md-and-up" @click="drawer = !drawer"></v-app-bar-nav-icon>
    <v-spacer class="hidden-md-and-up"></v-spacer>
    <v-toolbar-title>{{appTitle}}</v-toolbar-title>
    <v-spacer class="hidden-sm-and-down"></v-spacer>
    <v-btn flat class="hidden-sm-and-down">SIGN IN</v-btn>
    <v-btn class="hidden-sm-and-down">JOIN</v-btn>
    </v-app-bar>
</template>

<script>
  export default {
    name: 'AppNavigation',
     data: () => ({
            appTitle: 'Dashboard',
            drawer: false
  })
  }
</script>
