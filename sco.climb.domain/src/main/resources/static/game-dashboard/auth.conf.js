 var auth_conf = {
    authority: "https://aac.platform.smartcommunitylab.it/",
    client_id: "c_fc44aa82a6b04c2099b8d81058b47ee6",
    redirect_uri:  window.location.protocol + "//" + window.location.host + "/domain/game-dashboard/callback.html",
    post_logout_redirect_uri: window.location.protocol + "//" + window.location.host + "/domain/game-dashboard/signout.html",
    silent_redirect_uri: window.location.protocol + "//" + window.location.host + "/domain/game-dashboard/silent.html",
    response_type: "token id_token",
    scope: "openid email profile",
    automaticSilentRenew: true,
    accessTokenExpiringNotificationTime: 10,
    filterProtocolClaims: true,
    loadUserInfo: true
}