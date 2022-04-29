<template>
  <section>
    <h1>Welcome to the MSP Direct Account Transfer</h1>
    <div id="error-help" v-if="systemError">
      <p>
        There was an error with your transfer request.<br />
        Please contact MSP Direct Helpdesk at:<br />
        Phone: 555-555-1234<br />
        Email: support@mspdirect
      </p>
    </div>
    <p>To transfer your MSP Direct permissions to your new account you will need to enter your HealthNetBC Username and Password.</p>
    <p>
      <b>Your HealthNetBC Username</b> <br />
      A HealthNetBC Username will typically have the format: <b>Org ID-FirstInitial, Last Name</b>
      <br />
      <em>Example: 1234-asmith</em>
    </p>
    <p>
      <b>Forgot your password?</b> <br />
      Before you start the account transfer process follow
      <a href="https://healthnetbc.hlth.gov.bc.ca/?resetPassword" target="_blank">this link</a>
      to reset your password
    </p>

    <form @submit.prevent="submitForm">
      <AppRow>
        <AppCol class="col3">
          <AppInput :e-model="v$.username" id="username" label="Username" type="text" v-model.trim="username" />
        </AppCol>
      </AppRow>
      <AppRow>
        <AppCol class="col3">
          <div>
            <AppPasswordInput :e-model="v$.password" id="password" label="Password" v-model.trim="password" />
          </div>
        </AppCol>
      </AppRow>
      <AppRow>
        <AppButton :submitting="submitting" mode="primary" type="submit">Submit</AppButton>
      </AppRow>
    </form>
  </section>
</template>

<script>
import useVuelidate from '@vuelidate/core'
import { useAlertStore } from '../stores/alert'
import { required } from '@vuelidate/validators'
import AccountTransferService from '../services/AccountTransferService'
import { ref } from 'vue'

export default {
  name: 'home',
  setup() {
    return {
      alertStore: useAlertStore(),
      v$: useVuelidate(),
    }
  },
  data() {
    return {
      username: '',
      password: '',
      submitting: false,
      passwordVisible: false,
      systemError: false,
    }
  },
  methods: {
    async submitForm() {
      this.alertStore.dismissAlert()
      const isValid = await this.v$.$validate()
      if (!isValid) {
        this.alertStore.setErrorAlert()
        return
      }
      this.submitting = true

      try {
        let responseBody = (
          await AccountTransferService.transferAccount({
            username: this.username,
            password: this.password,
            application: 'MSPDIRECT-SERVICE',
          })
        ).data
        this.alertStore.setAlert({ message: responseBody.message, type: responseBody.status })
        // Display additional information on error
        if (responseBody.status == 'error') {
          this.handleError()
        }
        // Navigate to the Confirmation page on success
        if (responseBody.status == 'success') {
          this.systemError = false
          this.$router.push({ name: 'Confirmation' })
        }
      } catch (error) {
        this.alertStore.setErrorAlert(error)
        this.handleError()
      } finally {
        this.submitting = false
      }
    },
    handleError() {
      this.systemError = true
      this.username = ''
      this.password = ''
      this.v$.$reset()
    },
  },
  validations() {
    return {
      username: {
        required,
      },
      password: {
        required,
      },
    }
  },
}
</script>
<style scoped>
#error-help {
  background-color: #f2dede;
  color: #a12622;
  border: 2px solid #ebccd1;
  border-radius: 4px;
  margin: 5px 0 5px 0;
  padding: 5px;
  /* background-color: #eeeeee;
  border: 2px solid #38598a;
  border-radius: 4px;
  margin: 5px 0 5px 0;
  padding: 5px; */
}
</style>
