<template>
  <v-container :fluid="true" class="pa-0 mb-14">

      <!-- <div class="text-center ma-2">
      <v-snackbar
        v-model="snackbar"
        :timeout="2000"
        transition="scroll-y-reverse-transition"
      >
        {{ snackBarText }}
        <template v-slot:action="{ attrs }">
          <v-btn color="pink" text v-bind="attrs" @click="snackbar = false">
            Chiudi
          </v-btn>
        </template>
      </v-snackbar>
    </div> -->
    <v-form ref="form">
      <v-row class="mt-2">
        <v-col offset="2" cols="8">
          <v-row>
            <v-col cols="12" align-self="center" class="pa-0 ml-0">
              <div class="d-inline-block">
                <p class="titleFont" v-html="$t('habitsDefinition.title2')"></p>
                <p v-html="$t('habitsDefinition.description2')"></p>
              </div>
            </v-col>
          </v-row>
        </v-col>
      </v-row>

      <v-row class="mt-2">
        <v-col offset="2" cols="8">
          <v-row>
            <v-col
              cols="12"
              class="pa-0 ml-0"
              v-for="(mean, index) in habitsData.diaryMeans"
              :key="index"
            >
              <div class="pa-0 ma-0">
                <v-checkbox
                class="pa-0 ma-0"
                  @change="meansChange(index)"
                  v-model="mean.checked"
                  :label="mean.label"
                ></v-checkbox>
              </div>
            </v-col>
          </v-row>
        </v-col>
      </v-row>

      <v-row class="mt-10">
        <v-col offset="2" cols="8">
          <v-row>
            <v-col cols="12" align-self="center" class="pa-0 ml-0">
              <div class="d-inline-block">
                <p class="titleFont" v-html="$t('habitsDefinition.title1')"></p>
                <p v-html="$t('habitsDefinition.description1')"></p>
              </div>
              <!-- <div v-for="(gameclass,indexClass) in currentGame.classDefinition" :key="indexClass" >
                  {{className}}
                  <span v-for="(student,studentIndex) in currentGame.classDefinition[indexClass]" :key="studentIndex">
                    {{student}}
                  </span>
                </div> -->
            </v-col>
          </v-row>
        </v-col>
      </v-row>

      <v-row>
        <v-col offset="2" cols="8" class="px-0">
          <v-row>
            <v-col cols="12" align-self="center" class="px-0 pb-0 mb-0">
              <v-simple-table>
                <template v-slot:default>
                  <thead>
                    <tr>
                      <th class="text-left"></th>
                      <th
                        class="text-center size"
                        v-for="(mean, index) in habitsData.diaryMeans"
                        :key="index"
                        v-show="mean.checked"
                      >
                        {{ mean.label }}
                      </th>
                      <th class="text-center">TOTALE ALUNNI</th>
                    </tr>
                  </thead>

                  <tbody>
                    <tr
                      v-for="(gameclass, indexClass) in currentGame.classDefinition"
                      :key="indexClass"
                    >
                     <td>{{ gameclass.className }}</td>
                      <td v-for="(mean, index) in habitsData.diaryMeans"
                        :key="index"
                        v-show="mean.checked"
                        >
                        <v-text-field
                          placeholder="0"
                          class="inputField"
                          @input="onActivityValueChange(indexClass)"
                          v-model="habitsData.habits[indexClass][index].numStud"
                          type="number"
                        ></v-text-field>
                      </td>
                      <!-- <td>
                        <v-text-field
                          placeholder="0"
                          class="inputField"
                          @input="onActivityValueChange(indexClass)"
                          v-model="habitsData.habits[indexClass][1].numStud"
                          type="number"
                        ></v-text-field>
                      </td>
                      <td>
                        <v-text-field
                          placeholder="0"
                          @input="onActivityValueChange(indexClass)"
                          v-model="habitsData.habits[indexClass][2].numStud"
                          type="number"
                        >
                        </v-text-field>
                      </td>
                      <td>
                        <v-text-field
                          placeholder="0"
                          @input="onActivityValueChange(indexClass)"
                          v-model="habitsData.habits[indexClass][3].numStud"
                          type="number"
                        ></v-text-field>
                      </td>
                      <td>
                        <v-text-field
                          placeholder="0"
                          @input="onActivityValueChange(indexClass)"
                          v-model="habitsData.habits[indexClass][4].numStud"
                          type="number"
                        ></v-text-field>
                      </td>
                      <td>
                        <v-text-field
                          placeholder="0"
                          @input="onActivityValueChange(indexClass)"
                          v-model="habitsData.habits[indexClass][5].numStud"
                          type="number"
                        ></v-text-field>
                      </td>
                      <td>
                        <v-text-field
                          placeholder="0"
                          @input="onActivityValueChange(indexClass)"
                          v-model="habitsData.habits[indexClass][6].numStud"
                          type="number"
                        ></v-text-field>
                      </td> -->
                      <td>
                        <span
                          :class="{
                            'red--text':
                              !isInputsDataValid[indexClass] &&
                              formMetadata.totStudentsInserted[indexClass] != 0,
                          }"
                        >
                          {{ formMetadata.totStudentsInserted[indexClass] }}/{{
                            gameclass.classNum
                          }}
                        </span>
                      </td>
                    </tr>
                  </tbody>
                </template>
              </v-simple-table>
            </v-col>
          </v-row>
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

      <v-btn
        elevation="5"
        color="primary"
        rounded
        bottom
        left
        :absolute="true"
        text
        class="imBtnOut"
        @click="goToClassDefinition()"
      >
        <v-icon small>mdi-chevron-left</v-icon>
        <span>Indietro</span>
      </v-btn>

      <v-btn
        elevation="5"
        color="secondary"
        rounded
        bottom
        right
        :absolute="true"
        text
        class="imBtn"
        @click="goNext()"
      >
        <span>Salva e prosegui</span>
        <v-icon>mdi-chevron-right</v-icon>
      </v-btn>
    </v-form>
  </v-container>
