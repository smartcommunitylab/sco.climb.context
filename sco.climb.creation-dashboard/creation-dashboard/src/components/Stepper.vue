<template>
  <!--<div>
    <v-row class="text-center">
      <v-col col="12">
        <v-breadcrumbs :items="items" divider="">
          <template v-slot:item="{ item }">
            <v-breadcrumbs-item :href="item.href" :disabled="item.disabled" active-class="link-disabled" :class="{'actual':isActualPath(item)}">
              <div class="col breadcrumb" :class="item.class">
                <span>{{ item.text.toUpperCase() }}</span>
              </div>
            </v-breadcrumbs-item>
          </template>
        </v-breadcrumbs>
      </v-col>
    </v-row>
  </div>
  <div v-show="!['home'].includes($route.name)" class="divStepper">-->
    
    <div>
    <v-stepper alt-labels class="stepper elevation-0">
      <v-stepper-header>
        <v-stepper-step step="1">
          <p v-html="$t('stepper.step1')" class="step"></p>
        </v-stepper-step>
        <v-divider></v-divider>

        <v-stepper-step step="2">
          <p v-html="$t('stepper.step2')" class="step"></p>
        </v-stepper-step>

        <v-divider></v-divider>

        <v-stepper-step step="3">
          <p v-html="$t('stepper.step3')" class="step"></p>
        </v-stepper-step>
        
        <v-divider></v-divider>

        <v-stepper-step step="4">
          <p v-html="$t('stepper.step4')" class="step"></p>
        </v-stepper-step>

        <v-divider></v-divider>

        <v-stepper-step step="5">
          <p v-html="$t('stepper.step5')" class="step"></p>
        </v-stepper-step>
        
        <v-divider></v-divider>

        <v-stepper-step step="6" class="step">
          <p v-html="$t('stepper.step6')"></p>
        </v-stepper-step>
      </v-stepper-header>
    </v-stepper>
  </div>
</template>

<script>
import { mapState } from "vuex";
export default {
  name: "Stepper",
  data() {
    return {
      snackbar: false,
      snackBarText: "",
    }
  },
  computed: {
    ...mapState("navigation", ["page","items"]),
    ...mapState("game", ["currentGame"]),
    currentRouteName() {
        return this.$route.name;
    }
  },
  created() {},
  methods: {
    isActualPath(path){
      return this.currentRouteName===path.href
    },
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
