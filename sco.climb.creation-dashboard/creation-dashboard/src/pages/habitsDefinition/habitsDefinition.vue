<template>
  <v-container style="height: 1000px" class="grey lighten-5">
    <v-row no-gutters>
      <v-col cols="12" sm="12">
        <v-card class="pa-2" outlined tile>
          <div class="pa-7">
            <p class="text-center text-h3">{{ nomepagina }}</p>
            <hr />
            <h2>Abitudini di mobilità degli alunni</h2>
            <h4>
             
              Come si recano a scuola attualmente gli alunni che intendono
              partecipare al percorso Kids Go Green?
              <v-icon color="grey lighten-1"> mdi-information-outline </v-icon>
              <!--  <div class="text-center d-flex align-center justify-space-around">
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn color="primary" dark v-bind="attrs" v-on="on">
                      Button
                    </v-btn>
                  </template>
                  <span
                    >Assicurarsi che il numero totale dei partecipanti
                    corrisponda al numero delle abitudini rilevate</span
                  >
                </v-tooltip>
              </div> -->
            </h4>
          </div>

          <div
            v-if="appData.habits && appData.habits.length > 0"
            class="align-center text-center"
          >
            <div class="col-sm-12">
              <v-row>
                <div class="col-sm-2"></div>
                <div class="col-sm-2">In Bici</div>
                <div class="col-sm-2">
                  <v-text-field
                    v-model="appData.habits[0].numStud"
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    v-model="appData.habits[0].chilometri"
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
                    v-model="appData.habits[1].numStud"
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    v-model="appData.habits[1].chilometri"
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
                    v-model="appData.habits[2].numStud"
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    v-model="appData.habits[2].chilometri"
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
                    v-model="appData.habits[3].numStud"
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    v-model="appData.habits[3].chilometri"
                    type="number"
                    :label="$t('numKms')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
              </v-row>

              <v-row>
                <div class="col-sm-2"></div>
                <div class="col-sm-2">In auto fino alla piazzola di sosta</div>
                <div class="col-sm-2">
                  <v-text-field
                    v-model="appData.habits[4].numStud"
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    v-model="appData.habits[4].chilometri"
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
                    v-model="appData.habits[5].numStud"
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    v-model="appData.habits[5].chilometri"
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
                    v-model="appData.habits[6].numStud"
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    v-model="appData.habits[6].chilometri"
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
                <div class="col-sm-2">0/0</div>
              </v-row>
            </div>
            <!-- <v-btn @click="saveInfo()"></v-btn> -->
          </div>

          <div class="pa-7">
            <h2>Mezzi di trasporto da visualizzare nel diario giornaliero</h2>
            <h4>
              Spuntare tutte le abitudini di mobilità che si vogliono avere
              disponibili per guadagnare i chilometri sostenibili che facciano
              avanzare nel percorso Kids Go Green. E’ possibile inserire mezzi
              che al momento non sono ancora utilizzati da alcun alunno, ma che
              si prevede possano interessare in futuro.
            </h4>
          </div>

          <div
            class="align-center text-center"
            v-if="appData.habits && appData.habits.length > 0"
          >
            <v-row>
              <div class="col-sm-2"></div>
              <div class="col-sm-2 pa-1">In Bici</div>
              <div class="col-sm-1 pa-1">
                <v-checkbox v-model="appData.habits[0].checked"></v-checkbox>
              </div>

              <div class="col-sm-1"></div>
              <div class="col-sm-2 pa-1">A piedi</div>
              <div class="col-sm-1 pa-1 checkbox">
                <v-checkbox v-model="appData.habits[1].checked"></v-checkbox>
              </div>
            </v-row>

            <v-row>
              <div class="col-sm-2"></div>
              <div class="col-sm-2 pa-1">Scuolabus o trasporto pubblico</div>
              <div class="col-sm-1 pa-1">
                <v-checkbox v-model="appData.habits[2].checked"></v-checkbox>
              </div>

              <div class="col-sm-1"></div>
              <div class="col-sm-2 pa-1">Pedibus</div>
              <div class="col-sm-1 pa-1">
                <v-checkbox v-model="appData.habits[3].checked"></v-checkbox>
              </div>
            </v-row>

            <v-row>
              <div class="col-sm-2"></div>
              <div class="col-sm-2 pa-1">
                In auto fino alla piazzola di sosta
              </div>
              <div class="col-sm-1 pa-1">
                <v-checkbox v-model="appData.habits[4].checked"></v-checkbox>
              </div>

              <div class="col-sm-1"></div>
              <div class="col-sm-2 pa-1">Car pooling</div>

              <div class="col-sm-1 pa-1">
                <v-checkbox v-model="appData.habits[5].checked"></v-checkbox>
              </div>
            </v-row>

            <v-row>
              <div class="col-sm-2"></div>
              <div class="col-sm-2 pa-1">In auto fino a scuola</div>
              <div class="col-sm-1 pa-1">
                <v-checkbox v-model="appData.habits[6].checked"></v-checkbox>
              </div>
            </v-row>
          </div>

          <div class="pa-7">
            <h2>Calibrazione del percorso</h2>
            <div class="col-sm-12"></div>
            <v-row>
              <div class="col-sm-1"></div>
              <div class="col-sm-4">Data di inizio</div>
              <div class="col-sm-4">
                <v-btn icon @click="expandStart = !expandStart">
                  <v-icon color="grey lighten-1"> mdi-calendar </v-icon>
                </v-btn>
              </div>
            </v-row>
            <!--v-bind="attrs"
                  v-on="on" -->
            <div class="row">
              <div class="col-sm-1"></div>
              <div class="col-sm-4">Data di fine</div>
              <div class="col-sm-4">
                <v-btn icon @click="expandEnd = !expandEnd">
                  <v-icon color="grey lighten-1"> mdi-calendar </v-icon>
                </v-btn>
              </div>
            </div>

            <v-date-picker
              v-show="expandStart"
              v-model="pickerStart"
            ></v-date-picker>
            <v-date-picker
              v-show="expandEnd"
              v-model="pickerEnd"
            ></v-date-picker>
            <div class="col-sm-12"></div>
            <h4>Gli alunni vanno a scuola di sabato?</h4>
            <div class="row">
              <div class="col-sm-1"></div>
              <div class="col-sm-4">
                <v-radio-group v-model="appData.saturdaySchool">
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
              I punti sono calcolati considerando sia l’andata che il ritorno da
              scuola?
            </h4>
            <div class="row">
              <div class="col-sm-1"></div>
              <div class="col-sm-4">
                <v-radio-group v-model="appData.roundtrip">
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
                <v-btn class="float-right" @click="goToRouteSuggestion()"
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
</template>

