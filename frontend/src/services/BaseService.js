import axios from 'axios'
import keycloak from '../keycloak'

export const resources = {
  accounts: {
    transferAccount: '/accountTransfer',
  },
}

export function apiRequest() {
  function createAxios() {
    const baseURL = config.SERVICE_URL || import.meta.env.VITE_SERVICE_URL
    return axios.create({
      baseURL,
      headers: { Authorization: 'Bearer ' + keycloak.token },
    })
  }
  return keycloak.updateToken(0).then(createAxios)
}

export default apiRequest
