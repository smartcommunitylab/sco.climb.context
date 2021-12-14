<template>
  <v-container
  :fluid="true"
  class="pa-0 mb-14">

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
              :key="schoolClass.id"
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
              color="blue"
              class="ma-auto"
              @click="addNewClass()">
              <v-icon>mdi-plus</v-icon>
              <span>aggiungi classe</span>
            </v-btn>
          </v-col>
        </v-row>  
      </v-col>        
    </v-row>

    <div class="text-center ma-2">
      <v-snackbar
      v-model="snackbar"
      :timeout="1500"
      color="accent"
      transition="scroll-y-transition">
        {{ snackBarText }}
      </v-snackbar>
    </div>

    <v-btn
      elevation="5"
      rounded
      bottom
      left
      :absolute="true"
      text
      class="imBtnOut"
      @click="goHome()">
      <v-icon small>home</v-icon>
      <span>Torna alla home</span>
    </v-btn>
  
    <v-btn
      elevation="5"
      rounded
      bottom
      right
      :absolute="true"
      text
      class="imBtn"
      @click="goNext()">
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
      schoolClasses: [],
    };
  },
  computed: {
    ...mapState("game", ["currentGame"]),
  },
  methods: {
    ...mapActions("navigation",["changePageByName","nextStep"]),
    ...mapActions("game", {createClass: "createClass"}),
    goHome() {
       this.$router.push("home");
    },
    onCardRemoveBtnClick: function (index) {
      if (this.schoolClasses?.length <= index) {
        return;
      }
      if (this.schoolClasses?.length === 1) {
        this.snackBarText = "Almeno una classe deve essere definita!!!";
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

    addNewClass() {
      this.schoolClasses.push(this.createCardClassObj());
      this.snackBarText = "Nuova classe aggiunta";
      this.snackbar = true;

      // var scrollingElement = (document.scrollingElement || document.body);
      // scrollingElement.scrollTop = scrollingElement.scrollHeight;
      // window.scrollTo(0,document.body.scrollHeight);

  },
    // goHome() {
    //   this.$router.push("home");
    // },
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
      let tempArr = this.schoolClasses.map((classe) => {
        let studentsArray=[];
        for (let k=0;k<Number(classe.classNum);k++){
          classe.students[k]?studentsArray.push(classe.students[k]):studentsArray.push(k+1)
        }
        return { className: classe.className, students: studentsArray,classNum:Number(classe.classNum)};
      });
       this.createClass(tempArr);
        //this.createClass({classes:[{className: "ciao", students: ['1','2'],classNum:2},{className: "ciao", students: ['1','2'],classNum:2}]});
      //this.nextStep();
      // this.$router.push("habitsDefinition");
      this.changePageByName("habitsDefinition");
    },
  },
  mounted() {
    if (this.currentGame && this.currentGame.classDefinition != null) {
      this.schoolClasses = JSON.parse(JSON.stringify(this.currentGame.classDefinition));
    }
    else this.schoolClasses = [this.createCardClassObj()];
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

.plusFab {
  text-align: center;
}
</style>

