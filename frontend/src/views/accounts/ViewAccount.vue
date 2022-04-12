<template>
  <div>
    <form @submit.prevent="submitForm">
      <AppRow>
        <AppCol class="col3">
          <AppInput :e-model="v$.name" id="name" label="Name" type="text" v-model.trim="name" />
        </AppCol>
      </AppRow>
      <AppRow>
        <AppButton :submitting="searching" mode="primary" type="submit">Submit</AppButton>
        <AppButton @click="resetForm" mode="secondary" type="button">Clear</AppButton>
      </AppRow>
    </form>
  </div>
  <br />
  <div id="result" v-if="searchOk">
    <hr />
    <AppRow>
      <AppCol class="col3">
        <AppOutput label="id" :value="result.id" />
      </AppCol>
    </AppRow>
  </div>
</template>

<script>
import { useAlertStore } from '../../stores/alert'
import AccountsService from '../../services/AccountsService'
import useVuelidate from '@vuelidate/core'
import { required } from '@vuelidate/validators'

export default {
  name: 'ViewAccount',
  setup() {
    const alert = useAlertStore()
    return {
      alert,
      v$: useVuelidate(),
    }
  },
  data() {
    return {
      name: '',
      searching: false,
      searchOk: false,
      result: {
        id: '',
      },
    }
  },
  methods: {
    async submitForm() {
      this.result = null
      this.searching = true
      this.searchOk = false
      this.alert.dismissAlert()
      try {
        const isValid = await this.v$.$validate()
        if (!isValid) {
          this.showError()
          return
        }
        this.result = (
          await AccountsService.getAccount({
            name: this.name,
          })
        ).data

        if (this.result.status === 'error') {
          this.alert.setErrorAlert(this.result.message)
          return
        }

        this.searchOk = true
        if (this.result.status === 'success') {
          this.alert.setSuccessAlert(this.result.message || 'Update successful')
        } else if (this.result.status === 'warning') {
          this.alert.setWarningAlert(this.result.message)
        }
      } catch (err) {
        this.alert.setErrorAlert(err)
      } finally {
        this.searching = false
      }
    },
    showError(error) {
      this.alert.setErrorAlert(error)
      this.result = {}
      this.searching = false
    },
    resetForm() {
      this.name = ''
      this.result = null
      this.v$.$reset()
      this.alert.dismissAlert()
      this.searchOk = false
      this.searching = false
    },
  },
  validations() {
    return {
      name: {
        required,
      },
    }
  },
}
</script>
