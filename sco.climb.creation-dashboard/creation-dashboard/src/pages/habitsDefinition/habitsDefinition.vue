<template>
<!-- <div>
  <v-form ref="form" v-model="valid">
    <v-container style="height: 1000px" class="grey lighten-5">
      <v-row no-gutters>
        <v-col cols="12" sm="12">
          <v-card class="pa-2" outlined tile>
            <div class="pa-7">
              <v-row
                ><h2 class="font-weight-regular">
                  Abitudini di mobilità degli alunni

                </h2>
                <div v-for="(gameclass,indexClass) in currentGame.classDefinition" :key="indexClass" >
                  {{gameclass.className}}
                  <span v-for="(student,studentIndex) in currentGame.classDefinition[indexClass]" :key="studentIndex">
                    {{student}}
                  </span>
                </div> 
                </v-row
              >
              <v-row> <v-col cols="12"></v-col></v-row>
              <v-row
                ><h4 class="font-weight-regular">
                  Come si recano a scuola attualmente gli alunni che intendono
                  partecipare al percorso Kids Go Green?
                  <v-tooltip v-model="show2" bottom
                    ><template v-slot:activator="{ on, attrs }">
                      <v-btn icon v-bind="attrs" v-on="on">
                        <v-icon color="grey lighten-1">
                          mdi-information-outline
                        </v-icon>
                      </v-btn>
                    </template>
                    <span
                      >Assicurarsi che il numero totale dei partecipanti
                      corrisponda al numero delle abitudini rilevate
                    </span>
                  </v-tooltip>
                  <div
                    class="text-center d-flex align-center justify-space-around"
                  ></div>
                </h4>
              </v-row>
            </div>

            <div
              v-if="habitsData.habits && habitsData.habits.length > 0"
              class="align-center text-center"
            >
              <div class="col-sm-12">
                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">In bici</div>
                  <div class="col-sm-2">
                    <v-text-field
                      @change="onActivityValueChange()"
                      v-model="habitsData.habits[0].numStud"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange()"
                      v-model="habitsData.habits[0].chilometri"
                      type="number"
                      :label="$t('numKms')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                </v-row>

                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">A piedi</div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      @change="onActivityValueChange"
                      v-model="habitsData.habits[1].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[1].chilometri"
                      type="number"
                      :label="$t('numKms')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                </v-row>

                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">Scuolabus o trasporto pubblico</div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[2].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[2].chilometri"
                      type="number"
                      :label="$t('numKms')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                </v-row>

                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">Pedibus</div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[3].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[3].chilometri"
                      type="number"
                      :label="$t('numKms')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                </v-row>

                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">
                    In auto fino alla piazzola di sosta
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[4].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[4].chilometri"
                      type="number"
                      :label="$t('numKms')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                </v-row>
                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">Car pooling</div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[5].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[5].chilometri"
                      type="number"
                      :label="$t('numKms')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                </v-row>
                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">In auto fino a scuola</div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[6].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
                      @keydown="onActivityValueChange"
                      v-model="habitsData.habits[6].chilometri"
                      type="number"
                      :label="$t('numKms')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                </v-row>

                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">Abitudini rilevate dagli studenti</div>
                  <div class="col-sm-2">
                    <span
                      v-bind:class="{ 'red--text': !this.isInputsDataValid }"
                      >{{ this.formMetadata.totStudentsInserted }}</span
                    >
                  </div>
                  <div class="col-sm-2">
                    <span>{{ this.formMetadata.totalChilloMeters }}</span>
                  </div>
                </v-row>
              </div>
            </div>

            <div class="pa-7">
              <h2 class="font-weight-regular">
                Mezzi di trasporto da visualizzare nel diario giornaliero
              </h2>
              <h4 class="font-weight-regular">
                Spuntare tutte le abitudini di mobilità che si vogliono avere
                disponibili per guadagnare i chilometri sostenibili che facciano
                avanzare nel percorso Kids Go Green. E’ possibile inserire mezzi
                che al momento non sono ancora utilizzati da alcun alunno, ma
                che si prevede possano interessare in futuro.
              </h4>
            </div>

            <div
              class="align-center text-center"
              v-if="habitsData.habits && habitsData.habits.length > 0"
            >
              <v-row>
                <div class="col-sm-2"></div>
                <div class="col-sm-2 pa-1" style="align-self: center">
                  In bici
                </div>
                <div class="col-sm-1 pa-1">
                  <v-checkbox
                    @change="onActivityValueChange()"
                    v-model="habitsData.habits[0].checked"
                  ></v-checkbox>
                </div>

                <div class="col-sm-1"></div>
                <div class="col-sm-2 pa-1" style="align-self: center">
                  A piedi
                </div>
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
                <div class="col-sm-2 pa-1" style="align-self: center">
                  Pedibus
                </div>
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
                <div class="col-sm-2 pa-1" style="align-self: center">
                  Car pooling
                </div>

                <div class="col-sm-1 pa-1">
                  <v-checkbox
                    @change="onActivityValueChange()"
                    v-model="habitsData.habits[5].checked"
                  ></v-checkbox>
                </div>
              </v-row>

              <v-row>
                <div class="col-sm-2"></div>
                <div class="col-sm-2 pa-1" style="align-self: center">
                  In auto fino a scuola
                </div>
                <div class="col-sm-1 pa-1">
                  <v-checkbox
                    @change="onActivityValueChange()"
                    v-model="habitsData.habits[6].checked"
                  ></v-checkbox>
                </div>
              </v-row>
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
                      <v-btn text color="primary" @click="modalStart = false">
                        Cancella
                      </v-btn>
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
                      <v-btn text color="primary" @click="modalEnd = false">
                        Cancella
                      </v-btn>
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

              <div class="col-sm-12"></div>
              <h4 class="font-weight-regular">
                Gli alunni vanno a scuola di sabato?
              </h4>
              <div class="row">
                <div class="col-sm-1"></div>
                <div class="col-sm-4">
                  <v-radio-group v-model="habitsData.saturdaySchool">
                    <v-radio
                      name="saturdaySchool"
                      label="Sì"
                      value="si"
                    ></v-radio>
                    <v-radio
                      name="saturdaySchool"
                      label="No"
                      value="no"
                    ></v-radio>
                  </v-radio-group>
                </div>
              </div>

              <h4 class="font-weight-regular">
                I punti sono calcolati considerando sia l’andata che il ritorno
                da scuola?
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

              <div class="row py-6">
                <div class="col-sm-1"></div>
                <div class="col-sm-5">
                  <v-btn class="float-left" @click="goToClassDefinition()"
                    >Indietro</v-btn
                  >
                </div>

                <div class="col-sm-5">
                  <v-btn
                    class="float-right"
                    :disabled="!valid"
                    @click="goToRouteSuggestion()"
                    >Avanti</v-btn
                  >
                </div>
                <div class="col-sm-1"></div>
              </div>
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
  </v-form>
 </div>  -->

  <v-container 
  :fluid="true"
  class="pa-0">

    <v-row class="mt-2">
      <v-col offset="2" cols="8">
        <v-row>
          <v-col cols="12" align-self="center" class="pa-0 ml-0">
            <div class="d-inline-block">
              <p class="titleFont"
                v-html="$t('habitsDefinition.title')">
              </p>
              <p
                v-html="$t('habitsDefinition.description')">
              </p>
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
                  <th class="text-left">
                    
                  </th>
                  <th class="text-center">
                    In bici
                  </th>
                  <th class="text-center">
                    A piedi
                  </th>
                  <th class="text-center">
                    Trasporto pubblico
                  </th>
                  <th class="text-center">
                    Pedibus
                  </th>
                  <th class="text-center">
                    In auto fino alla piazzola
                  </th>
                  <th class="text-center">
                    Car pooling
                  </th>
                  <th class="text-center">
                    In auto fino a scuola
                  </th>
                  <th class="text-center">
                    TOTALE ALUNNI
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(gameclass,indexClass) in currentGame.classDefinition" :key="indexClass" >
                  <td>{{ gameclass.className }}</td>
                  <td auto-grow>
                    <v-text-field placeholder="0" class="inputField" auto-grow suffix="/0"></v-text-field>
                  </td>
                  <td>
                    <v-text-field placeholder="0" class="inputField" auto-grow></v-text-field>
                  </td>
                  <td>
                    <v-text-field placeholder="0" auto-grow></v-text-field>
                  </td>
                  <td>
                    <v-text-field placeholder="0"></v-text-field>
                  </td>
                  <td>
                    <v-text-field placeholder="0"></v-text-field>
                  </td>
                  <td>
                    <v-text-field placeholder="0"></v-text-field>
                  </td>
                  <td>
                    <v-text-field placeholder="0"></v-text-field>
                  </td>
                  <td>
                    <v-text-field ></v-text-field>
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
      transition="scroll-y-reverse-transition">
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
        @click="goToClassDefinition()">
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
        @click="goOnHab()">
        <span>Salva e prosegui</span>
        <v-icon>mdi-chevron-right</v-icon>
      </v-btn>

  </v-container>

