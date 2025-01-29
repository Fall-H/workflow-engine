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
        }
    },
    methods: {
    }
}
</script>