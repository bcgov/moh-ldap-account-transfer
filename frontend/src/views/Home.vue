<template>
  <section>
    <h1>Welcome to the MSP Direct Account Transfer</h1>
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

export default {
  name: 'home',
  setup() {
    return {
      alert: useAlertStore(),
      v$: useVuelidate(),
    }
  },
  data() {
    return {
      username: '',
      password: '',
      submitting: false,
      passwordVisible: false,
    }
  },
  methods: {
    async submitForm() {
      const isValid = await this.v$.$validate()
      if (!isValid) {
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
        this.alert.setAlert({ message: responseBody.message, type: responseBody.status })
        // Navigate to the Confirmation page on success
        if (responseBody.status == 'success') {
          this.$router.push({ name: 'Confirmation' })
        }
      } catch (error) {
        this.alert.setErrorAlert(error)
      } finally {
        this.submitting = false
      }
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
