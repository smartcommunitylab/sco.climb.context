<template>
<div>
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
                      @change="onActivityValueChange()"
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
                      placeholder="culo"
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
 </div>
 </template>