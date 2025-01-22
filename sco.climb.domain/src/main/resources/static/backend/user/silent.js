
var mgr = new Oidc.UserManager(auth_conf);
mgr.signinSilent().then(function (user) {
    debugger;
    console.log(user);
    window.sessionStorage.setItem('user', JSON.stringify(user))
}
)
