<template>
  <section>
    <h1>MSP Direct Account Transfer</h1>
    <h2>You have already transferred your account to MSP Direct</h2>
    <br />
    <p>Use the following link to go to the application</p>
    <AppRow>
      <AppButton @click="goToMSP" mode="primary" type="button">MSP Direct</AppButton>
    </AppRow>
    <br />
    <p>
      If you are experiencing access issues, please contact the Ministry HelpDesk:<br />
      <a :href="helpDeskDirectURL" target="_blank">{{ helpDeskDirectURL }}</a
      ><br />
    </p>
  </section>
</template>

<script>
export default {
  name: 'notification',
  computed: {
    helpDeskDirectURL() {
      return config.MSP_DIRECT_URL || import.meta.env.VITE_HELPDESK_URL
    },
  },
  methods: {
    goToMSP() {
      window.location.href = config.MSP_DIRECT_URL || import.meta.env.VITE_MSP_DIRECT_URL
    },
  },
}
</script>
