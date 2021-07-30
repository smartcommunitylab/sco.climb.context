<template>
  <v-container class="grey lighten-5">
    <v-row no-gutters>
      <v-col>
        <v-card class="pa-2" outlined tile>
          <p class="text-center">{{nomepagina}}</p>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: "classDefinition",
  data() {
      return {
          nomepagina:"classDefinition"
      }
  },
  mounted() {
          let loader = this.$loading.show({
        canCancel: false,
        backgroundColor: "#000",
        color: "#fff",
      });
      setTimeout(() => {loader.hide()},500);
  },
};
</script>

<style></style>

