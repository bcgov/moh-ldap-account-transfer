<template>
  <section>
    <h1>MSP Direct Account Transfer</h1>
    <p class="description">You have already transferred your account to MSP Direct.<br /></p>
    <p>Use the following link to go to the application:</p>
    <AppRow>
      <AppButton @click="goToMSP" mode="primary" type="button">MSP Direct</AppButton>
    </AppRow>
    <br />
    <p>If you need assistance, please contact the group administrator line at <b>(604) 683-7520.</b></p>
  </section>
</template>

<script>
export default {
  name: 'alreadyTransferred',
  methods: {
    goToMSP() {
      window.location.href = config.MSP_DIRECT_URL || import.meta.env.VITE_MSP_DIRECT_URL
    },
  },
}
</script>

<style scoped>
.description {
  font-weight: bold;
  font-size: 20px;
}
</style>
