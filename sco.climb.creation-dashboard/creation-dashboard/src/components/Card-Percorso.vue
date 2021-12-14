<template>
  <!--<div style="height: 385px; background-color: red" class="my-3">
    <v-card
      class="percorso-card font-weight-regular"
      v-bind:class="{ 'add-card': !percorso }"
    >
      NOME PERCORSO
      <div v-if="percorso">
        <v-col cols="1"></v-col>
        <v-row
          ><v-col cols="6"
            ><div class="content-card">
              {{ percorso.pedibusGame.gameName }}
            </div></v-col
          >

          CHIP
          <v-col cols="5" style="background-color: yellow">
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

        IMMAGINE
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
  </div>-->
  <div style="height: 100%" class="pa-4" v-if="percorso">
    <v-card class=" d-flex flex-column font-weight-regular rounded-lg" elevation="3">
      <v-img
      :src="percorso.pedibusGame.imageLink">
      </v-img>
       
       <div class="d-flex flex-row">
              <v-chip
                class="ma-2 rounded-xl"
                :class="{'yellow': !isDeployed(), 'green': isDeployed()}"
                small
                label
                text-color="white"
              >
              
              <span v-if="isDeployed()"
              >
              VALIDATO</span>
              <span v-else
              >
              NON ANCORA VALIDATO</span>
              </v-chip>

              <!-- <v-chip
                class="ma-2 rounded-xl yellowchip"
                small
                label
                text-color="white"
                v-else
              >
                NON APPROVATO
              </v-chip> -->
            </div>
    <v-card-title
    class="pa-1 pt-0 ml-2">{{ percorso.pedibusGame.gameName }}</v-card-title>
    <v-card-text>
      <div
      align="left"
      class="ml-1"
      >
            {{ getTimestamp(percorso.pedibusGame.from) }}</div>
    </v-card-text>

    <v-card-actions class="align-self-end">
      <v-btn
        class="rounded-xl"
        color="var(--primary)"
        text
        @click="showAlert = true"
        >
        Visualizza
      </v-btn>
    </v-card-actions>
    
    </v-card>

  </div>
</template>

<script>
export default {
  data: function() {
    return {
      showAlert: false,
    };
},
  namespaced: true,
  props: {
    percorso: Object,
  },
  methods: {
    isDeployed() {
      return this.percorso.pedibusGame.deployed
    },
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
.percorso-cardTTT {
  height: 100%;
  padding: 0;
  display: flex !important;
  align-content: center;
  justify-content: center;
  align-items: center;
  text-align: left;
  margin: auto;
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
.content-imageT {
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

