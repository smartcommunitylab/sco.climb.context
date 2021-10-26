<template>
    <div>
    <v-stepper alt-labels class="stepper elevation-0">
      <v-stepper-header>
        <template v-for="(item,index) in items"  >
          <v-stepper-step :key="index.step" :step="item.step" @click="openStep(item)" :complete="item.step <= lastStep" :editable="isEditable(item)">
          <p class="step">{{item.text}}</p>
           
        </v-stepper-step>
        <v-divider  :key="index" v-if="index < items.length - 1"></v-divider>
        </template>
      </v-stepper-header>
    </v-stepper>
  </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
export default {
  name: "Stepper",
  data() {
    return {
      // snackbar: false,
      // snackBarText: "",
    }
  },
  computed: {
    ...mapState("navigation", ["page","items","currentStep","lastStep"]),
    ...mapState("game", ["currentGame"]),
    currentRouteName() {
        return this.$route.name;
    }
  },
  created() {},
  methods: {
    ...mapActions("navigation",["changePageByStepper"]),
    //is the current step
    isEditable(item){
      return item.step == this.lastStep
    },
    openStep(step){
      this.changePageByStepper(step);
      

    },
    // isActualPath(path){
    //   return this.currentRouteName===path.href
    // },
  }
};
</script>
<style>
/*.breadcrumb {
  color: white;
  position: relative;
  margin: 25px;
}
.breadcrumb:after {
  content: "";
  position: absolute;
  right: -43px;
  top: 0%;
  height: 100%;
  border: 23px solid transparent;
}
.breadcrumb:before {
  position: absolute;
  content: "";
  left: -40px;
  top: 0%;
  height: 100%;
}*/
.divStepper {
  width: 100%;
  display: flex;
  justify-content: center;
}
.stepper {
  width: 90%;
}
.step {
  text-align: center;
}
.home {
  background-color: lightcoral;
}

.home:after {
  border-left: 20px solid lightcoral;
}

.classDefinition {
  background-color: #3ec28f;
}
.classDefinition:after {
  border-left: 20px solid #3ec28f;
}
.classDefinition:before {
  border-top: 22px solid #3ec28f;
  border-bottom: 22px solid #3ec28f;
  border-right: 22px solid #3ec28f;
  border-left: 22px solid transparent;
}
.habitsDefinition {
  background-color: #8bdcf5;
}
.habitsDefinition:after {
  border-left: 20px solid #8bdcf5;
}
.habitsDefinition:before {
  border-top: 22px solid #8bdcf5;
  border-bottom: 22px solid #8bdcf5;
  border-right: 22px solid #8bdcf5;
  border-left: 22px solid transparent;
}
.routeSuggestion {
  background-color: #bdb1ff;
}
.routeSuggestion:before {
  border-top: 22px solid #bdb1ff;
  border-bottom: 22px solid #bdb1ff;
  border-right: 22px solid #bdb1ff;
  border-left: 22px solid transparent;
}
.routeSuggestion:after {
  border-left: 20px solid #bdb1ff;
}
.personalize {
  background-color: #ffc490;
}
.personalize:before {
  border-top: 22px solid #ffc490;
  border-bottom: 22px solid #ffc490;
  border-right: 22px solid #ffc490;
  border-left: 22px solid transparent;
}
.personalize:after {
  border-left: 20px solid #ffc490;
}
/* .classDefinition:before {
  border-top: 20px solid #3ec28f;
  border-bottom: 20px solid #3ec28f;
  border-right: 20px solid #3ec28f;
  border-left: 20px solid transparent;
} */
.summary {
  background-color: #bdb1ff;
}
.summary:before {
  border-top: 22px solid #bdb1ff;
  border-bottom: 22px solid #bdb1ff;
  border-right: 22px solid #bdb1ff;
  border-left: 22px solid transparent;
}
.summary:after {
  border-left: 20px solid #bdb1ff;
}
.v-breadcrumbs__divider {
  padding: 0px !important;
}
.link-disabled .breadcrumb{
  background-color: grey;
}
.link-disabled .breadcrumb:after{
  border-left: 20px solid grey;
}
.link-disabled .breadcrumb:before{
  border-top: 22px solid grey;
  border-bottom: 22px solid grey;
  border-right: 22px solid grey;
  border-left: 22px solid transparent;}
.actual {
    border-bottom:solid 1px grey;;
  }
</style>
