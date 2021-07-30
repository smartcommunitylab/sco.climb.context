<template>
<div class="container-spinner">
<div class="loader ease-linear rounded-full border-8 border-t-8 border-gray-200 "></div>
</div>
</template>
<script>
export default {
  name: "loader",
  props: ["visible"]
};
</script>
<style>
.container-spinner{
    width: 100%;
    height: 100%;
    background-color: black;
    opacity: 0.8;
    position: absolute;
    z-index: 999;
}
.loader {
  border-top-color: #3498db;
  -webkit-animation: spinner 1.5s linear infinite;
  animation: spinner 1.5s linear infinite;
  position: fixed;
  top: 50%;
  left: 50%;
  margin-top: -50px;
  margin-left: -100px;
  width: 100px;
  height: 100px;
  }

@-webkit-keyframes spinner {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spinner {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>

