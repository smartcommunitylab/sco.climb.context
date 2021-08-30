<template>
  <div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
export default {
  name: 'OidcPopupCallback',
  methods: {
    ...mapActions('oidcStore', [
      'oidcSignInPopupCallback'
    ])
  },
  mounted () {
      // workaround: problem with non-standard id_token response
    let idx = window.location.hash.indexOf('id_token=');
    window.location.hash = window.location.hash.substring(0,idx);
    this.oidcSignInPopupCallback()
      .catch((err) => {
        console.error(err) // Handle errors any way you want
      })
  }
}
</script>