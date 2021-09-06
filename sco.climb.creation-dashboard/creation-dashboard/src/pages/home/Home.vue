<template>
  <v-container class="grey lighten-5">
    <v-card class="pa-1" outlined tile>
      <div class="align-center text-center mt-4">
        <v-row align="center">
          <v-col cols="12" sm="4">
            <v-avatar rounded="100" class="profile" color="grey" size="148">
              <v-img
                src="https://picsum.photos/id/1005/5760/3840.jpg?hmac=2acSJCOwz9q_dKtDZdSB-OIK1HUcwBeXco_RMMTUgfY"
              ></v-img>
            </v-avatar>
            <v-list-item class="pa-0" color="rgba(0, 0, 0, .4)">
              <v-list-item-content>
                <v-list-item-title
                  ><p class="font-weight-regular">
                    {{ username }}
                  </p>
                </v-list-item-title>
                <v-list-item-subtitle>
                  <p class="font-weight-regular">
                    {{ role }}
                  </p></v-list-item-subtitle
                >
              </v-list-item-content>
            </v-list-item>
          </v-col>
          <v-col cols="12" sm="8" class="text-left">
            <v-list-item-content>
              <v-list-item-title class="text-h6 pa-3">
                <p class="font-weight-regular">
                  Riepilogo informazioni insegnante
                </p>
              </v-list-item-title>
              <v-list-item-subtitle class="text pa-2"
                ><p class="font-weight-regular">
                  <v-icon>{{ territorioIcon }}</v-icon> Territorio:
                  {{ territorioName }}
                </p>
              </v-list-item-subtitle>
              <v-list-item-subtitle class="text pa-2"
                ><p class="font-weight-regular">
                  <v-icon>{{ istitutoIcon }}</v-icon> Istituto:
                  {{ istitutoName }}
                </p>
              </v-list-item-subtitle>
              <v-list-item-subtitle class="text pa-2">
                <p class="font-weight-regular">
                  <v-icon>{{ scuolaIcon }}</v-icon> Scuola: {{ scuolaName }}
                </p>
              </v-list-item-subtitle>
            </v-list-item-content>
          </v-col>
        </v-row>

        <v-row align="center">
          <v-col cols="12" sm="1"></v-col>
          <v-col cols="12" sm="10">
            <p class="text-h6 pa-1 text-left font-weight-regular">Crea percorsi</p>
          </v-col>
          <v-col cols="12" sm="1"></v-col>

          <v-row align="center" v-if="myGames.items">
            <v-col cols="12" sm="1"></v-col>
            <div
              class="col-sm-10 col-md-3"
              v-for="game in myGames.items"
              :key="game.id"
            >
              <Card-Percorso :percorso="game" :catalog="false"> </Card-Percorso>
            </div>

            <div
              class="col-sm-4 col-md-3 col-12"
              @click="goToClassDefinition()"
            >
              <Card-Percorso> </Card-Percorso>
            </div>
            <v-col cols="12" sm="1"></v-col>
          </v-row>
        </v-row>
      </div>
    </v-card>
  </v-container>
</template>

<script>
import {
  mdiMapMarker,
  mdiSchoolOutline,
  mdiBookEducationOutline,
} from "@mdi/js";
import { mapState, mapActions } from "vuex";
import CardPercorso from "@/components/Card-Percorso.vue";
export default {
  name: "Home",
  components: {
    "Card-Percorso": CardPercorso,
  },
  data() {
    return {
      nomepagina: "Home",
      territorioIcon: mdiMapMarker,
      istitutoIcon: mdiBookEducationOutline,
      scuolaIcon: mdiSchoolOutline,
      territorioName: "Trentino",
      istitutoName: "IC TN6",
      scuolaName: "S. Pertini",
      role: "Insegnante",
      username: "Francesca Russo",
    };
  },
  computed: {
    ...mapState("game", ["myGames"]),
  },
  methods: {
    goToClassDefinition() {
      this.$router.push("classDefinition");
      this.nextStep();
    },
    ...mapActions("game", {
      getAllMyGames: "getAllMyGames",
    }),
    ...mapActions("navigation", {
      nextStep: "nextStep",
    }),
  },
  mounted() {
    this.getAllMyGames();
  },
};
</script>

<style>
.col-md-3 {
  padding: 8px;
}

.pa-0 {
  padding-bottom: 135px;
}
.full-height {
  height: 100%;
}
</style>
