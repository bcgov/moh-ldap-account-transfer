import { apiRequest, resources } from './BaseService'

export default {
  transferAccount(request) {
    return apiRequest().then(axiosInstance => axiosInstance.post(resources.accounts.transferAccount, request))
  },
}
