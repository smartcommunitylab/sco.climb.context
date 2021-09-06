<template>
  <v-container style="height: 1000px" class="grey lighten-5">
    <v-row no-gutters>
      <v-col cols="12" sm="12">
        <v-card class="pa-2" outlined tile>
          <div class="pa-7">
            <p class="text-center">{{ nomepagina }}</p>
            <hr />
            <h2>Percorso personalizzabile</h2>

            <v-col cols="1"></v-col>
            <v-col cols="1"></v-col>
            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="5"><h4>Nome percorso</h4> </v-col
              ><v-col cols="5">Il giro del mediterraneo </v-col></v-row
            >

            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="5"><h4>Numero Tappe</h4> </v-col
              ><v-col cols="5">18 </v-col></v-row
            >
            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="5"><h4>Descrizione</h4> </v-col
              ><v-col cols="5"
                >Un viaggio alla scoperta del Mediterraneo
              </v-col></v-row
            >
            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="5"><h4>Data di arrivo stimata</h4> </v-col
              ><v-col cols="5">19/05/2021 </v-col></v-row
            >

            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="9"
                ><div id="preview" class="container-image">
                  <img
                    class="uploading-image"
                    src="https://i.picsum.photos/id/1040/4496/3000.jpg?hmac=kvZONlBpTcZ16PuE_g2RWxlicQ5JKVq2lqqZndfafBY"
                  />
                </div>
                <div class="container-image"></div> </v-col
            ></v-row>

            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="6"><h4>Tappe</h4> </v-col></v-row
            >

            <!-- v-for mancante, per ogni tappa -->

            <v-row
              ><v-col cols="1"></v-col>
              <v-col cols="4" class="legcontainer">1. Cadine</v-col
              ><v-col cols="4" class="legcontainer">
                <v-row
                  ><v-col cols="11" class="legcontainer"
                    >Elementi Multimediali:1</v-col
                  >
                  <v-col cols="11" class="legcontainer"
                    >Distanza tappa: 100 km</v-col
                  ></v-row
                >
                <v-icon color="black"> mdi-open-in-new </v-icon>
              </v-col>
            </v-row>

            <div class="row py-6">
              <div class="col-sm-1"></div>
              <div class="col-sm-5">
                <v-btn class="float-left" @click="goToRouteSuggestion()"
                  >Indietro</v-btn
                >
              </div>
              <div class="col-sm-5">
                <v-btn class="float-right" @click="goToSummary()"
                  >Adotta e Personalizza</v-btn
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
export default {
  name: "routePersonalization",

  data() {
    return {
      nomepagina: "routePersonalization",
      props: {
        percorso: Object,
      },
    };
  },

  methods: {
    goToSummary() {
      this.$router.push("summary");
      this.nextStep();
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
.legcontainer {
  background: #ccc;
  border: 1px solid #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  max-height: 350px;
  max-width: 900px;
}
</style>

