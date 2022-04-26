<script setup>
import AppInputError from './AppInputError.vue'
</script>

<template>
  <div class="text_label">
    <label>{{ label }}</label>
  </div>

  <div class="passwordinput_wrapper">
    <input :class="inputClass" :value="modelValue" @input="$emit('update:modelValue', $event.target.value)" v-bind="$attrs" :type="passwordVisible ? 'text' : 'password'" />
    <span class="password_toggle" @click="passwordVisible = !passwordVisible">
      <font-awesome-icon :icon="passwordVisible ? 'eye-slash' : 'eye'" aria-hidden="true"/>
    </span>
  </div>

  <AppInputError :e-model="eModel" :label="label" />
</template>

<script>
export default {
  name: 'AppInput',
  data() {
    return {
      passwordVisible: false,
    }
  },
  props: {
    eModel: {
      type: Object,
      required: false,
    },
    label: String,
    modelValue: String,
  },
  computed: {
    inputClass() {
      return {
        text_input: true,
        'error-input': this.eModel?.$error,
      }
    },
  },
}
</script>

<style scoped>
.text_input {
  font-family: 'BCSans', 'Noto Sans', Verdana, Arial, sans-serif;
  font-size: 18px;
}

.text_input {
  height: 34px;
  border: 2px solid #606060;
  margin-top: 5px;
  margin-bottom: 15px;
  border-radius: 4px;
  padding: 5px 5px 5px 7px;
}

.text_input[type='text']:focus,
.text_input[type='password']:focus {
  outline: 2px solid #3b99fc;
  outline-offset: 1px;
}

.text_label {
  display: flex;
}

.error-input {
  border-color: #d8292f !important;
}

.passwordinput_wrapper {
  display: inline-flex;
  width: 100%;
}

.password_toggle {
  cursor: pointer;
  margin-left: -27px;
  margin-top: 11px;
}
</style>
