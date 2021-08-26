<template>
  <div style="height: 240px" class="my-3">
    <v-card class="percorso-card" v-bind:class="{ 'add-card': !percorso }">
      <div v-if="percorso">
        {{ percorso.pedibusGame.gameName }}

        <div>
          <v-chip
            class="ma-1"
            color="green"
            label
            text-color="white"
            v-if="percorso.pedibusGame.deployed"
          >
            <v-icon left> mdi-label </v-icon>
            deployed
          </v-chip>

          <v-chip class="ma-1" color="red" label text-color="white" v-else>
            <v-icon left> mdi-label </v-icon>
            not deployed
          </v-chip>
        </div>

        <v-img
          v-bind:src="percorso.pedibusGame.imageLink"
          max-width="200"
          min-height="150"
        />

        {{ percorso.length }}
        {{ percorso.start }}

        Da: {{ getTimestamp(percorso.pedibusGame.from) }}
      </div>

      <div v-else>+</div>
      <slot />
    </v-card>
  </div>
  <!--length e start non ci sono -->
  <!-- this.$luxon(percorso.pedibusGame.from)  1475272800000
         let timestamp = 1615065599.426264
          undefined
          > new Date(timestamp).toJSON()
          '1970-01-19T16:37:45.599Z'
          > new Date(timestamp * 1000).toJSON()
        
        getTimestamp()
         -->
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
    percorso: Object,
  },
  methods: {
    getTimestamp(timestamp) {
      return this.$luxon(new Date(timestamp).toJSON());
    },
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
  text-align: left;
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
  border: 2px dashed #ccc !important;
  font-size: 42px !important;
  color: #cbcbcb !important;
}
</style>
