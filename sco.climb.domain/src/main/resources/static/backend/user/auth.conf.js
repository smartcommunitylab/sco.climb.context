 var auth_conf = {
    authority: "https://aac.platform.smartcommunitylab.it/",
    client_id: "c_104137f7-0049-40ae-811e-ad33eb59fd36",
    redirect_uri: window.location.protocol + "//" + window.location.host + "/v3/backend/user/callback.html",
    post_logout_redirect_uri: window.location.protocol + "//" + window.location.host + "/v3/backend/user/signout.html",
    silent_redirect_uri: window.location.protocol + "//" + window.location.host + "/v3/backend/user/silent.html",
    response_type: "token id_token",
    scope: "openid email profile",
    automaticSilentRenew: true,
    accessTokenExpiringNotificationTime: 10,
    filterProtocolClaims: true,
    loadUserInfo: true
}