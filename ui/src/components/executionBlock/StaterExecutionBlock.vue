<template>
    <NodeResizer min-width="100" min-height="30" />
    {{ data.label }}
    <Handle id="source" type="source" :position="Position.Bottom" :connectable="isHandleConnectable('source')" />
</template>

<script>
import { Handle, Position } from '@vue-flow/core';
import { NodeResizer } from '@vue-flow/node-resizer';

export default {
    components: {
        Handle,
        NodeResizer
    },
    props: {
        data: {
            type: Object
        }
    },
    data() {
        return {
            maxConnections: 1,
            Position
        }
    },
    computed: {
        isHandleConnectable() {
            return (handleId) => {
                return this.data[handleId] < this.maxConnections;
            };
        },
    },
    methods: {

    }
}
</script>

<style scoped></style>