</template>

<script>
import { mapState, mapActions } from "vuex";
export default {
  name: "habitsDefinition",
  components: {
  },
  data() {
    return {
      isInputsDataValid: false,
      snackbar: false,
      snackBarText: "",
      habitsData: {
        habits: [],
        saturdaySchool: "no",
        roundtrip: "no",
        pickerStart: 0,
        pickerEnd: 0,
        diffDays: 0,
      },
        desserts: [
          {
            name: 'Frozen Yogurt',
            calories: 159,
          },
          {
            name: 'Ice cream sandwich',
            calories: 237,
          },
          {
            name: 'Eclair',
            calories: 262,
          },
          {
            name: 'Cupcake',
            calories: 305,
          },
          {
            name: 'Gingerbread',
            calories: 356,
          },
          {
            name: 'Jelly bean',
            calories: 375,
          },
          {
            name: 'Lollipop',
            calories: 392,
          },
          {
            name: 'Honeycomb',
            calories: 408,
          },
          {
            name: 'Donut',
            calories: 452,
          },
          {
            name: 'KitKat',
            calories: 518,
          },
        ],
      row: null,
      modalStart: false,
      isAtLeastOneCheckboxChecked: false,
      modalEnd: false,
      valid: true,
      formMetadata: { totStudentsInserted: 0, totalChilloMeters: 0 },
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
    ...mapActions("navigation",["prevStep","nextStep"]),
    initHabitsArray: function () {
      if (this.currentGame.habitsDefinition == null) {
        this.habitsData.habits.push({
          name: "inBici",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.habitsData.habits.push({
          name: "aPiedi",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.habitsData.habits.push({
          name: "scuolabus",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.habitsData.habits.push({
          name: "pedibus",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.habitsData.habits.push({
          name: "autososta",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.habitsData.habits.push({
          name: "carpooling",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.habitsData.habits.push({
          name: "autoscuola",
          numStud: 0,
          chilometri: 0,
          checked: false,
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
      }
    },
    ...mapActions("game", {
      createHabits: "createHabits",
    }),
    ...mapActions("navigation", {
      nextStep: "nextStep",
    }),
    onActivityValueChange() {
      if (this.habitsData == null || this.habitsData.habits == null) return;
      this.isInputsDataValid = false;
      let totNumStud = 0;
      this.isAtLeastOneCheckboxChecked = false;
      let totChillo = 0;
      this.habitsData.habits.forEach((habit) => {
        totNumStud += +habit.numStud;
        totChillo += +habit.chilometri;
        if (habit.checked) {
          this.isAtLeastOneCheckboxChecked = true;
        }
      });
      this.formMetadata.totStudentsInserted = totNumStud;
      this.formMetadata.totalChilloMeters = totChillo;
      const totalStudents = this.getTotalStudents();
      if (totNumStud == totalStudents) {
        this.isInputsDataValid = true;
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
  mounted() {
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

