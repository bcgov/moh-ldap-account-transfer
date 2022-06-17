<template>
  <Transition>
    <AppWarningPanel :visible="displayWarning" :warningMessage="warningMessage" @close="handleClose()" />
  </Transition>
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
      <AppErrorPanel :visible="displayError" :message="errorMessage" :additionalInfo="additionalInfo" @close="handleClose()" />
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
import AppErrorPanel from '../components/ui/AppErrorPanel.vue'
import AppWarningPanel from '../components/ui/AppWarningPanel.vue'
import AccountTransferService from '../services/AccountTransferService'
import keycloak from '../keycloak'

export default {
  name: 'home',
  components: { AppErrorPanel, AppWarningPanel },
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
      displayError: false,
      displayWarning: false,
      warningMessage: '',
      errorMessage: '',
      additionalInfo: '',
    }
  },

  created() {
    var org_details = keycloak.tokenParsed.hasOwnProperty('org_details')
    var old_ldap_id = keycloak.tokenParsed.hasOwnProperty('old_ldap_id')
    var roleExist = false

    // Check if LDAP account has already been transferred and old uid exists in keycloak
    if (old_ldap_id) {
      this.$router.push({
        name: 'Notification',
      })
      return
    }

    // Check if role has been transferred
    if (keycloak.tokenParsed.hasOwnProperty('resource_access')) {
      if (keycloak.tokenParsed.resource_access.hasOwnProperty('MSPDIRECT-SERVICE')) {
        JSON.parse(JSON.stringify(keycloak.tokenParsed.resource_access), (key, value) => {
          if (key === 'roles' && value.length !== 0) {
            roleExist = true
          }
        })
      }
    }

    // Account has partially been transferred
    if ((roleExist && !org_details) || (!roleExist && org_details)) {
      this.displayWarning = true
      const message = 'Your MSP Direct Account has been partially transferred. Please complete the transfer below. If this error persists, please contact <a href="https://HLTH.HelpDesk.hlth.gov.bc.ca" target="_blank">HLTH.HelpDesk.gov.bc.ca</a>'

      this.showWarning(message)
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
        if (responseBody.status === 'error') {
          const errorMessage = responseBody.message
          let additionalInfo = ''
          if (errorMessage.startsWith('Invalid Username')) {
            additionalInfo =
              'If you forgot your password please try the following link: <a href="https://healthnetbc.hlth.gov.bc.ca/?resetPassword" target="_blank">https://healthnetbc.hlth.gov.bc.ca/?resetPassword</a><br/>If you are still having trouble, please contact the helpdesk at <b>(250) 952-1234</b> or <b>hlth.helpdesk@gov.bc.ca</b>'
          } else if (errorMessage.startsWith('User has no role')) {
            additionalInfo = 'Please contact your access administrator to confirm your access to MSP Direct'
          } else {
            additionalInfo = 'If you believe you are seeing this message in error, please contact the helpdesk at: <b>(250) 952-1234</b> or <b>hlth.helpdesk@gov.bc.ca</b>'
          }
          this.showError(errorMessage, additionalInfo)
          this.clearUserPass()
        }
        if (responseBody.status === 'success') {
          //Navigate to the Confirmation
          this.alertStore.setSuccessAlert(responseBody.message)
          this.$router.push({ name: 'Confirmation' })
        }
      } catch (error) {
        this.showError(error)
        this.clearUserPass()
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
    },
    showWarning(warningMessage) {
      this.displayWarning = true
      this.warningMessage = warningMessage
    },
    clearUserPass() {
      this.username = ''
      this.password = ''
      // The Username/Password have been cleared but we don't want to trigger immediate validation
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
.v-enter-active,
.v-leave-active {
  transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>
