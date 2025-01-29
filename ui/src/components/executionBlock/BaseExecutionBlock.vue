<template>
    <VueDragResize :isActive="true" :w="200" :h="200" @resizing="resizeHandler">

        <slot></slot>
    </VueDragResize>
</template>


<script lang="js">
import VueDragResize from 'vue-drag-resize';

export default {
    components: {
        VueDragResize,
    },
    methods: {
        resizeHandler(newRect) {
            // 处理调整大小后的逻辑
        },
    }
}
</script>