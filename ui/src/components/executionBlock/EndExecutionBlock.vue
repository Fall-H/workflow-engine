<template>
    <NodeResizer min-width="100" min-height="30" />
    {{ data.label }}
    <Handle type="target" :position="Position.Top" />
</template>

<script setup>
import { Handle, Position } from '@vue-flow/core'
import { NodeResizer } from '@vue-flow/node-resizer'

defineProps(['data'])
</script>

<style scoped></style>