
var mgr = new Oidc.UserManager(auth_conf);
mgr.signinSilent().then(function (user) {
    debugger;
    console.log(user);
    window.localStorage.setItem('user', JSON.stringify(user))
}
)
