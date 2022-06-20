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
import AccountTransferService from '../services/AccountTransferService'

export default {
  name: 'home',
  components: { AppErrorPanel },
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
      errorMessage: '',
      additionalInfo: '',
    }
  },

  created() {
    let hasOrgDetails = this.$keycloak.tokenParsed.hasOwnProperty('org_details')
    let hasOldLdapId = this.$keycloak.tokenParsed.hasOwnProperty('old_ldap_id')
    let roleExist = false
    let audience = ''

    // Check if LDAP account has already been transferred and old uid exists in keycloak
    if (hasOldLdapId) {
      this.$router.push({
        name: 'AlreadyTransferred',
      })
      return
    }
    // Check if audience exists
    if (this.$keycloak.tokenParsed.hasOwnProperty('aud')) {
      const aud = this.$keycloak.tokenParsed.aud
      if (typeof aud === 'string') {
        audience = aud.startsWith('MSPDIRECT-SERVICE') ? aud : ''
      } else {
        audience = aud.find((element) => element.startsWith('MSPDIRECT-SERVICE'))
      }
    }
    // Check if role has been transferred
    if (this.$keycloak.tokenParsed.hasOwnProperty('resource_access')) {
      if (this.$keycloak.tokenParsed.resource_access.hasOwnProperty(audience)) {
        const mspDirect = this.$keycloak.tokenParsed.resource_access[audience]
        if (mspDirect.roles && mspDirect.roles.length > 0) {
          roleExist = true
        }
      }
    }
    // Check if LDAP Account has partially been transferred
    if ((roleExist && !hasOrgDetails) || (!roleExist && hasOrgDetails)) {
      const warningMessage = 'Your MSP Direct Account has been partially transferred. Please complete the transfer below. If this error persists, please contact <a href="https://HLTH.HelpDesk.hlth.gov.bc.ca" target="_blank">HLTH.HelpDesk.gov.bc.ca</a>'

      this.alertStore.setWarningAlert(warningMessage)
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
            additionalInfo = 'If you forgot your password please try the following link: <a href="https://healthnetbc.hlth.gov.bc.ca/?resetPassword" target="_blank">https://healthnetbc.hlth.gov.bc.ca/?resetPassword</a><br/>If you are still having trouble, please contact the helpdesk at <b>(250) 952-1234</b> or <b>hlth.helpdesk@gov.bc.ca</b>'
          } else if (errorMessage.startsWith('User has no role')) {
            additionalInfo = 'Please contact your access administrator to confirm your access to MSP Direct'
          } else {
            additionalInfo = 'If you believe you are seeing this message in error, please contact the helpdesk at: <b>(250) 952-1234</b> or <b>hlth.helpdesk@gov.bc.ca</b>'
          }
          this.showError(errorMessage, additionalInfo)
          this.clearUserPass()
        }
        if (responseBody.status === 'success') {
          //Navigate to the Confirmation page on success
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
      this.displayWarning = false
    },
    showError(error, additionalInfo) {
      this.displayError = true
      this.errorMessage = error
      this.additionalInfo = additionalInfo
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
