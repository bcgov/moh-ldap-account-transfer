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
    <Transition>
      <AppError :visible="displayError" :message="errorMessage" :additionalInfo="additionalInfo" @close="handleClose()" />
    </Transition>
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
import AppError from '../components/ui/AppError.vue'
import AccountTransferService from '../services/AccountTransferService'

export default {
  name: 'home',
  components: { AppError },
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
      displayError: false,
      errorMessage: '',
      additionalInfo: '',
    }
  },
  methods: {
    async submitForm() {
      this.displayError = false
      const isValid = await this.v$.$validate()
      if (!isValid) {
        this.showError()
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
        // Display additional information on error
        if (responseBody.status == 'error') {
          const errorMessage = responseBody.message
          let additionalInfo = ''
          if (errorMessage.startsWith('Invalid Username')) {
            additionalInfo = 'If you forgot your password please try the following link: TODO.<br/>If you are still having trouble please contact the helpdesk at 555-555-1234 or support@mspdirect.'
          } else {
            additionalInfo = 'If you believe you are seeing this message in error please contact the helpdesk at 555-555-1234 or support@mspdirect.'
          }
          this.showError(errorMessage, additionalInfo)
          // The Username/Password have been cleared but we don't want to trigger immediate validation
          this.v$.$reset()
        }
        // Navigate to the Confirmation page on success
        if (responseBody.status == 'success') {
          this.alertStore.setSuccessAlert(responseBody.message)
          this.$router.push({ name: 'Confirmation' })
        }
      } catch (error) {
        this.showError()
      } finally {
        this.submitting = false
      }
    },
    handleClose() {
      this.displayError = false
    },
    showError(error, additionalInfo) {
      this.displayError = true
      this.errorMessage = error
      this.additionalInfo = additionalInfo
      this.username = ''
      this.password = ''
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
.v-enter-active,
.v-leave-active {
  transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>
