<template>
  <v-container 
  :fluid="true"
  class="pa-0">
  <!-- <test>
    <div @click="claDef">
      Ptova slot
    </div>
  </test> -->
    <v-row class="mt-8">
      <v-col offset="2" cols="8">
        <v-row>
          <v-col cols="12" align-self="center">
            <div class="d-flex justify-content-center align-items-center full-width">
              <v-avatar rounded="100" class="profile" color="grey" size="200">
                <v-img
                  src="https://picsum.photos/id/1005/5760/3840.jpg?hmac=2acSJCOwz9q_dKtDZdSB-OIK1HUcwBeXco_RMMTUgfY"
                ></v-img>
              </v-avatar>

              <div class="d-inline-block ml-16">
                <p class="titleFont"
                  v-html="$t('home.name')"></p>
                <p class="font-weight-regular">
                  <v-icon>{{ territorioIcon }}</v-icon>
                  <span color="primary"
                  v-html="$t('home.territorio')">
                  {{ territorioName }}</span>
                </p>
                <p class="font-weight-regular">
                  <v-icon>{{ istitutoIcon }}</v-icon> Istituto:
                  {{ istitutoName }}
                </p>
                <p class="font-weight-regular">
                  <v-icon>{{ scuolaIcon }}</v-icon> Scuola: {{ scuolaName }}
                </p>
              </div>  
            </div>

          </v-col>
        </v-row>  
      </v-col>        
    </v-row>
    
    <v-row>
      <v-col offset="2" cols="8">
        <v-row>
          <v-col cols="12" align-self="center" class="pb-0 mb-0">
          <p class="subtitleFont pa-1 text-left font-weight-regular" v-html="$t('home.subTitle')"></p>
          </v-col>
        </v-row>  
      </v-col>        
    </v-row>
    <v-row class="pt-0 mt-0">
      <v-col offset="2" cols="8">
        <v-row>
          <v-col cols="12" align-self="center" >
            <v-row v-if="myGames">
              <v-col cols="4" align-self="center"
                v-for="game in myGames.items"
                :key="game.id">

                <Card-Percorso :percorso="game" :catalog="false" elevation="3"> </Card-Percorso>
              
              </v-col>
            </v-row>  
          </v-col>
        </v-row>  
      </v-col>        
    </v-row>
    
    <v-btn
      elevation="6"
      color="secondary"
      
      rounded
      bottom
      right
      :absolute="true"
      text
      class="imFab"
      @click="claDef()"
      >
    <v-icon>mdi-plus</v-icon>
    <span>Crea percorso</span>
    </v-btn>
</v-container>


        <!-- <v-list-item-content>
      <v-list-item-title class="text-h6 pa-3">
        <p class="titleFont"
          v-html="$t('home.name')"></p>
      </v-list-item-title>
      <v-list-item-subtitle class="text pa-2"
        ><p class="font-weight-regular">
          <v-icon>{{ territorioIcon }}</v-icon>
          <span color="primary"
          v-html="$t('home.territorio')">
          {{ territorioName }}</span>
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

    <v-row align="center" class="pt-0 mt-0" style="background-color: blue">
        <v-col class="align-center text-center mt-0" cols="12" sm="4" style="background-color: pink">
            <v-avatar rounded="100" class="profile" color="grey" size="200">
              <v-img
                src="https://picsum.photos/id/1005/5760/3840.jpg?hmac=2acSJCOwz9q_dKtDZdSB-OIK1HUcwBeXco_RMMTUgfY"
              ></v-img>
            </v-avatar>
            
          </v-col>
          <v-col cols="12" sm="8" class="text-left">
            <v-list-item-content>
              <v-list-item-title class="text-h6 pa-3">
                <p class="titleFont"
                  v-html="$t('home.name')"></p>
              </v-list-item-title>
              <v-list-item-subtitle class="text pa-2"
                ><p class="font-weight-regular">
                  <v-icon>{{ territorioIcon }}</v-icon>
                  <span color="primary"
                  v-html="$t('home.territorio')">
                  {{ territorioName }}</span>
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
        </v-row> -->
        <!-- <v-col cols="12" sm="10" style="background-color: red">
          </v-col>
          <v-col cols="12" sm="1" style="background-color: pink"></v-col> -->
           <!-- <div
              class="col-sm-4 col-md-3 col-12"
              
            >
              <Card-Percorso> </Card-Percorso>
            </div> -->
  
</template>

<script>
import {
  mdiMapMarker,
  mdiSchoolOutline,
  mdiBookEducationOutline,
} from "@mdi/js";
// import Test from '@/components/Test.vue';
import { mapState, mapActions } from "vuex";
import CardPercorso from "@/components/Card-Percorso.vue";
export default {
  name: "Home",
  components: {
    "Card-Percorso": CardPercorso,
    // "test":Test
  },
  data() {
    return {
      territorioIcon: mdiMapMarker,
      istitutoIcon: mdiBookEducationOutline,
      scuolaIcon: mdiSchoolOutline,
      territorioName: "Trentino",
      istitutoName: "IC TN6",
      scuolaName: "S. Pertini",
      role: "Insegnante",
      username: "Francesca Russo",
    }
  },
  
  computed: {
    ...mapState("game", ["myGames"]),
  },
  methods: {
        claDef() {
           this.$router.push("classdefinition");
           this.nextStep();
    },

    // goToClassDefinition() {
    //   this.$router.push("classDefinition");
    //   this.nextStep();
    // },
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

.justify-content-center { justify-content: center; }
.align-items-center { align-items: center; }
</style>



