<template>
  <div>
  </div>
</template>

<script>
import { mapActions,mapState } from 'vuex'

export default {
  name: 'OidcCallback',
  methods: {
      ...mapActions('account', [
      'login'
    ]),
          ...mapActions('oidcStore', [
      'oidcSignInCallback'
    ])
  },
  computed:{
    ...mapState("oidcStore", ["access_token"]),
  },
  mounted () {
    // workaround: problem with non-standard id_token response
    let idx = window.location.hash.indexOf('id_token=');
    window.location.hash = window.location.hash.substring(0,idx);

    this.oidcSignInCallback()
        // eslint-disable-next-line no-unused-vars
      .then((redirectPath) => {
        // this.$router.push(redirectPath)
        // store.dispatch('account/login', {access_token:user.access_token}, {root:true})
        // this.$router.push({ name: 'home' });
        // alert(this.access_token)
        this.login({access_token:this.access_token})

      })
      .catch((err) => {
        console.error(err)
        this.$router.push('/oidc-callback-error') 
      })
  }
}
</script>