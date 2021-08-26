<template>
  <div class="col-xs-10 col-sm-10 c-card-layout">
    <!-- v-for="(card, index) in schoolClassCards.cards" :key="card.id" -->
    <v-row>
      <!-- button to delete class -->
      <v-col cols="10"></v-col>
      <v-btn icon color="red">
        <v-icon>mdi-delete</v-icon>
      </v-btn>
    </v-row>

    <v-row>
      <v-col cols="12">
        <!-- class input -->
        <v-text-field
          :label="$t('class')"
          v-model="schoolClass.className"
          outlined
          dense
        ></v-text-field>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        Nicknames
        <v-tooltip v-model="show" right @click="show = !show"
          ><template v-slot:activator="{ on, attrs }">
            <v-btn icon v-bind="attrs" v-on="on">
              <v-icon color="grey lighten-1"> mdi-help-circle-outline </v-icon>
            </v-btn>
          </template>
          <span
            >Ogni numero è associato al nickname di uno studente e non è
            modificabile</span
          >
        </v-tooltip>
      </v-col>
    </v-row>
    <v-row v-for="(student, index) in schoolClass.students" :key="student.id">
      <v-col cols="2 pa-0" align="center">
        <label align-item="center">{{ index }}</label>
      </v-col>

      <v-col cols="6 pa-0">
        <v-text-field
          type="text"
          min="1"
          value="15"
          :label="$t('nickname')"
          v-model="student.inputVal"
          outlined
          dense
        ></v-text-field>
      </v-col>

      <v-col cols="2 pa-1">
        <v-btn
          class="mx-2"
          color="primary"
          rounded
          small
          @click="deleteStudent()"
        >
          <v-icon> mdi-minus </v-icon>
        </v-btn>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="8"></v-col>
      <v-col cols="2 pa-1">
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
      index: 1,
      expand: false,
      expand2: false,
      show: false,
    };
  },
  namespaced: true,
  props: {
    schoolClass: Object,
  },
  computed: {
    ...mapState("game", ["currentGame"]),
  },
  methods: {
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
    this.updateStudentsFields(this.schoolClass.studentsNum);
    // this.createClass({ students: this.schoolClasses.students });
  },
};
</script>

<style>
.c-card-layout {
  background: #f8f8f8;
  border-radius: 8px;
}
</style>