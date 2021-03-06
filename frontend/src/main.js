import '@bcgov/bc-sans/css/BCSans.css'

import { createPinia } from 'pinia'
import { createApp } from 'vue'

import { library } from '@fortawesome/fontawesome-svg-core'
import { faCheckCircle, faExclamationCircle, faExclamationTriangle, faEye, faEyeSlash, faSpinner, faTimes } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

import App from './App.vue'
import AppCol from './components/grid/AppCol.vue'
import AppRow from './components/grid/AppRow.vue'
import AppButton from './components/ui/AppButton.vue'
import AppInput from './components/ui/AppInput.vue'
import AppOutput from './components/ui/AppOutput.vue'
import AppPasswordInput from './components/ui/AppPasswordInput.vue'
import keycloak from './keycloak'
import router from './router'

keycloak.onAuthSuccess = async function () {
  initApp()
}

function initApp() {
  const app = createApp(App)

  // add global components here
  app.component('AppCol', AppCol)
  app.component('AppRow', AppRow)
  app.component('AppInput', AppInput)
  app.component('AppPasswordInput', AppPasswordInput)
  app.component('AppOutput', AppOutput)
  app.component('AppButton', AppButton)

  app.component('font-awesome-icon', FontAwesomeIcon)

  // add libraries here
  library.add(faCheckCircle)
  library.add(faExclamationCircle)
  library.add(faExclamationTriangle)
  library.add(faSpinner)
  library.add(faTimes)
  library.add(faEye)
  library.add(faEyeSlash)

  // additions to the vue framework
  app.use(router)
  app.use(createPinia())

  app.config.globalProperties.$keycloak = keycloak

  app.mount('#app')
}
