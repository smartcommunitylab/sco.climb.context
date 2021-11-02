<template>
  <div>
    <!-- <v-row>
      <v-col cols="10"></v-col>
      <v-col cols="1">
        <v-tooltip top>
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              icon
              color="black"
              v-bind="attrs"
              v-on="on"
              @click="onDeleteBtnClick(schoolClass)"
            >
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </template>
          <span>Permette di eliminare la scheda corrente</span>
        </v-tooltip>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col><v-text-field
            v-model="schoolClass.className"
            :label="$t('class')"
            :rules="classNameRules"
            required
            outlined
            dense
          ></v-text-field></v-col>
          </v-row>
          
          <v-row>
            <v-col cols="10">
              Nicknames
              <v-tooltip v-model="show" right @click="show = !show"
                ><template v-slot:activator="{ on, attrs }">
                  <v-btn icon v-bind="attrs" v-on="on">
                    <v-icon color="grey lighten-1">
                      mdi-help-circle-outline
                    </v-icon>
                  </v-btn>
                </template>
                <span
                  >Ogni numero è associato al nickname di uno studente e non è
                  modificabile</span
                >
              </v-tooltip>
            </v-col>

            <v-col cols="2">
              <v-btn
                class="mx-2"
                v-if="
                  schoolClass == null ||
                  schoolClass.students == null ||
                  schoolClass.students.length == 0
                "
                color="indigo"
                rounded
                small
                @click="addStudent()"
              >
                <v-icon color="white"> mdi-plus </v-icon>
              </v-btn>
            </v-col>
          </v-row>
          <v-row
            class="ma-0"
            v-for="(student, index) in schoolClass.students"
            :key="student.id"
          >
            <v-col cols="2" align="center">
              <label align-item="center">{{ index + 1 }}</label>
            </v-col>
            <v-col cols="8" class="pa-0">
              <v-text-field
                :label="$t('nickname')"
                v-model="student.inputVal"
                :rules="nicknameRules"
                required
                outlined
                dense
              ></v-text-field>
            </v-col>
            <v-col cols="2">
              <v-btn
                class="mx-2"
                color="primary"
                rounded
                small
                @click="deleteStudent()"
              >
                <v-icon color="white"> mdi-delete </v-icon>
              </v-btn>
            </v-col>
          </v-row>
        </v-form>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="10"></v-col>
      <v-col cols="2" class="pl-0">
        <v-btn
          class="mx-2"
          v-if="
            schoolClass &&
            schoolClass.students &&
            schoolClass.students.length > 0
          "
          color="indigo"
          rounded
          small
          @click="addStudent()"
        >
          <v-icon color="white"> mdi-plus </v-icon>
        </v-btn>
      </v-col>
    </v-row> -->

    <v-card class="rounded-lg px-3 pt-2 ma-0"  width="100%" elevation="3"
    >
            <v-form ref="form" lazy-validation class="pt-2 px-4">

      <v-row class="px-0">
      <v-col cols="11" class="px-0"></v-col>
      <v-col cols="1" class="px-0">
        <v-tooltip top>
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              icon
              absolute
              right
              color="alert"
              v-bind="attrs"
              v-on="on"
              @click="onDeleteBtnClick(schoolClass)"
            >
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </template>
          <span>Permette di eliminare la scheda corrente</span>
        </v-tooltip>
      </v-col>
    </v-row>
<!-- FORM CARD -->
    <!-- <v-row class="pt-2 px-4">
      <v-col cols="12"> -->
          <v-row>
<!-- FIELD CLASSE -->
            <v-col cols="1" class="pl-0 text">
              <span class="tinyTitle"
              v-html="$t('cardClass.promptClass')">
              </span>
            </v-col>
            <v-col cols="3">
              <v-text-field
                class="mr-5"
                v-model="schoolClass.className"
                :placeholder="$t('cardClass.inputClass')"
                :rules="classNameRules"
                required
                dense
              ></v-text-field>
            </v-col>
