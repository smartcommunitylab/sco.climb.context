<template>
  <v-form ref="form" v-model="valid" lazy-validation>
    <v-container style="height: 1000px" class="grey lighten-5">
      <v-row no-gutters>
        <v-col cols="12" sm="12">
          <v-card class="pa-2" outlined tile>
            <div class="pa-7">
              <h2>Abitudini di mobilità degli alunni</h2>
              <h4>
                Come si recano a scuola attualmente gli alunni che intendono
                partecipare al percorso Kids Go Green?
                <v-icon color="grey lighten-1">
                  mdi-information-outline
                </v-icon>
                <div
                  class="text-center d-flex align-center justify-space-around"
                ></div>
              </h4>
            </div>

            <div
              v-if="habitsData.habits && habitsData.habits.length > 0"
              class="align-center text-center"
            >
              <div class="col-sm-12">
                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">In Bici</div>
                  <div class="col-sm-2">
                    <v-text-field
                      v-model="habitsData.habits[0].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
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
                      v-model="habitsData.habits[1].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
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
                      v-model="habitsData.habits[2].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
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
                      v-model="habitsData.habits[3].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
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
                      v-model="habitsData.habits[4].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
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
                      v-model="habitsData.habits[5].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
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
                      v-model="habitsData.habits[6].numStud"
                      type="number"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                  <div class="col-sm-2">
                    <v-text-field
                      v-model="habitsData.habits[6].chilometri"
                      type="number"
                      :label="$t('numKms')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                </v-row>

                <!-- da cambiare  -->
                <v-row>
                  <div class="col-sm-2"></div>
                  <div class="col-sm-2">Abitudini rilevate dagli studenti</div>
                  <div class="col-sm-2"  > <span v-bind:class="{'red--text': hasStudentsError }">{{totStud}}</span>/<span>{{totHabits}}</span></div>
                </v-row>
              </div>
            </div>

            <div class="pa-7">
              <h2>Mezzi di trasporto da visualizzare nel diario giornaliero</h2>
              <h4>
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
                <div class="col-sm-2 pa-1">In Bici</div>
                <div class="col-sm-1 pa-1">
                  <v-checkbox
                    v-model="habitsData.habits[0].checked"
                  ></v-checkbox>
                </div>

                <div class="col-sm-1"></div>
                <div class="col-sm-2 pa-1">A piedi</div>
                <div class="col-sm-1 pa-1 checkbox">
                  <v-checkbox
                    v-model="habitsData.habits[1].checked"
                  ></v-checkbox>
                </div>
              </v-row>

              <v-row>
                <div class="col-sm-2"></div>
                <div class="col-sm-2 pa-1">Scuolabus o trasporto pubblico</div>
                <div class="col-sm-1 pa-1">
                  <v-checkbox
                    v-model="habitsData.habits[2].checked"
                  ></v-checkbox>
                </div>

                <div class="col-sm-1"></div>
                <div class="col-sm-2 pa-1">Pedibus</div>
                <div class="col-sm-1 pa-1">
                  <v-checkbox
                    v-model="habitsData.habits[3].checked"
                  ></v-checkbox>
                </div>
              </v-row>

              <v-row>
                <div class="col-sm-2"></div>
                <div class="col-sm-2 pa-1">
                  In auto fino alla piazzola di sosta
                </div>
                <div class="col-sm-1 pa-1">
                  <v-checkbox
                    v-model="habitsData.habits[4].checked"
                  ></v-checkbox>
                </div>

                <div class="col-sm-1"></div>
                <div class="col-sm-2 pa-1">Car pooling</div>

                <div class="col-sm-1 pa-1">
                  <v-checkbox
                    v-model="habitsData.habits[5].checked"
                  ></v-checkbox>
                </div>
              </v-row>

              <v-row>
                <div class="col-sm-2"></div>
                <div class="col-sm-2 pa-1">In auto fino a scuola</div>
                <div class="col-sm-1 pa-1">
                  <v-checkbox
                    v-model="habitsData.habits[6].checked"
                  ></v-checkbox>
                </div>
              </v-row>
            </div>

            <div class="pa-7">
              <h2>Calibrazione del percorso</h2>
              <div class="col-sm-12"></div>

              <v-row>
                <v-col cols="6">
                  <v-dialog
                    ref="dialog"
                    v-model="modal"
                    :return-value.sync="pickerStart"
                    persistent
                    width="290px"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-text-field
                        v-model="pickerStart"
                        label="Data di inizio"
                        prepend-icon="mdi-calendar"
                        readonly
                        v-bind="attrs"
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker v-model="pickerStart" scrollable>
                      <v-spacer></v-spacer>
                      <v-btn text color="primary" @click="modal = false">
                        Cancel
                      </v-btn>
                      <v-btn
                        text
                        color="primary"
                        @click="$refs.dialog.save(pickerStart)"
                      >
                        OK
                      </v-btn>
                    </v-date-picker>
                  </v-dialog>
                </v-col>
                <v-spacer></v-spacer>
                <v-col cols="6">
                  <v-menu
                    v-model="menu2"
                    :close-on-content-click="false"
                    :nudge-right="40"
                    transition="scale-transition"
                    offset-y
                    min-width="auto"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-text-field
                        v-model="pickerEnd"
                        label="Data di fine"
                        prepend-icon="mdi-calendar"
                        readonly
                        v-bind="attrs"
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker
                      v-model="pickerEnd"
                      @input="menu2 = false"
                    ></v-date-picker>
                  </v-menu>
                </v-col>
              </v-row>

              <!-- CALENDARIO VECCHIO -->
              <!-- <v-row>
                <div class="col-sm-4">Data di inizio</div>
                <div class="col-sm-1">
                  <v-btn icon @click="expandStart = !expandStart">
                    <v-icon color="grey lighten-1"> mdi-calendar </v-icon>
                  </v-btn>
                </div>
                <v-date-picker
                  v-show="expandStart"
                  v-model="pickerStart"
                ></v-date-picker>

                <div class="col-sm-4">Data di fine</div>
                <div class="col-sm-1">
                  <v-btn icon @click="expandEnd = !expandEnd">
                    <v-icon color="grey lighten-1"> mdi-calendar </v-icon>
                  </v-btn>
                </div>

                <v-date-picker
                  v-show="expandEnd"
                  v-model="pickerEnd"
                ></v-date-picker>
              </v-row> -->

              <div class="col-sm-12"></div>
              <h4>Gli alunni vanno a scuola di sabato?</h4>
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

              <h4>
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
    </v-container>
  </v-form>
