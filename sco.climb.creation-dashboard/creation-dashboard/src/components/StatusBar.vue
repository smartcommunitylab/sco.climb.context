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
        text: "Login",
        disabled: false,
        class: "login",
        href: "login",
      },
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
    ],
  }),
  methods: {},
};
</script>
<style scoped>
.breadcrumb{
    color: white;

}
.login {
    background-color: aqua;
}
.home {
    background-color: red;
}
.classDefinition {
    background-color: brown;
}
</style>
