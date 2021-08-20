<template>
  <div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'OidcCallback',
  methods: {
    ...mapActions([
      'oidcSignInCallback'
    ])
  },
  mounted () {
    // workaround: problem with non-standard id_token response
    let idx = window.location.hash.indexOf('id_token=');
    window.location.hash = window.location.hash.substring(0,idx);

    this.oidcSignInCallback()
      .then((redirectPath) => {
        this.$router.push(redirectPath)
      })
      .catch((err) => {
        console.error(err)
        this.$router.push('/oidc-callback-error') 
      })
  }
}
</script>