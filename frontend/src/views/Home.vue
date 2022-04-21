<template>
  <section>
    <h1>Welcome to the Account Transfer application</h1>
    <p>
      To transfer your account to the new MSP Direct please enter your HealthNetBC Username and Password.
      <br/>
      <em>A HealthNetBC Username will typically have a format similar to: 12345-asmith</em>
    </p>

    <form @submit.prevent="submitForm">
      <AppRow>
        <AppCol class="col3">
          <AppInput :e-model="v$.username" id="username" label="Username" type="text" v-model.trim="username" />
        </AppCol>
      </AppRow>
      <AppRow>
        <AppCol class="col3">
          <AppInput :e-model="v$.password" id="password" label="Password" :type="passwordVisible ? 'password' : 'input'" v-model.trim="password" />
          <span class="password-toggle" @click="passwordVisible = !passwordVisible">
             <font-awesome-icon :icon="passwordVisible ? 'eye' : 'eye-slash'" aria-hidden="true"/>
          </span>
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
      v$: useVuelidate()
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
      await this.v$.$validate()
      this.submitting = true

      AccountTransferService.transferAccount({
        username: this.username,
        password: this.password,
        application: 'MSPDIRECT-SERVICE'
      }).then(response => {
        let responseBody = response.data

        if (responseBody.status === 'success') {
          this.alert.setSuccessAlert(responseBody.message)
        } else if (responseBody.status === 'error') {
          this.alert.setErrorAlert(responseBody.message)
        }
      }).catch(error => {
        this.alert.setErrorAlert(`${error}`)
      }).finally(() => {
            this.submitting = false
      })
    }
  },
  validations() {
    return {
      username: {
        required
      },
      password: {
        required
      },
    }
  }
}
</script>
<style>
.password-toggle {
  position: relative;
  bottom: 45%;
  left: 90%;
  cursor: pointer;
}
</style>
