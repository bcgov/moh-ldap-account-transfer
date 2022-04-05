import { DEFAULT_ERROR_MESSAGE } from '../util/constants.js'
import { defineStore } from 'pinia'

export const useAlertStore = defineStore('alert', {
  state: () => {
    return {
      message: '',
      type: 'success',
      active: false,
    }
  },
  /*
   Note, state could also be defined as 
    state: () => ({ count: 0 }) 
    but use the chosen format to infer type
   */
  actions: {
    setErrorAlert(message) {
      this.message = message || DEFAULT_ERROR_MESSAGE
      this.type = 'error'
      this.active = true
    },
    setErrorAlerts(messages) {
      const message = messages.join('\n')
      this.message = message
      this.type = 'error'
      this.active = true
    },
    setInfoAlert(message) {
      this.message = message
      this.type = 'info'
      this.active = true
    },
    setSuccessAlert(message) {
      this.message = message
      this.type = 'success'
      this.active = true
    },
    setWarningAlert(message) {
      this.message = message
      this.type = 'warning'
      this.active = true
    },
    dismissAlert() {
      this.active = false
    },
  },
})
