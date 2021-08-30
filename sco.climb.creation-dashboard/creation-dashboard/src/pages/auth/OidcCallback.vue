<template>
  <div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'OidcCallback',
  methods: {
      ...mapActions('oidcStore', [
      'oidcSignInCallback'
    ])
  },
  mounted () {
    // workaround: problem with non-standard id_token response
    let idx = window.location.hash.indexOf('id_token=');
    window.location.hash = window.location.hash.substring(0,idx);

    this.oidcSignInCallback()
        // eslint-disable-next-line no-unused-vars
      .then((redirectPath) => {
        // this.$router.push(redirectPath)
        this.$router.push({ name: 'home' });
      })
      .catch((err) => {
        console.error(err)
        this.$router.push('/oidc-callback-error') 
      })
  }
}
</script>