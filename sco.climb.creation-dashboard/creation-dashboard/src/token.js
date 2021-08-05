
oauth.serverUrl=${OAUTH_URL:https://am-test.smartcommunitylab.it/aac}
profile.serverUrl=${PROFILE_URL:https://am-test.smartcommunitylab.it/aac/}

rememberMe.key=${REMEMBERME_KEY:l22fc92f}

vlab.token=${VLAB_TOKEN:831a2cc0-48bd-46ab-ace1-c24f767af8af}

storage.s3.bucketName=climb-kgg

authorization.domain=climb
authorization.userType=user

security.oauth2.client.clientId=${OAUTH2_CLIENT_ID:202e666e-ee54-4143-b92b-e4b65ab00d1d}
security.oauth2.client.clientSecret=${OAUTH2_CLIENT_SECRET:05d5bc23-5feb-44d1-8845-0c3e3e6e3437}
security.oauth2.client.accessTokenUri=${OAUTH2_TOKEN_URI:https://am-test.smartcommunitylab.it/aac/oauth/token}
security.oauth2.client.userAuthorizationUri=${OAUTH2_AUTH_URI:https://am-test.smartcommunitylab.it/aac/eauth/authorize}
security.oauth2.client.tokenName=oauth_token
security.oauth2.client.authenticationScheme=header
security.oauth2.client.clientAuthenticationScheme=form
security.oauth2.client.scope=profile.accountprofile.me
security.oauth2.resource.userInfoUri=${OAUTH2_USER_URI:https://am-test.smartcommunitylab.it/aac/accountprofile/me}
security.oauth2.resource.preferTokenInfo=false

gamification.url=${GAMIFICATION_URL:https://dev.smartcommunitylab.it/gamification-v3}