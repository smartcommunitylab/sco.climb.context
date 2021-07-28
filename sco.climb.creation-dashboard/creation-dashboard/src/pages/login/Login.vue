<template>
  <div>
    <h1 class="text-xl">Login</h1>
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
