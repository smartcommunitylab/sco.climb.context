
var mgr = new Oidc.UserManager(auth_conf);
localStorage.clear();
sessionStorage.clear()
window.location = window.location.protocol + "//" + window.location.host + "/v3/game-dashboard/index.html"
