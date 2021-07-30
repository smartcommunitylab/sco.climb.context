<template>
  <v-container class="grey lighten-5">
    <v-row no-gutters>
      <v-col>
        <v-card class="pa-2" outlined tile>
          <p class="text-center">{{ nomepagina }}</p>
          <div class="align-center">
            <div class="row">
              <h1 class="text-xl">Login</h1>
            </div>
            <div class="row">
              <div>
                <v-img height="100%">
                  <v-row align="end" class="fill-height">
                    <v-col align-self="start" class="pa-0" cols="12">
                      <v-avatar class="profile" color="grey" size="164" tile>
                        <v-img
                          src="https://picsum.photos/id/1005/5760/3840.jpg?hmac=2acSJCOwz9q_dKtDZdSB-OIK1HUcwBeXco_RMMTUgfY"
                        ></v-img>
                      </v-avatar>
                    </v-col>
                    <v-col class="py-0">
                      <v-list-item color="rgba(0, 0, 0, .4)">
                        <v-list-item-content>
                          <v-list-item-title class="text-h6">
                            Francesca Russo
                          </v-list-item-title>
                          <v-list-item-subtitle class="text-h8"
                            >Insegnante</v-list-item-subtitle
                          >
                        </v-list-item-content>
                      </v-list-item>
                    </v-col>
                  </v-row>
                </v-img>
              </div>

              <div>
                <v-list-item-content>
                  <v-list-item-title class="text-h7">
                    Riepilogo informazioni insegnante
                  </v-list-item-title>
                  <v-list-item-subtitle class="text-h8"
                    ><v-icon>{{ territorioIcon }}</v-icon
                    >Territorio: Trentino</v-list-item-subtitle
                  >
                  <v-list-item-subtitle class="text-h8"
                    ><v-icon>{{ istitutoIcon }}</v-icon
                    >Istituto: IC TN6</v-list-item-subtitle
                  >
                  <v-list-item-subtitle class="text-h8"
                    ><v-icon>{{ scuolaIcon }}</v-icon
                    >Scuola: S.Pertini</v-list-item-subtitle
                  >
                </v-list-item-content>
              </div>
            </div>

            <div class="col">
              <h7 class="text-h7"> Crea percorsi </h7>
          <v-btn color="primary" @click="goToClassDefinition()">
            {{ $t("createPath") }}
          </v-btn>
            </div>
          </div>

        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mdiMapMarker ,mdiSchoolOutline, mdiBookEducationOutline } from '@mdi/js';

export default {
  name: "Home",
  data() {
    return {
      nomepagina: "Home",
      territorioIcon: mdiMapMarker, 
      istitutoIcon: mdiBookEducationOutline ,
      scuolaIcon: mdiSchoolOutline,
    };
  },
  methods: {
    goToClassDefinition() {
      this.$router.push("classDefinition");
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
