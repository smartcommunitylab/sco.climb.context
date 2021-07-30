<template>
  <div>
    <h2 class="text-xl">  <p>{{ $t('hello') }}</p>
</h2>
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
