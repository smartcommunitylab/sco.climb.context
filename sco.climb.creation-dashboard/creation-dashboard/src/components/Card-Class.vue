<template>
  <div class="c-card-layout px-7">
    <v-row>
      <v-col cols="10"></v-col>
      <v-col cols="1" class="pull-right">
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
          <v-text-field
            v-model="schoolClass.className"
            :label="$t('class')"
            :rules="classNameRules"
            required
            outlined
            dense
          ></v-text-field>
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
        <v-btn class="mx-2" color="indigo" rounded small @click="addStudent()">
          <v-icon color="white"> mdi-plus </v-icon>
        </v-btn>
      </v-col>
    </v-row>
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
      nicknameRules: [
        (v) =>
          (v && v.length >= 3) ||
          "Il nickname deve essere più lungo di 3 caratteri",
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
    deleteStudent(index) {
      this.schoolClass.students.splice(index, 1);
    },
    addStudent() {
      this.schoolClass.students.push({
        inputVal: "",
        id: this.schoolClass.students.length,
      });
    },
    updateStudentsFields(num) {
      for (let i = 0; i < num; i++) {
        this.schoolClass.students.push({ inputVal: "", id: i });
      }
    },
  },
  mounted() {
    if (this.schoolClass) {
      this.schoolClass.form = this.$refs.form;
    }
  },
};
</script>

<style>
.c-card-layout {
  background: #f8f8f8;
  border-radius: 8px;
}
</style>