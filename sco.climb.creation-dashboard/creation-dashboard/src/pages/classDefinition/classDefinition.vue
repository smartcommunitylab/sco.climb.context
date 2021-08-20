<template>
  <v-container class="grey lighten-5">
    <v-row no-gutters>
      <v-col>
        <v-card class="pa-2" outlined tile>
          <div class="pa-7">
            <p class="text-center text-h3">{{ nomepagina }}</p>
            <hr />
            <h2>Classi</h2>
            <h4>
              Inserendo il numero totale degli alunni verrà generata una lista
              di campi dove potranno essere inseriti i nicknames scelti dagli
              alunni e che servirà per la compilazione quotidiana del diario di
              mobilità. Questa modifica non è obbligatoria e può essere fatta a
              percorso già iniziato.
              <v-tooltip v-model="show" right
                ><template v-slot:activator="{ on, attrs }">
                  <v-btn icon v-bind="attrs" v-on="on">
                    <v-icon color="grey lighten-1"> mdi-open-in-new </v-icon>
                  </v-btn>
                </template>
                <span>See more</span>
              </v-tooltip>
            </h4>
          </div>

          <div class="row ma-2">
            <div class="col-9"></div>
            <v-btn
              class="ma-2"
              height="50px"
              width="200px"
              color="primary"
              @click="addNewClass"
            >
              Add another class
            </v-btn>
          </div>

          <div class="row">
            <div class="col-sm-1"></div>
            <div
              class="col-5"
              v-for="schoolClass in schoolClasses"
              v-bind:key="schoolClass"
            >
              <Card-Class :schoolClass="schoolClass"></Card-Class>
              <div class="col-sm-1"></div>
            </div>
          </div>

          <div class="row py-6">
            <div class="col-sm-1"></div>
            <div class="col-sm-5">
              <v-btn class="float-left" @click="navigateHome()">Indietro</v-btn>
            </div>
            <div class="col-sm-5">
              <v-btn class="float-right" @click="goNext()">Avanti</v-btn>
            </div>
            <div class="col-sm-1"></div>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mapState, mapActions } from "vuex";
import CardClass from "@/components/Card-Class.vue";
export default {
  name: "classDefinition",
  components: {
    "Card-Class": CardClass,
  },
  data() {
    return {
      expand: false,
      expand2: false,
      isHidden: true,
      nomepagina: "Class Definition",
      schoolClasses: [
        {
          className: "",
          studentsNum: 1,
          students: [],
        },
      ],
      // studentsNum: 5,
      // students: [],
    };
  },
  computed: {
    ...mapState("game", ["currentGame"]),
  },
  methods: {
    ...mapActions("game", {
      createClass: "createClass",
    }),
    ...mapActions("navigation", {
      nextStep: "nextStep",
    }),
    addNewClass() {
      this.schoolClasses.push({
        className: "",
        studentsNum: 1,
        students: [],
      });
    },
    navigateHome() {
      this.$router.push("home");
    },
    goNext() {
      this.$router.push("habitsDefinition");
      this.createClass(this.schoolClasses);
      this.nextStep();
    },
  },
  mounted() {
    if (this.currentGame.classDefinition != null) {
      this.schoolClasses = this.currentGame.classDefinition;
    }
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
.c-input-field {
  padding: 0 !important;
  margin: 0 !important;
}
.c-card-layout {
  background: #f4f2f2;
  border-radius: 8px;
}
</style>

