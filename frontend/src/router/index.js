import { createRouter, createWebHistory } from 'vue-router'

import Home from './../views/Home.vue'
import ViewAccount from './../views/accounts/ViewAccount.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/viewAccount',
    name: 'ViewAccount',
    component: ViewAccount,
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
