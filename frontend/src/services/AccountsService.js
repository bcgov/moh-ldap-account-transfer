import { createAxios, resources } from './BaseService'

export default {
  getAccount(request) {
    // TODO (dbarrett) When available (keycloak has been enabled) replace with post call for apiRequest
    return createAxios()
      .get(resources.accounts.getAccount + request.name)
      .then((res) => res)
  },
}