<!-- FIELD TOTALE ALUNNI -->
            <v-col cols="2" class="text">
              <span class="tinyTitle"
              v-html="$t('cardClass.promptNum')">
              </span>
            </v-col>
            <v-col cols="4" class="mb-2">
              <v-text-field
                v-model="schoolClass.classNum"
                :placeholder="$t('cardClass.inputNum')"
                :rules="classNumRules"
                type="number"
                required
                dense
              ></v-text-field>    
            </v-col>
          </v-row>
<!-- BOTTONE CARD PER ESPLODERE NICKNAME  -->
          <!-- <v-row class="mt-0">
            <v-col class="py-0 pr-0"> -->
              <v-card-actions >
                <v-spacer></v-spacer>

                <v-btn
                  top
                  text
                  rounded
                  color="primary"
                  @click="show = !show"
                >
                Aggiungi nickname
                  <v-icon>{{ show ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
                </v-btn>
              </v-card-actions>
            <!-- </v-col>  
          </v-row>     -->
          <!-- </v-col>
    </v-row> -->


    
    <v-expand-transition>
      <div v-if="show">
        <v-card-text>
          <template v-if="schoolClass.classNum>0">
            <!-- <v-form ref="formLong" lazy-validation> -->
            <v-row>
              <v-col cols="3" v-for="(alumn,index) in Number(schoolClass.classNum)" :key="index">
                <v-text-field
                v-model="schoolClass.students[index]"
                :prefix="String(index+1)"
                :placeholder="$t('cardClass.inputNickname')"
                :rules="nicknameRules">alunno</v-text-field>
              </v-col>
            </v-row>
            <!-- </v-form> -->
          </template>
          <template v-else>
            <div>
              Inserisci sopra il numero di alunni per cominciare a definire i nickname.
            </div>
          </template>
        </v-card-text>
      </div>
    </v-expand-transition>
            </v-form>

    </v-card>
  </div>
</template>

<script>
import { mapState } from "vuex";
export default {
  data() {
    return {
      show: false,
      classNameRules: [
        (v) => !!v || "Il nome della classe è richiesto!",
        (v) =>
          (v && v.length >= 2) ||
          "Il nome della classe deve essere lungo più di 1 carattere",
      ],
      classNumRules: [
        (v) => !!v || "Il numero totale degli alunni è richiesto!",
        // (v) => (v && !isNaN(v))|| "Il valore deve essere un numero!",
        (v) => (v > 0)|| "Il totale deve essere un numero positivo maggiore di zero",
        (v) =>
          (v && String(v).length <= 3) ||
          "Il numero totale degli alunni non può essere più lungo di 3 cifre",
      ],
      nicknameRules: [
        (v) => (!!v && /\S/.test(v))|| "Il nickname non può essere uno spazio vuoto!",

      ],
    };
  },
  namespaced: true,
  props: {
    schoolClass: Object,
    cardIdx: Number,
  },
  computed: {
    ...mapState("game", ["currentGame"]),
  },

  methods: {
    onDeleteBtnClick() {
      this.$emit("removeClassCard", this.cardIdx);
    },
    // deleteStudent(index) {
    //   this.schoolClass.students.splice(index, 1);
    // },
    // addStudent() {
    //   this.schoolClass.students.push({
    //     inputVal: "",
    //     id: this.schoolClass.students.length,
    //   });
    // },
    // updateStudentsFields(num) {
    //   for (let i = 0; i < num; i++) {
    //     this.schoolClass.students.push({ inputVal: "", id: i });
    //   }
    // },
  },
  mounted() {
    if (this.schoolClass) {
      this.schoolClass.form = this.$refs.form;
      window.scrollTo(0,document.body.scrollHeight);
    }
  },
};
</script>

<style>
/* .c-card-layout {
  background: #f8f8f8;
  border-radius: 8px;
} */

.text {
  padding-top: 2.6%;
}
</style>