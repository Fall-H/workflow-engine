<template>
    <!-- <BaseExecutionBlock> -->
        <div :style="[{width: `${config.w}px`}, {height: `${config.h}px`}, {border: `1px solid #000`}, {borderRadius: `10%`}]">
        </div>
    <!-- </BaseExecutionBlock> -->
</template>

<script lang="js">
import BaseExecutionBlock from './BaseExecutionBlock.vue';

export default {
    name: 'StaterExecutionBlock.vue',
    props: {
        config: {
            type: Object,
        },
    },
    components: {
        BaseExecutionBlock
    },
    data() {
        return {
            dragging: false,
            startX: 0,
            selectedRangeLeft: 0,
            selectedRangeWidth: 0
        }
    },
    methods: {
        startDrag(event) {
            this.dragging = true;
        },
        dragging(event) {
            console.log(event)
        },
        endDrag() {
            this.dragging = false;
        }
    }
}
</script>