</template>

<script>
import { mapState, mapActions } from "vuex";
import { DIARY_MEANS } from "../../variables";
export default {
  name: "habitsDefinition",
  components: {},
  data() {
    return {
      isInputsDataValid: [],
      snackbar: false,
      snackBarText: "",
      habitsData: {
        habits: [],
        diaryMeans: [],
        saturdaySchool: "no",
        roundtrip: "no",
        pickerStart: 0,
        pickerEnd: 0,
        diffDays: 0,
      },
      row: null,
      modalStart: false,
      // isAtLeastOneCheckboxChecked: false,
      modalEnd: false,
      valid: true,
      formMetadata: { totStudentsInserted: [] },
      // totalChilloMeters: 0
      nomepagina: "habitsDefinition",
      show2: false,
      show: true,
    };
  },
  computed: {
    ...mapState("game", ["currentGame"]),
    totHabits() {
      let sum = 0;
      for (let i = 0; i < this.habitsData.habits.length; i++) {
        sum = sum + this.habitsData.habits[i].chilometri;
      }
      return sum;
    },
  },
  methods: {
    ...mapActions("navigation", ["prevStep", "nextStep"]),
    initHabitsArray: function () {
      for (var j = 0; j < this.currentGame.classDefinition.length; j++) {
        this.formMetadata.totStudentsInserted[j] = 0;
      }
      if (this.currentGame.habitsDefinition == null) {
        for (var i = 0; i < this.currentGame.classDefinition.length; i++) {
          var habitsClass = [];
          DIARY_MEANS.forEach((mean) => {
            if (mean.active) {
              habitsClass.push({
                name: mean.value,
                numStud: "",
              });
            }
          });

          this.habitsData.habits.push(habitsClass);
          this.isInputsDataValid.push(false);
        }
        // for (var i = 0; i < this.currentGame..length; i++) {
        // }
        DIARY_MEANS.forEach((mean) => {
          if (mean.active) {
            this.habitsData.diaryMeans.push({
              label: mean.label,
              value: mean.value,
              checked: true,
            });
          }
        });
        this.habitsData.pickerStart = new Date(
          Date.now() - new Date().getTimezoneOffset() * 60000
        )
          .toISOString()
          .substr(0, 10);
        this.habitsData.pickerEnd = new Date(
          Date.now() - new Date().getTimezoneOffset() * 60000
        )
          .toISOString()
          .substr(0, 10);
        this.habitsData.diffDays = 0;
        this.habitsData.saturdaySchool = "no";
        this.habitsData.roundtrip = "no";
      } else {
        this.habitsData = this.currentGame.habitsDefinition;
        //TODO calcolare il totale bambini
      }
    },

    ...mapActions("game", { createHabits: "createHabits" }),
    ...mapActions("navigation", { nextStep: "nextStep" }),
    onActivityValueChange(index) {
      if (this.habitsData == null || this.habitsData.habits[index] == null) return;
      this.isInputsDataValid[index] = false;
      let totNumStud = 0;
      // this.isAtLeastOneCheckboxChecked = false;
      // let totChillo = 0;
      this.habitsData.habits[index].forEach((habit) => {
        totNumStud += +habit.numStud;
        // totChillo += +habit.chilometri;
        // if (habit.checked) {
        //   this.isAtLeastOneCheckboxChecked = true;
        // }
      });
      this.formMetadata.totStudentsInserted[index] = totNumStud;
      // this.formMetadata.totalChilloMeters = totChillo;
      // const totalStudents = this.getTotalStudents();
      if (totNumStud == this.currentGame.classDefinition[index].students.length) {
        this.isInputsDataValid[index] = true;
      }
    },
    meansChange(index) {
      console.log(index);
    },
    getTotalStudents() {
      let num = 0;
      if (this.currentGame?.classDefinition?.length > 0) {
        this.currentGame.classDefinition.forEach((studArr) => {
          if (studArr?.students?.length > 0) {
            num += studArr.students.length;
          }
        });
      }
      return num;
    },
    getDays() {
      const date1 = new Date(this.habitsData.pickerStart);
      const date2 = new Date(this.habitsData.pickerEnd);
      const diffTime = date2 - date1;
      this.habitsData.diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return this.habitsData.diffDays;
    },
    goNext() {
      if (
        this.isInputsDataValid.every((ele) => ele === true) &&
        this.habitsData.diaryMeans.some((ele) => ele.checked === true)
      ) {
        this.$refs.form.validate();
        this.createHabits(this.habitsData);
        this.nextStep();
      } else if (this.isInputsDataValid.some((ele) => ele != true)) {
        this.snackBarText =
          "Per favore, controllare le informazioni immesse nella prima sezione di abitudini";
        this.snackbar = true;
      } else if (this.habitsData.diaryMeans.every((ele) => ele.checked != true)) {
        this.snackBarText =
          "Per favore, controllare che le caselle di controllo segnate siano almeno una";
        this.snackbar = true;
      }
    },
    goToClassDefinition() {
      this.prevStep();
      //this.$router.push("classDefinition");
    },
  },
  created() {
    this.initHabitsArray();
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
.checkboxstyle {
  align-items: center;
  justify-content: center;
  text-align: center;
}
.checkbox {
  float: left;
}
.col-sm-2 {
  padding: 8px !important;
}
.inputField {
  text-align: center;
  min-width: 60%;
}
.size{
  font-size: 15px!important;
}
</style>
