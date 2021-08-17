import Vue from 'vue';
import Router from 'vue-router';
import Login from "@/pages/login/Login.vue";
import NotFound from '@/pages/NotFound.vue';
import { vuexOidcCreateRouterMiddleware } from 'vuex-oidc'
import { store } from './store'

Vue.use(Router);
const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('./pages/about/About.vue')
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('./pages/home/Home.vue')
  },
  {
    path: '/classDefinition',
    name: 'classDefinition',
    component: () => import('./pages/classDefinition/classDefinition.vue')
  },
  {
    path: '/habitsDefinition',
    name: 'habitsDefinition',
    component: () => import('./pages/habitsDefinition/habitsDefinition.vue')
  },
  {
    path: '/routeSuggestion',
    name: 'routeSuggestion',
    component: () => import('./pages/routeSuggestion/routeSuggestion.vue')
  },
  {
    path: '/routePersonalization',
    name: 'routePersonalization',
    component: () => import('./pages/routePersonalization/routePersonalization.vue')
  },
  {
    path: '/routeCreation',
    name: 'routeCreation',
    component: () => import('./pages/routeCreation/routeCreation.vue')
  },
  {
    path: '/summary',
    name: 'summary',
    component: () => import('./pages/summary/summary.vue')
  },
  {
    path: '/oidc-callback', // Needs to match redirectUri (redirect_uri if you use snake case) in you oidcSettings
    name: 'oidcCallback',
    component: () => import('./pages/OidcCallback.vue')
  },
  {
    path: '/404',
    component: NotFound

  },
  {
    path: '*',
    redirect: '/404'
  },

];

export const router = new Router({
  mode: 'history',
  routes
})
router.beforeEach(vuexOidcCreateRouterMiddleware(store))

    // router.beforeEach((to, from, next) => {
    //   // redirect to login page if not logged in and trying to access a restricted page
    //   const publicPages = ['/login'];
    //   const authRequired = !publicPages.includes(to.path);
    //   const loggedIn = localStorage.getItem('user');

    //   if (authRequired && !loggedIn && to.name!='resetpwd') {
    //     return next('/login');
    //   }

    //   next();
    // })