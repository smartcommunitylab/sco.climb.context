export const oidcSettings = {
  authority: 'https://am-test.smartcommunitylab.it/aac',
  clientId: '202e666e-ee54-4143-b92b-e4b65ab00d1d',
  redirectUri: 'http://localhost:8080/oidc-callback',
  popupRedirectUri: "http://localhost:8080/oidc-popup-callback",
  // automaticSilentRenew: true,
  //  automaticSilentSignin: false,
    // silentRedirectUri: "http://localhost:5002/silent-renew-oidc.html",
  responseType: 'token',
  scope: 'profile.basicprofile.me profile.accountprofile.me',
  loadUserInfo: false
}

