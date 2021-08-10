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
            
            <v-row>
              <div
                class="col-sm-4 col-md-3 col-12"
                v-for="item in items"
                :key="item.message"
              >
                <Card-Percorso
                  :free="false"
                  @click="goToRoutePersonalization()"
                >
                  {{ item }}
                </Card-Percorso>
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
                  >Avanti</v-btn
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
import {mapActions} from "vuex";
import CardPercorso from "@/components/Card-Percorso.vue";
export default {
  name: "routeSuggestion",
  components: {
    "Card-Percorso": CardPercorso,
  },
  data() {
    return {
      items: ["Il giro del mediterraneo", "Le tradizioni italiane"],
      nomepagina: "routeSuggestion",
      titleCard: "Il giro del Mediterraneo",
    };
  },
  methods: {
  ...mapActions("navigation", {
      nextStep: "nextStep",
    }),
    goToRouteCreation() {
      this.$router.push("");
      this.nextStep();
    },
    goToRoutePersonalization() {
      this.$router.push("personalize");
      this.nextStep();
    },
    goToHabitsDefinition() {
      this.$router.push("habitsDefinition");
    },
  },

  mounted() {
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

