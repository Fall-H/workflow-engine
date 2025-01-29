<template>
  <div class="common-layout">
    <el-container>
      <el-header style="height: 60px;">Header</el-header>
      <el-container>
        <!-- 组件栏 -->
        <el-aside style="background-color: aqua; height: calc(100vh - 60px);">
          <VueDraggable
            class="component-list"
            :group="{ name: 'componentDrag', pull: 'clone' }"
            :sort="false"
            animation="300"
            item-key="id"
            v-model="comList"
            @clone="cloneComponent"
          >
            <div v-for="(element, index) in comList" :key="index" class="component-item">
              <el-icon name="circle-plus-outline" class="icon"></el-icon>{{ element.title }}
            </div>
          </VueDraggable>
        </el-aside>
        <!-- 画布 -->
        <el-main style="background-color: blanchedalmond; position: relative;">
          <VueDraggable
            group="componentDrag"
            animation="400"
            v-model="drawList"
            ghost-class="ghost"
          >
            <div v-for="(element, index) in drawList" :key="index"
              :style="{ width: element.config.w + 'px', height: element.config.h + 'px' }">
              <VueDragResize
                :x="parseFloat(element.config.x)"
                :y="parseFloat(element.config.y)"
                :w="parseFloat(element.config.w)"
                :h="parseFloat(element.config.h)"
                @resizestop="handleResizeStop($event, element)"
                @dragstop="handleDragStop($event, element)"
                :parent="true"
              >
                <component :is="element.componentName" :config="element.config" />
              </VueDragResize>
            </div>
          </VueDraggable>
        </el-main>
        <el-aside style="background-color: magenta;"></el-aside>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="js">
import { VueDraggable } from 'vue-draggable-plus';
import VueDragResize from 'vue-drag-resize/src';
import StaterExecutionBlock from '../components/executionBlock/StaterExecutionBlock.vue';
import EndExecutionBlock from '../components/executionBlock/EndExecutionBlock.vue';

export default {
  components: {
    VueDraggable,
    VueDragResize,
    StaterExecutionBlock,
    EndExecutionBlock,
  },
  data() {
    return {
      comList: [
        {
          title: '开始块',
          componentName: 'StaterExecutionBlock',
          id: '',
          config: {
            w: 80,
            h: 45,
            x: 0,
            y: 0,
          },
        },
        {
          title: '结束块',
          componentName: 'EndExecutionBlock',
          id: '',
          config: {
            w: 80,
            h: 45,
            x: 0,
            y: 0,
          },
        },
      ],
      currentMove: {},
      drawList: [],
      isUndoing: false, // 添加一个标志，用于标识是否正在进行撤销操作
    };
  },
  methods: {
    cloneComponent(event) {
      this.currentMove = {
        id: `box_${new Date().getTime()}`,
        ...event.item,
      };
    },
    handleEnd(event) {
      const { newIndex } = event;
      this.drawList.push({
        ...this.currentMove,
      });
    },
    handleResizeStop(e, val) {
      val.config.w = e.width;
      val.config.h = e.height;
    },
    handleDragStop(e, val) {
      val.config.x = e.left;
      val.config.y = e.top;
    },
  }
};
</script>

<style>
.ghost {
  opacity: 0.5;
  background-color: #f0f0f0;
}
</style>