var mgr = new Oidc.UserManager(auth_conf);
mgr.signinRedirectCallback().then(function (user) {
    console.log(user);
    window.localStorage.setItem('user', JSON.stringify(user));
    if (window.localStorage.getItem('state') && JSON.parse(window.localStorage.getItem('state')).href) {
        window.history.replaceState({},
            window.document.title,
            window.localStorage.getItem('state').href);
        window.location = JSON.parse(window.localStorage.getItem('state')).href;
    }
    else {
        window.history.replaceState({},
            window.document.title,
            window.location.origin + window.location.pathname);
        window.location = "index.html";
    }
});