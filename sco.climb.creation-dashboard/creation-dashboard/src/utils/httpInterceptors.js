import axios from 'axios';
import {store} from '../store/index'
const actionScope = `loader`;

export function setupInterceptors({ dispatch }) {
  let requestsPending = 0;
  const req = {
    pending: () => {
      requestsPending++;
      dispatch(`${actionScope}/show`);
    },
    done: () => {
      requestsPending--;
      if (requestsPending <= 0) {
        dispatch(`${actionScope}/hide`);
      }
    }
  };
  axios.interceptors.request.use(
    config => {
      let token =store.getters['oidcStore/oidcAccessToken'];

      if (token) {
        config.headers['Authorization'] = 'Bearer ' + token;
      }
      req.pending();
      return config;
    },
    error => {
      requestsPending--;
      req.done();
      return Promise.reject(error);
    }
  );
  axios.interceptors.response.use(
    ({ data }) => {
      req.done();
      return Promise.resolve(data);
    },
    error => {
      req.done();
      return Promise.reject(error);
    }
  );
}