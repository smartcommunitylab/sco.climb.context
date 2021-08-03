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
              </v-tooltip> </h4>

             
          </div>

          <div class="row">
            <div class="col-sm-1"></div>
            <div class="col-xs-10 col-sm-4 c-card-layout">
              <div class="row mx-0">
                <div class="col-12 col-xs-12">
                  <label for="">Class:</label>
                  <input type="text" class="c-input-field" />
                </div>
                <div class="col-12 col-sm-4">
                  <label for="">No. Student:</label>
                  <input
                    class="c-input-field"
                    type="number"
                    min="0"
                    value="14"
                    v-model="studentsNum"
                    @change="updateStudentsFields(studentsNum)"
                  />
                </div>
                <div
                  class="col-12 col-xs-12"
                  v-for="student in students"
                  :key="student.id"
                >
                  <label>{{ student.id }}</label>
                  <input
                    class="c-input-field"
                    type="text"
                    v-model="student.inputVal"
                  />
                </div>
              </div>
            </div>
            <div class="col-sm-4">
              <!-- Transistion Card goes here-->
              <Card-Percorso :free="true"></Card-Percorso>
            </div>
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
import CardPercorso from "@/components/Card-Percorso.vue";
export default {
  name: "classDefinition",
  components: {
    "Card-Percorso": CardPercorso,
  },
  data() {
    return {
      nomepagina: "Class Definition",
      studentsNum: 5,
      students: [],
    };
  },
  methods: {
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
          this.students.push({ inputVal: "name", id: lastM + 1 + i });
        }
      }
    },
    navigateHome() {
      this.$router.push("home");
    },
    goNext() {
      this.$router.push("habitsDefinition");
    },
  },
  mounted() {
    let loader = this.$loading.show({
      canCancel: false,
      backgroundColor: "#000",
      color: "#fff",
    });
    this.updateStudentsFields(this.studentsNum);
    setTimeout(() => {
      loader.hide();
    }, 500);
  },
};
</script>

<style>
.c-input-field {
  border: 1px solid #000;
  border-radius: 5px;
}

.c-card-layout {
  background: #f4f2f2;
  border-radius: 8px;
}
</style>

