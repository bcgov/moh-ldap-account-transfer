import '@bcgov/bc-sans/css/BCSans.css'

import { faCheckCircle, faExclamationCircle, faExclamationTriangle, faSpinner, faTimes } from '@fortawesome/free-solid-svg-icons'

import App from './App.vue'
import AppButton from './components/ui/AppButton.vue'
import AppCol from './components/grid/AppCol.vue'
import AppInput from './components/ui/AppInput.vue'
import AppOutput from './components/ui/AppOutput.vue'
import AppRow from './components/grid/AppRow.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { library } from '@fortawesome/fontawesome-svg-core'
import router from './router'

const app = createApp(App)

// add global components here
app.component('AppCol', AppCol)
app.component('AppRow', AppRow)
app.component('AppInput', AppInput)
app.component('AppOutput', AppOutput)
app.component('AppButton', AppButton)

app.component('font-awesome-icon', FontAwesomeIcon)

// add libraries here
library.add(faCheckCircle)
library.add(faExclamationCircle)
library.add(faExclamationTriangle)
library.add(faSpinner)
library.add(faTimes)

// additions to the vue framework
app.use(router)
app.use(createPinia())

app.mount('#app')
