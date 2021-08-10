<template>
  <v-container style="height: 1000px" class="grey lighten-5">
    <v-row no-gutters>
      <v-col cols="12" sm="12">
        <v-card class="pa-2" outlined tile>
          <div class="pa-7">
            <p class="text-center">{{ nomepagina }}</p>
            <hr />
            <h2>Creazione Percorso</h2>
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
    };
  },

  methods: {},

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



