<template>
  <v-container class="grey lighten-5">
    <v-row no-gutters>
      <v-col>
        <v-card class="pa-2" outlined tile>
          <div class="pa-7">
            <p class="text-center text-h3">{{ nomepagina }}</p>
            <hr />
            <h2>Classi</h2>
            <h4>
              Inserendo il numero totale degli alunni verrà generata una lista
              di campi dove potranno essere inseriti i nicknames scelti dagli
              alunni e che servirà per la compilazione quotidiana del diario di
              mobilità. Questa modifica non è obbligatoria e può essere fatta a
              percorso già iniziato.
              <v-tooltip v-model="show" right
                ><template v-slot:activator="{ on, attrs }">
                  <v-btn icon v-bind="attrs" v-on="on">
                    <v-icon color="grey lighten-1"> mdi-open-in-new </v-icon>
                  </v-btn>
                </template>
                <span>See more</span>
              </v-tooltip>
            </h4>
          </div>

          <div class="row ma-0">
            <Card-Class></Card-Class>
            <div class="col-sm-1"></div>
            <v-btn
              class="ma-2"
              height="50px"
              width="200px"
              color="primary"
              @click="
                expand = !expand;
                isHidden = !isHidden;
              "
              v-show="isHidden"
            >
              Add another class
            </v-btn>

            <v-expand-transition>
              <Card-Class v-show="expand"></Card-Class>
            </v-expand-transition>

            <div class="col-sm-1"></div>
          </div>

          <div class="row py-6">
            <div class="col-sm-1"></div>
            <div class="col-sm-5">
              <v-btn class="float-left" @click="navigateHome()">Indietro</v-btn>
            </div>
            <div class="col-sm-5">
              <v-btn class="float-right" @click="goNext()">Avanti</v-btn>
            </div>
            <div class="col-sm-1"></div>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import CardClass from "@/components/Card-Class.vue";
export default {
  name: "classDefinition",
  components: {
    "Card-Class": CardClass,
  },
  data() {
    return {
      expand: false,
      expand2: false,
      isHidden: true,
      nomepagina: "Class Definition",
      studentsNum: 5,
      students: [],
    };
  },
  computed: {},
  methods: {
    ...mapActions("game", {
      createClass: "createClass",
    }),
        ...mapActions("navigation", {
      nextStep: "nextStep",
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
    navigateHome() {
      this.$router.push("home");
    },
    goNext() {
      this.$router.push("habitsDefinition");
      this.createClass({nome:"ciao",students:this.students})
      this.nextStep();
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

<style>
.c-input-field {
  padding: 0 !important;
  margin: 0 !important;
}
.c-card-layout {
  background: #f4f2f2;
  border-radius: 8px;
}
</style>