<script>
import { mapState, mapActions } from "vuex";
export default {
  name: "habitsDefinition",
  data() {
    return {
      appData: {
        habits: [],
        saturdaySchool: "no",
        roundtrip: "no",
        pickerStart: 0,
        pickerEnd: 0,
      },
      row: null,
      nomepagina: "habitsDefinition",
      expandStart: false,
      show: false,
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
  },
  methods: {
    initHabitsArray: function () {
      //const storedValue = localStorage.getItem("saveHabits");
      if (this.currentGame.habitsDefinition == null) {
        this.appData.habits.push({
          name: "inBici",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.appData.habits.push({
          name: "aPiedi",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.appData.habits.push({
          name: "scuolabus",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.appData.habits.push({
          name: "pedibus",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.appData.habits.push({
          name: "autososta",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.appData.habits.push({
          name: "carpooling",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });
        this.appData.habits.push({
          name: "autoscuola",
          numStud: 0,
          chilometri: 0,
          checked: false,
        });

        this.appData.pickerStart = new Date(
          Date.now() - new Date().getTimezoneOffset() * 60000
        )
          .toISOString()
          .substr(0, 10);

        this.appData.pickerEnd = new Date(
          Date.now() - new Date().getTimezoneOffset() * 60000
        )
          .toISOString()
          .substr(0, 10);

        this.appData.saturdaySchool = "no";
        this.appData.roundtrip = "no";
      } else {
        this.appData = this.currentGame.habitsDefinition;
      }
    },
    /*addHabit: function () {
      const str = JSON.stringify(this.appData);
      localStorage.setItem("saveHabits", str);
      console.log(this.appData);
    },*/
     ...mapActions("game", {
      createHabits: "createHabits",
    }),
    ...mapActions("navigation", {
      nextStep: "nextStep",
    }),

    goToRouteSuggestion() {
      this.createHabits(this.appData);
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

