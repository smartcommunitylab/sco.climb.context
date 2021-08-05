<template>
  <v-container>
    <v-row class="text-center">
      <v-col col="12">
        <v-breadcrumbs :items="items">
          <template v-slot:item="{ item }">
            <v-breadcrumbs-item :href="item.href" :disabled="item.disabled" >
              <div class="col breadcrumb" :class="item.class">
                {{ item.text.toUpperCase() }}
              </div>
              </v-breadcrumbs-item
            >
          </template>
        </v-breadcrumbs>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mapState } from "vuex";
export default {
  name: "StatusBar",
  computed: {
    ...mapState("navigation", ["page"]),
  },
  created() {},
  data: () => ({
    items: [
      {
        text: "Home",
        disabled: false,
        class: "home",
        href: "home",
      },
      {
        text: "classDefinition",
        disabled: false,
        class: "classDefinition",
        href: "classDefinition",
      },
      {
        text: "habitsDefinition",
        disabled: false,
        class: "habitsDefinition",
        href: "habitsDefinition",
      },
            {
        text: "routesSuggestion",
        disabled: false,
        class: "routesSuggestion",
        href: "routesSuggestion",
      },
    ],
  }),
  methods: {},
};
</script>
<style scoped>
.breadcrumb{
    color: white;

}

.home {
    background-color: lightgreen;
}
.classDefinition {
    background-color: #FF7D60;
}
.habitsDefinition {
    background-color: #8BDCF5;
}
.routesSuggestion {
    background-color: #BDB1FF;
}
</style>
