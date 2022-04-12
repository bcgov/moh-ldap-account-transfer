import { apiRequest, resources } from './BaseService'

export default {
  getAccount(request) {
    // TODO (dbarrett) When available (keycloak has been enabled) replace with post call for apiRequest
    return apiRequest().then(axiosInstance => axiosInstance.get(resources.accounts.getAccount + request.name))
  },
}
