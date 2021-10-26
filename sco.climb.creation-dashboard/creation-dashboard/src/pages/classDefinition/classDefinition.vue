<template>
  <v-container 
  :fluid="true"
  class="pa-0">

    <!-- <v-row no-gutters>
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
              v-bind:key="schoolClass.id"
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
              <v-btn class="float-left" @click="navigateHome()" color="primary">Indietro</v-btn>
            </div>
            <div class="col-sm-5">
              <v-btn class="float-right" @click="goNext()" color="primary">Avanti</v-btn>
            </div>
            <div class="col-sm-1"></div>
          </div>
        </v-card>
      </v-col>
    </v-row> -->

    <v-row class="mt-2">
      <v-col offset="2" cols="8">
        <v-row>
          <v-col cols="12" align-self="center" class="pa-0 ml-0">
            <div class="d-inline-block">
              <p class="titleFont"
                v-html="$t('classDefinition.title')">
              </p>
              <p
                v-html="$t('classDefinition.description')">
              </p>
            </div>
          </v-col>
        </v-row>  
      </v-col>        
    </v-row>

    <v-row>
      <v-col offset="2" cols="8" class="px-0">
        <v-row>
          <v-col cols="12" align-self="center" class="px-0 pb-0 mb-0">
          <div class="row pa-4">
            <div
              class="col-12 pb-2"
              v-for="(schoolClass, idx) in schoolClasses"
              v-bind:key="schoolClass.id"
            >
              <Card-Class
                @removeClassCard="onCardRemoveBtnClick"
                :schoolClass="schoolClass"
                :cardIdx="idx"
              ></Card-Class>
            </div>
          </div>
          </v-col>
        </v-row>
        <v-row>
          <v-col class="plusFab">
            <v-btn
              color="primary"
              class="ma-auto"
              @click="addNewClass()">
              <v-icon>mdi-plus</v-icon>
              <!-- <span>aggiungi classe</span> -->
            </v-btn>
          </v-col>
        </v-row>  
      </v-col>        
    </v-row>

    <div class="text-center ma-2">
      <v-snackbar
      v-model="snackbar"
      :timeout="2000">
        {{ snackBarText }}
        <template v-slot:action="{ attrs }">
          <v-btn color="pink" text v-bind="attrs" @click="snackbar = false">
            Chiudi
          </v-btn>
        </template>
      </v-snackbar>
    </div>

      <v-btn
        color="primary"
        outlined
        rounded
        bottom
        left
        :absolute="true"
        text
        class="homeStep"
        @click="goHome()">
        <v-icon small>home</v-icon>
        <span>Torna alla home</span>
      </v-btn>
    
      <v-btn
        color="secondary"
        rounded
        bottom
        right
        :absolute="true"
        text
        class="imFab"
        @click="goOnHab()">
        <span>Salva e prosegui</span>
        <v-icon>mdi-chevron-right</v-icon>
      </v-btn>

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
    goHome() {
      this.$router.push("home");
    },
    goOnHab() {
      this.$router.push("habitsDefinition");
    },
    onCardRemoveBtnClick: function (index) {
      if (this.schoolClasses?.length <= index) {
        return;
      }
      if (this.schoolClasses?.length === 1) {
        this.snackBarText = "Almeno una classe deve essere definita";
        this.snackbar = true;
        return;
      }
      this.snackBarText = "Classe rimossa con successo!";
      this.snackbar = true;
      this.schoolClasses.splice(index, 1);
    },
    createCardClassObj() {
      return {
        id:this.schoolClasses?this.schoolClasses.length:0,
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
.homeStep {
  bottom: 0;
  position: fixed;
  margin: 0 0 16px 16px;
}
.plusFab {
  text-align: center;
}
</style>

