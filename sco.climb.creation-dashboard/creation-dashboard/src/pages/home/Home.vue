<template>
  <v-container class="grey lighten-5">
    <v-card class="pa-1" outlined tile>
      <div class="align-center text-center mt-4">
        <v-row justify="center" align="center">
          <v-col cols="12" sm="4">
            <v-avatar rounded="100" class="profile" color="grey" size="148">
              <v-img
                src="https://picsum.photos/id/1005/5760/3840.jpg?hmac=2acSJCOwz9q_dKtDZdSB-OIK1HUcwBeXco_RMMTUgfY"
              ></v-img>
            </v-avatar>
            <v-list-item class="pa-0" color="rgba(0, 0, 0, .4)">
              <v-list-item-content>
                <v-list-item-title class="text-h6">
                  {{ username }}
                </v-list-item-title>
                <v-list-item-subtitle class="text-h8">{{
                  role
                }}</v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </v-col>
          <v-col cols="12" sm="8" class="text-left">
            <v-list-item-content>
              <v-list-item-title class="text-h5 pa-3">
                Riepilogo informazioni {{ role }}
              </v-list-item-title>
              <v-list-item-subtitle class="text pa-2"
                ><v-icon>{{ territorioIcon }}</v-icon> Territorio:
                {{ territorioName }}
              </v-list-item-subtitle>
              <v-list-item-subtitle class="text pa-2"
                ><v-icon>{{ istitutoIcon }}</v-icon> Istituto:
                {{ istitutoName }}
              </v-list-item-subtitle>
              <v-list-item-subtitle class="text pa-2">
                <v-icon>{{ scuolaIcon }}</v-icon> Scuola:
                {{ scuolaName }}
              </v-list-item-subtitle>
            </v-list-item-content>
          </v-col>
        </v-row>

        <v-row justify="center" align="center">
          <v-col cols="12" sm="1"></v-col>
          <v-col cols="12" sm="10">
            <p class="text-h5 text-left">Crea percorsi</p>
          </v-col>
          <v-col cols="12" sm="1"></v-col>

          <v-row justify="left" align="center" v-if="myGames.items">
            <v-col cols="12" sm="1"></v-col>
            <div
              class="col-sm-10 col-md-3"
              v-for="game in myGames.items"
              :key="game.id"
            >
              <Card-Percorso :percorso="game"> </Card-Percorso>
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
import institutes from "../../../public/tmp-data/institutes.json";
import CardPercorso from "@/components/Card-Percorso.vue";
export default {
  name: "Home",
  components: {
    "Card-Percorso": CardPercorso,
  },
  data() {
    return {
      institutesJson: institutes,
      nomepagina: "Home",
      territorioIcon: mdiMapMarker,
      istitutoIcon: mdiBookEducationOutline,
      scuolaIcon: mdiSchoolOutline,
      territorioName: "Trentino",
      istitutoName: "IC TN6",
      scuolaName: "S. Pertini",
      role: "Insegnante",
      username: "Francesca Russo",
      departure: "Trento",
      kilometers: "12424",
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
