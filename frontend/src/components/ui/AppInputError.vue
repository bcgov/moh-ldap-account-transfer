<template>
  <div class="error-text" v-for="error in eModel?.$errors">
    {{ error.$message.replace('Value', label) }}
  </div>
</template>

<script>
export default {
  name: 'AppInputError',
  props: {
    eModel: Object,
    label: String,
  },
}
</script>

<style scoped>
.error-text {
  color: #d8292f;
  margin-top: -15px;
}
</style>
