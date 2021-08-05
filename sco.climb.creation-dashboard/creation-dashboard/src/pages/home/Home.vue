<template>
  <v-container class="grey lighten-5">
    <v-card class="pa-1" outlined tile>
      <p class="text-center text-h3 my-1">{{ nomepagina }}</p>
      <hr />
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
        </v-row>

        <v-row v-if="myGames.items">
          <div
            class="col-sm-4 col-md-3 col-12"
            v-for="game in myGames.items"
            :key="game.id"
          >
            <Card-Percorso :percorso="game">
            </Card-Percorso>
          </div>

          <div class="col-sm-4 col-md-3 col-12" @click="goToClassDefinition()">
            <Card-Percorso>
            </Card-Percorso>
          </div>
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
      // items: [{
      //   id:'1',
      //   title:"Il giro del mediterraneo",
      //   length:"1845 Km",
      //   start:'Trento',
      //   imgsource: 'https://i.picsum.photos/id/1026/4621/3070.jpg?hmac=OJ880cIneqAKIwHbYgkRZxQcuMgFZ4IZKJasZ5c5Wcw'}, {
      //     id:'2',
      //   title:"Le tradizioni italiane",
      //   length:"245 Km",
      //   start:'Pescara',
      //   imgsource: 'https://i.picsum.photos/id/1043/5184/3456.jpg?hmac=wsz2e0aFKEI0ij7mauIr2nFz2pzC8xNlgDHWHYi9qbc'}
      //     ],
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
      titleCard: "Il giro del Mediterraneo",
      kilometers: "12424",
    };
  },
    computed: {
    ...mapState("game", ["myGames"])
  },
  methods: {
    goToClassDefinition() {
      this.$router.push("classDefinition");
    },
    ...mapActions("game", {
      getAllMyGames: "getAllMyGames",
    })
  },
  mounted() {
    this.getAllMyGames();
  },
};
</script>

<style>
.pr {
  padding-left: 25px;
}
.pa-0 {
  padding-bottom: 135px;
}
.full-height {
  height: 100%;
}
</style>

