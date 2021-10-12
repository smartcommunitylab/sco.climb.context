<template>
    <div class="test">
        <v-btn @click="alert('ciao')">
            pulsante 1
        </v-btn>
        <slot>
            ciao
        </slot>
        <v-btn @click="alert('ciaone')">
            pulsante 2
        </v-btn>
    </div>    
</template>

<script>
export default {
methods:{
    alert(stringa){
        console.log(stringa);
    }
}
}
</script>

<style>
.test {
    width: 100px;
    height: 100px;
    background-color: red;
    border: solid 2px black;
}
</style>