</template>

<script>
import { mapState, mapActions } from "vuex";
export default {
  name: "habitsDefinition",
  data() {
    return {
      habitsData: {
        habits: [],
        saturdaySchool: "no",
        roundtrip: "no",
        //passa anche start date and end date
        diffDays: 0,
      },
      row: null,
      modal: false,
      modal2: false,
      menu2: false,
      valid: true,
      nomepagina: "habitsDefinition",
      expandStart: false,
      show: true,
      expandEnd: false,
      pickerStart: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
        .toISOString()
        .substr(0, 10),
      pickerEnd: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
        .toISOString()
        .substr(0, 10),
    };
  },
  computed: {
    ...mapState("game", ["currentGame"]),
    hasStudentsError(){
      //if totStud == studenti di classDef
      return true;
    },
    //hasError if totHabits 
    totStud(){
      return 4 ; //currentGame.stud in classDef 
    },
    totHabits(){
      return 10;
    }
  },
  methods: {
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

        //aggiungere pickenEnd e pickerStart con luxon?
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

    getDays() {
      /* const date1 = luxon.DateTime.fromISO("2020-09-06T12:00")
          const date2 = luxon.DateTime.fromISO("2019-06-10T14:00")

const diff = date1.diff(date2, ["years", "months", "days", "hours"])

console.log(diff.toObject())*/
      const date1 = new Date(this.pickerStart);
      const date2 = new Date(this.pickerEnd);
      const diffTime = Math.abs(date2 - date1);
      this.habitsData.diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return this.habitsData.diffDays;
    },
    goToRouteSuggestion() {
      this.$refs.form.validate();
      this.getDays();
      this.createHabits(this.habitsData);
      this.$router.push("routeSuggestion");
      this.nextStep();
    },
    goToClassDefinition() {
      this.$router.push("classDefinition");
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
</style>

