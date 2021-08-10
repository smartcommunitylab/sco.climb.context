<template>
  <div class="col-xs-10 col-sm-4 c-card-layout">
    <div class="row mx-0">
      <div class="col-12 col-xs-12 pa-1">
        <v-text-field
          :label="$t('class')"
          v-model="schoolClass.className"
          outlined
          dense
        ></v-text-field>
      </div>
      <!-- <div class="col-12 col-sm-4">
        <label for="">No. Student:</label>
        <v-text-field
          type="number"
          min="0"
          value="15"
          v-model="schoolClass.studentsNum"
          @change="updateStudentsFields(schoolClass.studentsNum)"
          outlined
          dense
        ></v-text-field>
      </div> -->
      <div
        class="col-12 col-xs-12"
        v-for="(student, index) in schoolClass.students"
        :key="student.id"
      >
        <label>{{ index }}</label>
        <v-text-field
          type="text"
          min="0"
          value="15"
          :label="$t('nickname')"
          v-model="student.inputVal"
          outlined
          dense
        ></v-text-field>
        <v-btn class="mx-2" fab dark small color="primary" @click="deleteStudent(index)">
          <v-icon dark> mdi-minus </v-icon>
        </v-btn>
      </div>
      <v-btn class="mx-2" fab dark color="indigo" @click="addStudent()">
        <v-icon dark> mdi-plus </v-icon>
      </v-btn>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
export default {
  data() {
    return {
      expand: false,
      expand2: false,
      // className: "",
      // studentsNum: 5,
      // students: [],
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
    // ...mapActions("game", {
    //   createClass: "createClass",
    // }),
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
      // if (this.schoolClass.students?.length > num) {
      //   this.schoolClass.students.splice(0, this.schoolClass.students.length - num);
      // } else {
      //   let lastM = 0;
      //   if (this.schoolClass.students?.length > 0) {
      //     lastM = this.schoolClass.students[this.schoolClass.students.length - 1].id;
      //   }
      //   const tobeAdded = num - this.schoolClass.students.length;
      //   for (let i = 0; i < tobeAdded; i++) {
      //     this.schoolClass.students.push({ inputVal: "", id: lastM + 1 + i });
      //   }
      // }
    },
  },
  mounted() {
    this.updateStudentsFields(this.schoolClass.studentsNum);
    // this.createClass({ students: this.schoolClasses.students });
  },
};
</script>

<style></style>
