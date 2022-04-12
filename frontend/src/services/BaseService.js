import axios from 'axios'

export const resources = {
  accounts: {
    getAccount: '/accounts/',
  },
}

// TODO (dbarrett) Add keycloak request, will be done when keycloak is added to the project
export function createAxios() {
  const baseURL = config.SERVICE_URL || import.meta.env.VITE_SERVICE_URL
  return axios.create({
    baseURL,
  })
}

export default createAxios
