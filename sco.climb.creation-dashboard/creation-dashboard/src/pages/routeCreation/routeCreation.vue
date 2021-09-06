<template>
  <v-container style="height: 1000px" class="grey lighten-5">
    <v-row no-gutters>
      <v-col cols="12" sm="12">
        <v-card class="pa-2" outlined tile>
          <div class="pa-7">
            <p class="text-center">{{ nomepagina }}</p>
            <hr />
            <h2>Creazione percorso</h2>
            <h4>Crea il tuo percorso</h4>
            <v-col cols="1"></v-col>
            <v-col cols="1"></v-col>
            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="6"
                ><h4>Nome percorso</h4>
                <v-text-field></v-text-field></v-col
            ></v-row>
            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="9"
                ><h4>Descrizione percorso</h4>
                <v-textarea solo name="input-7-4"></v-textarea></v-col
            ></v-row>

            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="9"
                ><div id="preview" class="container-image">
                  <img
                    class="uploading-image"
                    v-if="item.imageUrl"
                    :src="item.imageUrl"
                  />
                </div>
                <div class="container-image">
                  <input type="file" accept="image/*" @change="onChange" />
                </div> </v-col
            ></v-row>

            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="6"><h4>Tappe</h4> </v-col></v-row
            >

            <v-row>
              <v-col cols="1"></v-col>
              <v-col cols="2"
                ><Button-Tappa
                  icon="mdi-plus"
                  title="Crea tappa"
                  @click.native="goToCreateDeparture()"
                ></Button-Tappa>
              </v-col>
              <v-col cols="2"
                ><Button-Tappa
                  icon="mdi-arrow-split-horizontal"
                  title="Riordina tappe"
                  @click.native="goToCreateDeparture()"
                ></Button-Tappa> </v-col
            ></v-row>
            <div class="row py-6">
              <div class="col-sm-1"></div>
              <div class="col-sm-5">
                <v-btn class="float-left" @click="goToRouteSuggestion()"
                  >Indietro</v-btn
                >
              </div>
              <div class="col-sm-5">
                <v-btn class="float-right" @click="goToSummary()">Avanti</v-btn>
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
//import axios from "axios";
import { mapActions } from "vuex";
import ButtonTappa from "@/components/Button-Tappa.vue";
export default {
  name: "routeCreation",
  components: {
    "Button-Tappa": ButtonTappa,
  },
  data() {
    return {
      item: {
        //...
        image: null,
        imageUrl: null,
      },
      nomepagina: "routeCreation",
      previewImage: null,
    };
  },

  methods: {
    onChange(e) {
      const file = e.target.files[0];
      this.image = file;
      this.item.imageUrl = URL.createObjectURL(file);
    },
    ...mapActions("navigation", {
      nextStep: "nextStep",
    }),
    goToSummary() {
      this.$router.push("summary");
      this.nextStep();
    },
    goToCreateDeparture() {
      this.$router.push("createDeparture");
    },
     goToRouteSuggestion() {
      this.$router.push("routeSuggestion");
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

<style>
.uploading-image {
  display: flex;
  max-width: 350px;
  max-height: 350px;
}
.container-image {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>



