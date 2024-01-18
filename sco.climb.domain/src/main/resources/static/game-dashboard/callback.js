var mgr = new Oidc.UserManager(auth_conf);
mgr.signinRedirectCallback().then(function (user) {
    console.log(user);
    window.sessionStorage.setItem('user', JSON.stringify(user));
    if (window.sessionStorage.getItem('state') && JSON.parse(window.sessionStorage.getItem('state')).href) {
        window.history.replaceState({},
            window.document.title,
            window.sessionStorage.getItem('state').href);
        window.location = JSON.parse(window.sessionStorage.getItem('state')).href;
    }
    else {
        window.history.replaceState({},
            window.document.title,
            window.location.origin + window.location.pathname);
        window.location = "index.html";
    }
});