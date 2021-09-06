<template>
  <div>
    <v-row
      ><v-btn elevation="2" depressed>
        {{ title }} <v-icon>{{ icon }}</v-icon></v-btn
      ></v-row
    >
  </div>
</template>

<script>
export default {
  data() {
    return {};
  },
  props: {
    icon: String,
    title: String,
  },
  namespaced: true,

  methods: {},
};
</script>

<style>
</style>
