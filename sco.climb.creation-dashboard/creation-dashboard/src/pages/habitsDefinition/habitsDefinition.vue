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
              {{ currentGame.classDefinition.nome }}
              <div
                v-for="student in currentGame.classDefinition.students"
                :key="student.id"
              >
                {{ student.inputVal }}
              </div>
              Come si recano a scuola attualmente gli alunni che intendono
              partecipare al percorso Kids Go Green?<v-tooltip
                v-model="show"
                right
                ><template v-slot:activator="{ on, attrs }">
                  <v-btn icon v-bind="attrs" v-on="on">
                    <v-icon color="grey lighten-1">
                      mdi-information-outline
                    </v-icon>
                  </v-btn>
                </template>
                <span
                  >Assicurarsi che il numero totale dei partecipanti corrisponda
                  al numero delle abitudini rilevate</span
                >
              </v-tooltip>
            </h4>
          </div>

          <div class="align-center text-center">
            <div class="col-sm-12">
              <v-row>
                <div class="col-sm-2">In Bici</div>
                <v-form ref="form" @submit.prevent="submit">
                  <div class="col-sm-2">
                    <v-text-field
                      v-model="form.inbici"
                      type="number"
                      color="blue darken-2"
                      :label="$t('numStud')"
                      outlined
                      dense
                    ></v-text-field>
                  </div>
                
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numKms')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                </v-form>
              </v-row>
              <v-row>
                <div class="col-sm-2">A piedi</div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numKms')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
              </v-row>

              <v-row>
                <div class="col-sm-2">Scuolabus o trasporto pubblico</div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numKms')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
              </v-row>
              <v-row>
                <div class="col-sm-2">Pedibus</div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numKms')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
              </v-row>

              <v-row>
                <div class="col-sm-2">In auto fino alla piazzola di sosta</div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numKms')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
              </v-row>
              <v-row>
                <div class="col-sm-2">Car pooling</div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numKms')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
              </v-row>
              <v-row>
                <div class="col-sm-2">In auto fino a scuola</div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numStud')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
                <div class="col-sm-2">
                  <v-text-field
                    type="number"
                    :label="$t('numKms')"
                    outlined
                    dense
                  ></v-text-field>
                </div>
              </v-row>

              <v-row>
                <div class="col-sm-2">Abitudini rilevate dagli studenti</div>
                <div class="col-sm-2">
                  <v-text-field></v-text-field>
                </div>
              </v-row>
            </div>

            <v-btn @click="saveInfo()"></v-btn>
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

          <div class="align-center text-center">
            <v-row>
              <div class="col-sm-2">In Bici</div>
              <div class="col-sm-4">
                <v-checkbox></v-checkbox>
              </div>
            </v-row>

            <v-row>
              <div class="col-sm-2">A piedi</div>
              <div class="col-sm-4">
                <v-checkbox></v-checkbox>
              </div>
            </v-row>
            <v-row>
              <div class="col-sm-2">Scuolabus o trasporto pubblico</div>
              <div class="col-sm-4">
                <v-checkbox></v-checkbox>
              </div>
            </v-row>

            <v-row>
              <div class="col-sm-2">Pedibus</div>
              <div class="col-sm-4">
                <v-checkbox></v-checkbox>
              </div>
            </v-row>

            <v-row>
              <div class="col-sm-2">In auto fino alla piazzola di sosta</div>
              <div class="col-sm-4">
                <v-checkbox></v-checkbox>
              </div>
            </v-row>

            <v-row>
              <div class="col-sm-2">Car pooling</div>
              <div class="col-sm-4">
                <v-checkbox></v-checkbox>
              </div>
            </v-row>
            <v-row>
              <div class="col-sm-2">In auto fino a scuola</div>
              <div class="col-sm-4">
                <v-checkbox></v-checkbox>
              </div>
            </v-row>
          </div>

          <div class="pa-7">
            <h2>Calibrazione del percorso</h2>

            <div class="row">
              <div class="col-sm-4">Data di inizio</div>
              <div class="col-sm-4">
                <v-btn
                  icon
                  v-bind="attrs"
                  v-on="on"
                  @click="expandStart = !expandStart"
                >
                  <v-icon color="grey lighten-1"> mdi-calendar </v-icon>
                </v-btn>
              </div>
            </div>

            <div class="row">
              <div class="col-sm-4">Data di fine</div>
              <div class="col-sm-4">
                <v-btn
                  icon
                  v-bind="attrs"
                  v-on="on"
                  @click="expandEnd = !expandEnd"
                >
                  <v-icon color="grey lighten-1"> mdi-calendar </v-icon>
                </v-btn>
              </div>
            </div>

            <v-date-picker
              v-show="expandStart"
              v-model="picker"
            ></v-date-picker>
            <v-date-picker v-show="expandEnd" v-model="picker2"></v-date-picker>

            <h4>Gli alunni vanno a scuola di sabato?</h4>
            <div class="row">
              <div class="col-sm-4">
                <v-radio-group v-model="row" row>
                  <v-radio label="Sì" value="radio-1"></v-radio>
                  <v-radio label="No" value="radio-2"></v-radio>
                </v-radio-group>
              </div>
            </div>

            <h4>
              I punti sono calcolati considerando sia l’andata che il ritorno da
              scuola?
            </h4>
            <div class="row">
              <div class="col-sm-4">
                <v-radio-group v-model="row" row>
                  <v-radio label="Sì" value="radio-1"></v-radio>
                  <v-radio label="No" value="radio-2"></v-radio>
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
import { mapState } from "vuex";
export default {
  name: "habitsDefinition",
  data() {
    const habitsForm = Object.freeze({
      inbici: 0,
    });
    return {
      form: Object.assign({}, habitsForm),
      habitsForm,
      nomepagina: "habitsDefinition",
      expandStart: false,
      expandEnd: false,
      picker: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
        .toISOString()
        .substr(0, 10),

      picker2: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
        .toISOString()
        .substr(0, 10),
    };
  },
  computed: {
    ...mapState("game", ["currentGame"]),
    formIsValid() {
      return this.form.inbici;
    },
  },
  methods: {
    goToRouteSuggestion() {
      this.$router.push("routeSuggestion");
    },
    goToClassDefinition() {
      this.$router.push("classDefinition");
    },
    resetForm() {
      this.form = Object.assign({}, this.habitsForm);
      this.$refs.form.reset();
    },
    submit() {
      this.resetForm();
    },
  },
  mounted() {
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

<style></style>

