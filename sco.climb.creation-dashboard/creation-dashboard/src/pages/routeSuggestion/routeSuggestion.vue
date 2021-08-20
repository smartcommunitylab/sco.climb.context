<template>
  <v-container style="height: 1000px" class="grey lighten-5">
    <v-row no-gutters>
      <v-col cols="12" sm="12">
        <v-card class="pa-2" outlined tile>
          <div class="pa-7">
            <p class="text-center">{{ nomepagina }}</p>
            <hr />
            <h2>Percorsi consigliati</h2>
            <h4>
              Il catalogo di Kids Go Green offre questi percorsi per i gruppi
              simili al suo
              <v-tooltip v-model="show" right
                ><template v-slot:activator="{ on, attrs }">
                  <v-btn icon v-bind="attrs" v-on="on">
                    <v-icon color="grey lighten-1">
                      mdi-information-outline
                    </v-icon>
                  </v-btn>
                </template>
                <span
                  >E’ possibile modificare questi percorsi oppure crearne uno
                  nuovo</span
                >
              </v-tooltip>
            </h4>

            <v-row class="pa-7">
              <div class="col-sm-2"></div>
              <v-btn @click="expand = !expand" class="ma-2" color="primary"
                >Filtri <v-icon>mdi-filter-outline </v-icon></v-btn
              >
              <!-- disciplines in catalogGames.json-->

              <v-expand-transition>
                <v-card
                  v-show="expand"
                  height="150"
                  width="500"
                  color="primary"
                >
                  <v-row class="pa-4">
                    <v-col cols="1"></v-col>
                    <div>Materia:</div></v-row
                  >

                  <v-row class="pa-4">
                    <v-col cols="1"></v-col>
                    <div>Area Geografica:</div>
                  </v-row>
                </v-card>
              </v-expand-transition></v-row
            >

            <v-row v-if="catalogGames.items">
              <div class="col-sm-3"></div>
              <div
                class="col-sm-4 col-md-3 col-12"
                v-for="game in catalogGames.items"
                :key="game.id"
              >
                <Card-Suggestion :percorso="game"> </Card-Suggestion>
              </div>
            </v-row>

            <div class="row py-6">
              <div class="col-sm-1"></div>
              <div class="col-sm-5">
                <v-btn class="float-left" @click="goToHabitsDefinition()"
                  >Indietro</v-btn
                >
              </div>
              <div class="col-sm-5">
                <v-btn class="float-right" @click="goToRouteCreation()"
                  >Salta e crea percorso</v-btn
                >
              </div>
              <div class="col-sm-1"></div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mapActions, mapState } from "vuex";
import CardSuggestion from "@/components/Card-Suggestion.vue";
export default {
  name: "routeSuggestion",
  components: {
    "Card-Suggestion": CardSuggestion,
  },
  data() {
    return {
      nomepagina: "routeSuggestion",
      expand: false,
    };
  },
  computed: {
    ...mapState("game", ["catalogGames"]),
  },
  methods: {
    ...mapActions("navigation", {
      nextStep: "nextStep",
    }),
    goToRouteCreation() {
      this.$router.push("routeCreation");
      this.nextStep();
    },
    ...mapActions("game", {
      getCatalogGames: "getCatalogGames",
    }),
    goToHabitsDefinition() {
      this.$router.push("habitsDefinition");
    },
  },

  mounted() {
    this.getCatalogGames();
    let loader = this.$loading.show({
      canCancel: false,
      backgroundColor: "#000",
      color: "#fff",
    });
    setTimeout(() => {
      loader.hide();
    }, 500);
  },
};
</script>

<style></style>

