<template>
    <NodeResizer min-width="100" min-height="30" />
    <Handle type="target" :position="Position.Top" />
    <div class="node">{{ data.label }}</div>
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

<style lang="css">
.node {
    padding: 10px;
    border-radius: 3px;
    width: 128px;
    font-size: 12px;
    text-align: center;
    border-width: 1px;
    border-style: solid;
    color: #222;
    background-color: #fff;
    border-color: #222;
}

.node.draggable {
    cursor: grab;
    pointer-events: all;
}

.node:hover {
    box-shadow: 0 1px 4px 1px rgba(0, 0, 0, 0.08);
}
</style>