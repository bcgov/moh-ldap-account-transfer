<template>
  <div class="warning-container bc-gov-alertbanner bc-gov-alertbanner-warning" v-if="visible">
    <div class="warning">
      <font-awesome-icon icon="exclamation-triangle"></font-awesome-icon>
    </div>
    <div class="warning-message">
      <p class="warning-additional-info" v-html="warningMessage" />
    </div>
    <div class="warning-close">
      <a @click="$emit('close')">
        <font-awesome-icon icon="times" />
      </a>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AppWarningPanel',
  data() {
    return {}
  },
  props: {
    warningMessage: {
      type: String,
      default: '',
    },

    visible: {
      type: Boolean,
      default: true,
    },
  },
  emits: ['close'],
}
</script>

<style scoped>
.warning-container {
  align-items: center;
  display: flex;
}

.warning-icon {
  display: flex;
  flex: 0;
  justify-content: flex-start;
  width: 150px;
}
.warning-message {
  flex: 1;
  justify-content: flex-start;
  vertical-align: middle;
  /* white-space: pre-wrap; */
}

.warning-additional-info {
  font-weight: normal;
}

.warning-close {
  display: flex;
  flex: 0;
  justify-content: flex-end;
  width: 150px;
}
.warning-close a {
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

.bc-gov-alertbanner-warning {
  background-color: #f9f1c6;
  border-color: #faebcc;
  color: #3a4105;
}

.bc-gov-alertbanner-warning a {
  color: #3a4105;
}

.bc-gov-alertbanner-warning p {
  color: #3a4105;
}
</style>
