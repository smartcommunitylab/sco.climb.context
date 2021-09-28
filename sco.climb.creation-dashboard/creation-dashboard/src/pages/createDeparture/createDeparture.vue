<template>
  <div>
    <stepper></stepper>
  <v-container style="height: 1000px" class="grey lighten-5"> </v-container>
  </div>
</template>

<script>
import Stepper from "@/components/Stepper.vue";
export default {
  name: "createdeparture",
  components: {
    Stepper,
  },
  data() {
    return {
      nomepagina: "createdeparture",
    };
  },
};
</script>

<style></style>

