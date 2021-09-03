<template>
  <div style="height: 300px" class="my-3">
    <v-card
      class="percorso-card font-weight-regular"
      v-bind:class="{ 'add-card': !percorso }"
    >
      <div v-if="percorso">
        <v-col cols="1"></v-col>
        <v-row
          ><v-col cols="6"
            ><div class="content-card">
              {{ percorso.pedibusGame.gameName }}
            </div></v-col
          >
          <v-col cols="5">
            <div>
              <v-chip
                class="ma-2"
                small
                color="green"
                label
                text-color="white"
                v-if="percorso.pedibusGame.deployed"
              >
                deployed
              </v-chip>

              <v-chip
                class="ma-1"
                small
                color="red"
                label
                text-color="white"
                v-else
              >
                not deployed
              </v-chip>
            </div>
          </v-col>
        </v-row>
        <div class="content-image">
          <v-img
            v-bind:src="percorso.pedibusGame.imageLink"
            max-width="200"
            min-height="150"
          />
        </div>

        <div class="content-start">
          Dal: {{ getTimestamp(percorso.pedibusGame.from) }}
        </div>

        <div class="content-start">Da: {{ percorso.pedibusGame.city }}</div>
      </div>

      <div v-else>+</div>
      <slot />
    </v-card>
  </div>
</template>

<script>
export default {
  data() {
    return {};
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
.content-card {
  padding-left: 15px;
}
.content-image {
  padding: 15px;
  padding-right: 0px;
  align-content: center;
  justify-content: center;
  align-items: center;
}
.content-start {
  padding: 15px;
  padding-right: 0px;
  padding-top: 0px;
  align-content: center;
  justify-content: center;
  align-items: center;
}
</style>

