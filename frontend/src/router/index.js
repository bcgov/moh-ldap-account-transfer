import { createRouter, createWebHistory } from 'vue-router'

import Confirmation from './../views/Confirmation.vue'
import Home from './../views/Home.vue'
import AlreadyTransferred from './../views/AlreadyTransferred.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/confirmation',
    name: 'Confirmation',
    component: Confirmation,
  },
  {
    path: '/alreadyTransferred',
    name: 'AlreadyTransferred',
    component: AlreadyTransferred,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { left: 0, top: 0 }
  },
})

export default router
