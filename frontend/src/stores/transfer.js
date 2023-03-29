import { defineStore } from 'pinia'

export const useTransferStore = defineStore('transfer', {
  state: () => {
    return {
      complete: false,
    }
  },
})
