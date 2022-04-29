<template>
  <div class="alert-container bc-gov-alertbanner bc-gov-alertbanner-error" v-if="visible">
    <div class="alert-icon">
      <font-awesome-icon icon="exclamation-circle"></font-awesome-icon>
    </div>
    <div class="alert-message">
      <p>{{ message || DEFAULT_ERROR_MESSAGE }}</p>
      <p class="error-additional-info" v-html="additionalInfo" />
    </div>
    <div class="alert-close">
      <a @click="$emit('close')">
        <font-awesome-icon icon="times" />
      </a>
    </div>
  </div>
</template>

<script>
import { DEFAULT_ERROR_MESSAGE } from '../../util/constants.js'

export default {
  name: 'AppError',
  data() {
    return {}
  },
  props: {
    additionalInfo: {
      type: String,
      default: '',
    },
    message: {
      type: String,
      default: DEFAULT_ERROR_MESSAGE,
    },
    visible: {
      type: Boolean,
      default: true,
    },
  },
  emits: ['close'],
  methods: {
    close() {
      this.$emit('close')
    },
  },
}
</script>

<style scoped>
.alert-container {
  align-items: center;
  display: flex;
}

.alert-icon {
  display: flex;
  flex: 0;
  justify-content: flex-start;
  width: 150px;
}
.alert-message {
  flex: 1;
  justify-content: flex-start;
  vertical-align: middle;
  /* white-space: pre-wrap; */
}

.error-additional-info {
  font-weight: normal;
}

.alert-close {
  display: flex;
  flex: 0;
  justify-content: flex-end;
  width: 150px;
}
.alert-close a {
  cursor: pointer;
}

.bc-gov-alertbanner {
  border: 1px solid transparent;
  border-radius: 4px;
  font-weight: 700;
  margin-bottom: 20px;
  padding: 15px;
}
.bc-gov-alertbanner p {
  font-size: 18px;
  margin: 0;
  padding-left: 35px;
}

.bc-gov-alertbanner-error {
  background-color: #f2dede;
  border-color: #ebccd1;
  color: #a12622;
}

.bc-gov-alertbanner-error a {
  color: #a12622;
}

.bc-gov-alertbanner-error p {
  color: #843534;
}
</style>
