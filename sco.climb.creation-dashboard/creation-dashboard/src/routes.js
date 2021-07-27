import Vue from 'vue';
import Router from 'vue-router';
import Login from "./pages/login/Login.vue";
Vue.use(Router);
const routes = [
    {
      path: '/',
      redirect: '/login' 
    },
    {
      path: '/',
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
      component: () => import('./pages/Home.vue')
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
    routes})
    router.beforeEach((to, from, next) => {
      // redirect to login page if not logged in and trying to access a restricted page
      const publicPages = ['/login'];
      const authRequired = !publicPages.includes(to.path);
      const loggedIn = localStorage.getItem('user');
    
      if (authRequired && !loggedIn && to.name!='resetpwd') {
        return next('/login');
      }
    
      next();
    })