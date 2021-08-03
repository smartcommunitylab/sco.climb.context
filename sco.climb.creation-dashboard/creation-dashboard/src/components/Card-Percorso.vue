<template>
  <div style="height: 240px" class="my-3">
    <v-card class="percorso-card" v-bind:class="{ 'add-card': !percorso }">
      <div  v-if="percorso">
        {{ percorso.title }}
        {{ percorso.length }}
        {{ percorso.start }}
      </div>
      <div v-else>
        +
      </div>
      <slot />
    </v-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      date: new Date().getFullYear(),
    };
  },
  namespaced: true,
  props: {
    // free: {type: Boolean, required: true, default: false},
    percorso: Object,
  },
  methods: {
    openTab(url) {
      window.open(url, "_blank");
    },
  },
};
</script>

<style>
.percorso-card {
  min-width: 180px;
  max-width: 248px !important;
  height: 100%;
  border-radius: 8px;
  border: 1px solid #ccc;
  padding: 0;
  display: flex !important;
  align-content: center;
  justify-content: center;
  align-items: center;
  margin: auto;
  cursor: pointer;
  transition: 0.5s all;
}
.percorso-card:hover {
  box-shadow: 2px 2px 6px 0px rgba(0, 0, 0, 0.75) !important;
  -webkit-box-shadow: 3px 4px 13px 2px rgba(0, 0, 0, 0.75);
  -moz-box-shadow: 3px 4px 13px 2px rgba(0, 0, 0, 0.75);
  transition: 0.5s all;
}
.add-card {
  border: 2px dashed #ccc!important;
  font-size: 42px !important;
  color: #cbcbcb !important;
}
</style>
