import { createRouter, createWebHistory } from 'vue-router'

import Confirmation from './../views/Confirmation.vue'
import Home from './../views/Home.vue'
import Notification from './../views/Notification.vue'

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
    path: '/notification',
    name: 'Notification',
    component: Notification,
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
