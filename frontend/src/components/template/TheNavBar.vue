<template>
  <nav role="navigation">
    <div class="container">
      <ul>
        <li id="home-link" :class="tabClass($route, 'Home')">
          <router-link @click="resetAlert" :to="{ name: 'Home' }">Home</router-link>
        </li>
        <li id="view-accounts-link" :class="tabClass($route, 'ViewAccount')">
          <router-link @click="resetAlert" :to="{ name: 'ViewAccount' }">View Account</router-link>
        </li>
      </ul>
    </div>
  </nav>
</template>

<script>
import { useAlertStore } from '../../stores/alert'
export default {
  name: 'TheNavBar',
  setup() {
    const alert = useAlertStore()
    return {
      alert,
    }
  },
  methods: {
    resetAlert() {
      this.alert.dismissAlert()
    },
    tabClass(route, routeName) {
      return route.name === routeName ? 'active' : 'inactive'
    },
  },
}
</script>

<style scoped>
/* Main Navigation */
nav {
  height: 40px;
  background-color: #38598a;
}
nav .container {
  max-width: 1320px;
  min-width: 1100px;
  margin: 0 auto;
  padding: 0 60px;
}
nav .container ul {
  padding: 0;
}
nav .container ul li {
  color: #ffffff;
  display: inline-block;
  height: 40px;
  line-height: 40px;
  vertical-align: top;
  font-weight: bold;
}
nav .container ul li.active {
  background-color: #496da2;
}
nav .container ul li:first-child {
  margin-left: -20px;
}
nav .container ul li:hover {
  cursor: pointer;
  background-color: #496da2;
}
nav .container ul li a {
  display: block;
  font-size: 1rem;
  line-height: 40px;
  color: #ffffff;
  text-decoration: none;
  height: 40px;
  padding: 0 20px;
}
nav .container ul li a:focus {
  background-color: #496da2;
}
</style>
