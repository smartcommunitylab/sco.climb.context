<template>
  <v-container style="height: 1000px" class="grey lighten-5">
    <v-row no-gutters>
      <v-col cols="12" sm="12">
        <v-card class="pa-2" outlined tile>
          <div class="pa-7">
            <p class="text-center">{{ nomepagina }}</p>
            <hr />
            <h2>Riepilogo</h2>
            <h4>Percorso creato: Il giro del mediterraneo</h4>
            <v-col cols="1"></v-col>
            <v-col cols="1"></v-col>

            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="5"><h4>Numero Tappe</h4> </v-col
              ><v-col cols="5">18 </v-col></v-row
            >

            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="5"><h4>Data di inizio</h4> </v-col
              ><v-col cols="5">01/03/2021 </v-col></v-row
            >

            <v-row
              ><v-col cols="1"></v-col
              ><v-col cols="5"><h4>Data di fine</h4> </v-col
              ><v-col cols="5">31/05/2021 </v-col></v-row
            >

            <v-row
              ><v-col cols="1"></v-col><v-col cols="5"><h4>Classi</h4> </v-col
              ><v-col cols="5">2B </v-col></v-row
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
              ><v-col cols="6">
                <v-icon left>mdi-exclamation </v-icon>
                Il percorso verrà validato da Kids Go Green dopo averlo inviato
              </v-col></v-row
            >

            <div class="row py-6">
              <div class="col-sm-1"></div>
              <div class="col-sm-5">
                <v-btn class="float-left" @click="goToRouteSuggestion()"
                  >Indietro</v-btn
                >
              </div>
              <div class="col-sm-5">
                <v-btn class="float-right" @click="goToHome()">Salva</v-btn>
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
  name: "summary",
  data() {
    return {
      nomepagina: "summary",
    };
  },
  methods: {
    goToHome() {
      this.$router.push("home");
    },
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

