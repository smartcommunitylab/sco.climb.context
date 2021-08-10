<template>
  <div class="col-xs-10 col-sm-4 c-card-layout">
    <div class="row mx-0">
      <div class="col-12 col-xs-12 pa-1">
        <v-text-field :label="$t('class')" v-model="className" outlined dense></v-text-field>
      </div>
      <div class="col-12 col-sm-4">
        <label for="">No. Student:</label>
        <v-text-field
          type="number"
          min="0"
          value="15"
          v-model="studentsNum"
          @change="updateStudentsFields(studentsNum)"
          outlined
          dense
        ></v-text-field>
      </div>
      <div
        class="col-12 col-xs-12"
        v-for="student in students"
        :key="student.id"
      >
        <label>{{ student.id }}</label>
        <v-text-field
          type="text"
          min="0"
          value="15"
          :label="$t('nickname')"
          v-model="student.inputVal"
          @change="updateStudentsFields(studentsNum)"
          outlined
          dense
        ></v-text-field>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
export default {
  data() {
    return {
      expand: false,
      expand2: false,
      className: "",
      studentsNum: 5,
      students: [],
    };
  },
  namespaced: true,
  props: {},
  computed: {
    ...mapState("game", ["currentGame"]),
  },
  methods: {
    ...mapActions("game", {
      createClass: "createClass",
    }),
    updateStudentsFields(num) {
      if (this.students?.length > num) {
        this.students.splice(0, this.students.length - num);
      } else {
        let lastM = 0;
        if (this.students?.length > 0) {
          lastM = this.students[this.students.length - 1].id;
        }
        const tobeAdded = num - this.students.length;
        for (let i = 0; i < tobeAdded; i++) {
          this.students.push({ inputVal: "", id: lastM + 1 + i });
        }
      }
    },
  },
  mounted() {
    this.updateStudentsFields(this.studentsNum);
    this.createClass({ students: this.students });
  },
};
</script>

<style>
</style>
