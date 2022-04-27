<template>
  <section>
    <h1>MSP Direct Account Transfer</h1>
    <p>
      You have successfully transferred your MSP Direct Account.<br />
      Going forward you will login to MSP Direct with your {{ identityProvider }} credential. You may wish to bookmark this link for future use.<br />
      Click below to continue to MSP Direct.
    </p>
    <AppButton><a :href="mspDirectURL" target="_blank">Login to MSP Direct</a></AppButton>
  </section>
</template>

<script>
export default {
  name: 'confirmation',
  computed: {
    identityProvider() {
      const idp = this.$keycloak.tokenParsed.identity_provider
      // The IDP alias in keycloak doesn't always match what's known by users
      // Formatted to match standard naming conventions
      let formattedIdentityProviders = {
        phsa: 'Health Authority',
        idir: 'IDIR',
        bceid_business: 'Business BCeID',
        bcsc: 'BC Services Card',
        moh_idp: 'Keycloak',
      }
      return formattedIdentityProviders[idp] || idp
    },
    mspDirectURL() {
      return config.MSP_DIRECT_URL || import.meta.env.VITE_MSP_DIRECT_URL
    },
  },
}
</script>
