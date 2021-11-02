<template>
  <v-container :fluid="true" class="pa-0">
    <v-row class="mt-2">
      <v-col offset="2" cols="8">
        <v-row>
          <v-col cols="12" align-self="center" class="pa-0 ml-0">
            <div class="d-inline-block">
              <p class="titleFont" v-html="$t('habitsDefinition.title')"></p>
              <p v-html="$t('habitsDefinition.description')"></p>
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
                    <th class="text-center">In bici</th>
                    <th class="text-center">A piedi</th>
                    <th class="text-center">Trasporto pubblico</th>
                    <th class="text-center">Pedibus</th>
                    <th class="text-center">In auto fino alla piazzola</th>
                    <th class="text-center">Car pooling</th>
                    <th class="text-center">In auto fino a scuola</th>
                    <th class="text-center">TOTALE ALUNNI</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(gameclass, indexClass) in currentGame.classDefinition"
                    :key="indexClass"
                  >
                    <td>{{ gameclass.className }}</td>
                    <td>
                      <v-text-field
                        placeholder="0"
                        class="inputField"
                        @input="onActivityValueChange(indexClass)"
                        v-model="habitsData.habits[indexClass][0].numStud"
                        type="number"
                      ></v-text-field>
                    </td>
                    <td>
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
                    </td>
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
    </div>

    <v-btn
      color="primary"
      outlined
      rounded
      bottom
      left
      :absolute="true"
      text
      class="goBack"
      @click="goToClassDefinition()"
    >
      <v-icon small>mdi-chevron-left</v-icon>
      <span>Indietro</span>
    </v-btn>

    <v-btn
      color="secondary"
      rounded
      bottom
      right
      :absolute="true"
      text
      class="imFab"
      @click="goOnHab()"
    >
      <span>Salva e prosegui</span>
      <v-icon>mdi-chevron-right</v-icon>
    </v-btn>
    <div class="pa-7">
      <h2 class="font-weight-regular">
        Mezzi di trasporto da visualizzare nel diario giornaliero
      </h2>
      <h4 class="font-weight-regular">
        Spuntare tutte le abitudini di mobilità che si vogliono avere disponibili per
        guadagnare i chilometri sostenibili che facciano avanzare nel percorso Kids Go
        Green. E’ possibile inserire mezzi che al momento non sono ancora utilizzati da
        alcun alunno, ma che si prevede possano interessare in futuro.
      </h4>
    </div>

    <div
      class="align-center text-center"
      v-if="habitsData && habitsData && habitsData.diaryMeans"
    >
      <v-row>
        <!-- <div class="col-sm-2"></div>
        <div class="col-sm-2 pa-1" style="align-self: center"></div> -->
        <div class="col-sm-6 m-auto " v-for="(mean, index) in habitsData.diaryMeans" :key="index">
          <v-checkbox
            @change="onActivityValueChange()"
            v-model="mean.checked"
            :label="mean.label"
          ></v-checkbox>
        </div>
      </v-row>
      <!-- <v-row>
        <div class="col-sm-2"></div>
        <div class="col-sm-2 pa-1" style="align-self: center">In bici</div>
        <div class="col-sm-1 pa-1">
          <v-checkbox
            @change="onActivityValueChange()"
            v-model="habitsData.habits[0].checked"
          ></v-checkbox>
        </div>

        <div class="col-sm-1"></div>
        <div class="col-sm-2 pa-1" style="align-self: center">A piedi</div>
        <div class="col-sm-1 pa-1 checkbox">
          <v-checkbox
            @change="onActivityValueChange()"
            v-model="habitsData.habits[1].checked"
          ></v-checkbox>
        </div>
      </v-row>

      <v-row>
        <div class="col-sm-2"></div>
        <div class="col-sm-2 pa-1" style="align-self: center">
          Scuolabus o trasporto pubblico
        </div>
        <div class="col-sm-1 pa-1">
          <v-checkbox
            @change="onActivityValueChange()"
            v-model="habitsData.habits[2].checked"
          ></v-checkbox>
        </div>

        <div class="col-sm-1"></div>
        <div class="col-sm-2 pa-1" style="align-self: center">Pedibus</div>
        <div class="col-sm-1 pa-1">
          <v-checkbox
            @change="onActivityValueChange()"
            v-model="habitsData.habits[3].checked"
          ></v-checkbox>
        </div>
      </v-row>

      <v-row>
        <div class="col-sm-2"></div>
        <div class="col-sm-2 pa-1" style="align-self: center">
          In auto fino alla piazzola di sosta
        </div>
        <div class="col-sm-1 pa-1">
          <v-checkbox
            @change="onActivityValueChange()"
            v-model="habitsData.habits[4].checked"
          ></v-checkbox>
        </div>

        <div class="col-sm-1"></div>
        <div class="col-sm-2 pa-1" style="align-self: center">Car pooling</div>

        <div class="col-sm-1 pa-1">
          <v-checkbox
            @change="onActivityValueChange()"
            v-model="habitsData.habits[5].checked"
          ></v-checkbox>
        </div>
      </v-row>

      <v-row>
        <div class="col-sm-2"></div>
        <div class="col-sm-2 pa-1" style="align-self: center">In auto fino a scuola</div>
        <div class="col-sm-1 pa-1">
          <v-checkbox
            @change="onActivityValueChange()"
            v-model="habitsData.habits[6].checked"
          ></v-checkbox>
        </div>
      </v-row> -->
    </div>

    <div class="pa-7">
      <h2 class="font-weight-regular">Calibrazione del percorso</h2>
      <div class="col-sm-12"></div>

      <v-row>
        <v-col cols="6">
          <v-dialog
            ref="dialog"
            v-model="modalStart"
            :return-value.sync="habitsData.pickerStart"
            persistent
            width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="habitsData.pickerStart"
                :label="$t('dateStart')"
                prepend-icon="mdi-calendar"
                readonly
                v-bind="attrs"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker v-model="habitsData.pickerStart" scrollable>
              <v-spacer></v-spacer>
              <v-btn text color="primary" @click="modalStart = false"> Cancella </v-btn>
              <v-btn
                text
                color="primary"
                @click="$refs.dialog.save(habitsData.pickerStart)"
              >
                OK
              </v-btn>
            </v-date-picker>
          </v-dialog>
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="6">
          <v-dialog
            ref="dialogEnd"
            v-model="modalEnd"
            :return-value.sync="habitsData.pickerEnd"
            persistent
            width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="habitsData.pickerEnd"
                :label="$t('dateEnd')"
                prepend-icon="mdi-calendar"
                readonly
                v-bind="attrs"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker v-model="habitsData.pickerEnd" scrollable>
              <v-spacer></v-spacer>
              <v-btn text color="primary" @click="modalEnd = false"> Cancella </v-btn>
              <v-btn
                text
                color="primary"
                @click="$refs.dialogEnd.save(habitsData.pickerEnd)"
              >
                OK
              </v-btn>
            </v-date-picker>
          </v-dialog>
        </v-col>
      </v-row>
    </div>
    <div class="col-sm-12"></div>
    <h4 class="font-weight-regular">Gli alunni vanno a scuola di sabato?</h4>
    <div class="row">
      <div class="col-sm-1"></div>
      <div class="col-sm-4">
        <v-radio-group v-model="habitsData.saturdaySchool">
          <v-radio name="saturdaySchool" label="Sì" value="si"></v-radio>
          <v-radio name="saturdaySchool" label="No" value="no"></v-radio>
        </v-radio-group>
      </div>
    </div>

    <h4 class="font-weight-regular">
      I punti sono calcolati considerando sia l’andata che il ritorno da scuola?
    </h4>
    <div class="row">
      <div class="col-sm-1"></div>
      <div class="col-sm-4">
        <v-radio-group v-model="habitsData.roundtrip">
          <v-radio name="roundtrip" label="Sì" value="si"></v-radio>
          <v-radio name="roundtrip" label="No" value="no"></v-radio>
        </v-radio-group>
      </div>
    </div>

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
      isAtLeastOneCheckboxChecked: false,
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
        console.log(this.habitsData.habits);
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
        //TODO
        this.habitsData = this.currentGame.habitsDefinition;
      }
      console.log(this.habitsData.habits);
    },
    ...mapActions("game", {
      createHabits: "createHabits",
    }),
    ...mapActions("navigation", {
      nextStep: "nextStep",
    }),
    onActivityValueChange(index) {
      if (this.habitsData == null || this.habitsData.habits[index] == null) return;
      this.isInputsDataValid[index] = false;
      let totNumStud = 0;
      this.isAtLeastOneCheckboxChecked = false;
      // let totChillo = 0;
      this.habitsData.habits[index].forEach((habit) => {
        totNumStud += +habit.numStud;
        // totChillo += +habit.chilometri;
        if (habit.checked) {
          this.isAtLeastOneCheckboxChecked = true;
        }
      });
      this.formMetadata.totStudentsInserted[index] = totNumStud;
      // this.formMetadata.totalChilloMeters = totChillo;
      // const totalStudents = this.getTotalStudents();
      console.log(this.currentGame);
      if (totNumStud == this.currentGame.classDefinition[index].students.length) {
        this.isInputsDataValid[index] = true;
      }
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
    goToRouteSuggestion() {
      this.getDays();
      if (
        this.isInputsDataValid &&
        this.getDays() > 1 &&
        this.isAtLeastOneCheckboxChecked
      ) {
        this.$refs.form.validate();
        this.createHabits(this.habitsData);
        //this.$router.push("routeSuggestion");
        this.nextStep();
      } else if (this.isInputsDataValid != true) {
        this.snackBarText =
          "Per favore, controllare le informazioni immesse nella prima sezione di abitudini";
        this.snackbar = true;
      } else if (this.getDays() <= 1) {
        this.snackBarText =
          "Per favore, controllare che i giorni del percorso siano più di uno";
        this.snackbar = true;
      } else if (this.isAtLeastOneCheckboxChecked != true) {
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
</style>
