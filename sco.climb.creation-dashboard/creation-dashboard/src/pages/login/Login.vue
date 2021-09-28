<template>
  <div>
    <h2 class="text-xl"><p v-html="$t('login.welcome')"></p></h2>
     <!-- <v-btn color="primary"  @click="login" v-if="!status.loggedIn">Accedi</v-btn>
      <v-btn color="primary"  @click="logout" v-else>Logout</v-btn> -->
  </div>
  
</template>

<script>
import { mapState, mapActions } from "vuex";
export default {
  name: "Login",
  computed: {
    ...mapState("account", ["status"]),
  },
  methods: {
    ...mapActions("account", ["login", "logout"]),
  },
  created() {},
};
</script>

<style scoped></style>
