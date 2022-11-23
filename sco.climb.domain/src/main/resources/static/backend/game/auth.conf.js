 var auth_conf = {
    authority: "https://aac.platform.smartcommunitylab.it/",
    client_id: "c_104137f7-0049-40ae-811e-ad33eb59fd36",
    redirect_uri: window.location.protocol + "//" + window.location.host + "/v3/domain/backend/game/callback.html",
    post_logout_redirect_uri: window.location.protocol + "//" + window.location.host + "/v3/domain/backend/game/index.html",
    silent_redirect_uri: window.location.protocol + "//" + window.location.host + "/v3/domain/backend/game/silent.html",
    response_type: "token id_token",
    scope: "openid email profile",
    automaticSilentRenew: false,
    accessTokenExpiringNotificationTime: 10,
    filterProtocolClaims: true,
    loadUserInfo: true
}