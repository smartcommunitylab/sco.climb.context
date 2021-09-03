<template>
  <v-container class="grey lighten-5">
    <v-row no-gutters>
      <v-col>
        <v-card class="pa-2" outlined tile>
          <div class="pa-7">
            <v-row> <h2 class="font-weight-regular">Classi</h2></v-row>
            <v-row> <v-col cols="12"></v-col></v-row>
            <v-row>
              <h4 class="font-weight-regular">
                Inserendo il numero totale degli alunni verrà generata una lista
                di campi dove potranno essere inseriti i nicknames scelti dagli
                alunni e che servirà per la compilazione quotidiana del diario
                di mobilità. Questa modifica non è obbligatoria e può essere
                fatta a percorso già iniziato.
              </h4>
            </v-row>
          </div>

          <div class="row ma-2">
            <div class="col-9"></div>
            <div class="col-3">
              <v-btn
                class="ma-2"
                height="50px"
                width="200px"
                color="primary"
                @click="addNewClass()"
              >
                Aggiungi classe
              </v-btn>
            </div>
          </div>

          <div class="row pa-4">
            <div
              class="col-5 pa-5"
              v-for="(schoolClass, idx) in schoolClasses"
              v-bind:key="schoolClass"
            >
              <Card-Class
                @removeClassCard="onCardRemoveBtnClick"
                :schoolClass="schoolClass"
                :cardIdx="idx"
              ></Card-Class>
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

    <div class="text-center ma-2">
      <v-snackbar v-model="snackbar">
        {{ snackBarText }}
        <template v-slot:action="{ attrs }">
          <v-btn color="pink" text v-bind="attrs" @click="snackbar = false">
            Chiudi
          </v-btn>
        </template>
      </v-snackbar>
    </div>
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
      snackbar: false,
      snackBarText: "",
      show: false,
      isHidden: true,
      nomepagina: "Class Definition",
      schoolClasses: [this.createCardClassObj()],
    };
  },
  computed: {
    ...mapState("game", ["currentGame"]),
  },
  methods: {
    onCardRemoveBtnClick: function (index) {
      if (this.schoolClasses?.length <= index) {
        return;
      }
      if (this.schoolClasses?.length === 1) {
        this.snackBarText = "Almeno una classe deve essere definita";
        this.snackbar = true;
        return;
      }
      this.schoolClasses.splice(index, 1);
    },
    createCardClassObj() {
      return {
        className: "",
        students: [],
      };
    },
    ...mapActions("game", {
      createClass: "createClass",
    }),
    ...mapActions("navigation", {
      nextStep: "nextStep",
    }),
    addNewClass() {
      this.schoolClasses.push(this.createCardClassObj());
    },
    navigateHome() {
      this.$router.push("home");
    },
    checkFormValidations() {
      let areAllFormValid = true;
      this.schoolClasses.forEach((ele) => {
        if (!ele.form.validate()) {
          areAllFormValid = false;
        }
      });
      return areAllFormValid;
    },
    goNext() {
      const areFormsValid = this.checkFormValidations();
      if (!areFormsValid) {
        this.snackBarText = "Per favore controllare le informazioni immesse";
        this.snackbar = true;
        return;
      }
      let tempArr = this.schoolClasses.map((x) => {
        return { className: x.className, students: x.students };
      });
      this.createClass(tempArr);
      this.$router.push("habitsDefinition");
      this.nextStep();
    },
  },
  mounted() {
    if (this.currentGame && this.currentGame.classDefinition != null) {
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

