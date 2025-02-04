<template>
  <!-- 操作栏 -->
  <div style="display: flex; flex-direction: column; height: 100vh;">
    <!-- 顶部操作栏 -->
    <div style="display: flex; justify-content: start;">
      <div style="margin: 10px; display: flex; gap: 10px; align-items: center;">
        <el-form-item label="流程名" style="margin-bottom: 0;">
          <el-input v-model="flowName" style="width: 100px;"></el-input>
        </el-form-item>
        <el-button @click="onSave">下载</el-button>
        <el-button @click="onUpload">上传</el-button>
        <el-button>执行</el-button>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div style="display: flex; flex-direction: row; height: 100vh;">
      <!-- 组件栏 -->
      <div class="component-sidebar">
        <div v-for="item in componentList" :key="item.id" class="drag-item" draggable="true"
          @dragstart="onDragStart($event, item)">
          {{ item.label }}
        </div>
      </div>

      <!-- 画布 -->
      <div class="vue-flow-container">
        <VueFlow ref="vueFlowInstance" :nodes="nodes" :edges="edges" fit-view-on-init @node-click="onNodeClick"
          @edge-click="onEdgeClick" @drop="onDrop" @dragover.prevent @dragenter.prevent :node-types="nodeTypes">
          <Background />
          <Controls />
          <MiniMap />
        </VueFlow>
      </div>

      <!-- 设置 -->
      <div v-show="isNode !== 0" style="padding: 20px; width: 300px; border: 1px solid #000;">
        <div style="display: flex; justify-content: end; padding: 20px;">
          <el-button type="danger" icon="Close" @click="closeSetting"/>
        </div>

        <el-form v-show="isNode === 1" label-width="auto">
          <el-form-item label="名字">
            <el-input v-model="current.data.label" />
          </el-form-item>
          <el-form-item label="处理逻辑地址">
            <el-input v-model="current.data.executionLogic" />
          </el-form-item>

          <!-- 参数设置栏 -->
          <el-form-item label="参数设置">
            <div v-for="(value, key) in current.data.params" :key="key" style="margin-bottom: 10px;">
              <el-input disabled :placeholder="`键: ${key}`" style="margin-bottom: 5px;" />
              <el-input disabled :placeholder="`值: ${value}`" style="margin-bottom: 5px;" />
              <el-button type="danger" size="small" @click="removeParam(key)">删除</el-button>
            </div>
            <el-input v-model="newParamKey" placeholder="参数名" style="margin-bottom: 5px;" />
            <el-input v-model="newParamValue" placeholder="参数值" style="margin-bottom: 5px;" />
            <el-button type="primary" size="small" @click="addParam">添加参数</el-button>
          </el-form-item>
        </el-form>

        <el-form-item v-show="current.typeNumber === 2" label="当条件为真时执行左路逻辑">
          <el-switch v-model="current.data.isLeft" />
        </el-form-item>

        <!-- 连线设置 -->
        <el-form v-show="isNode === 2">
          <el-form-item label="是否是通过">
            <el-switch v-model="current.data.isPass" />
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { VueFlow, useVueFlow, MarkerType } from '@vue-flow/core';
import { Background } from '@vue-flow/background';
import { Controls } from '@vue-flow/controls';
import { MiniMap } from '@vue-flow/minimap';
import '@vue-flow/core/dist/style.css';
import '@vue-flow/controls/dist/style.css';
import '@vue-flow/minimap/dist/style.css';

import StaterExecutionBlock from '@/components/executionBlock/StaterExecutionBlock.vue';
import JudgeExecutionBlock from '@/components/executionBlock/JudgeExecutionBlock.vue';
import ProcessExecutionBlock from '@/components/executionBlock/ProcessExecutionBlock.vue';
import EndExecutionBlock from '@/components/executionBlock/EndExecutionBlock.vue';

import { jsonToXml, downloadXmlFile, xmlToJson } from '@/util/XmlUtil.js';
import { conversionTypeNumberToType, conversionTypeToTypeNumber } from '@/util/ConversionUtil.js';

const { addNodes, addEdges, getNodes, getEdges, onConnect, onNodesChange, onEdgesChange, applyNodeChanges, applyEdgeChanges } = useVueFlow();

// 组件栏的组件列表
const componentList = ref([
  { id: '1', label: '开始', type: 'input' },
  { id: '2', label: '处理', type: 'process' },
  { id: '3', label: '判断', type: 'judge' },
  { id: '4', label: '结束', type: 'output' },
]);

// 画布中的节点和连线
const nodes = ref([]);
const edges = ref([]);

const isNode = ref(0);
const nodeNumber = ref(1);

const flowName = ref('');

const current = ref({
  data: {
    label: "",
    executionLogic: "",
    params: {},
  },
});

const newParamKey = ref("");
const newParamValue = ref("");

// 节点类型
const nodeTypes = ref({
  input: StaterExecutionBlock,
  judge: JudgeExecutionBlock,
  process: ProcessExecutionBlock,
  output: EndExecutionBlock,
});

// 拖拽开始时的处理函数
const onDragStart = (event, item) => {
  event.dataTransfer.setData('text/plain', JSON.stringify(item));
};

// 拖拽结束时的处理函数
const onDrop = (event) => {
  const data = JSON.parse(event.dataTransfer.getData('text/plain'));
  const position = {
    x: event.clientX - event.target.getBoundingClientRect().left,
    y: event.clientY - event.target.getBoundingClientRect().top,
  };

  const param = data.type === 'judge'
    ? { label: data.label, 'source-a': 0, 'source-b': 0, executionLogic: "", params: {} }
    : { label: data.label, source: 0, executionLogic: "", params: {} };

  const newNode = {
    id: nodeNumber.value.toString(),
    type: data.type,
    typeNumber: conversionTypeToTypeNumber(data.type),
    data: param,
    position,
  };

  nodeNumber.value++;
  addNodes([newNode]);
};

// 节点点击事件
const onNodeClick = (event) => {
  current.value = event.node;
  isNode.value = 1;
};

// 连线点击事件
const onEdgeClick = (event) => {
  current.value = event.edge;
  isNode.value = 2;
};

// 连接事件
onConnect((connection) => {
  const currentNode = getNodes.value.find((node) => node.id === connection.source);
  if (currentNode.data[connection.sourceHandle] >= 1) return;

  addEdges([
    {
      id: `edge-${connection.source}-${connection.target}-${Date.now()}`,
      source: connection.source,
      target: connection.target,
      sourceHandle: connection.sourceHandle,
      style: { stroke: '#10D27A', strokeWidth: 2 },
      markerEnd: MarkerType.ArrowClosed,
    },
  ]);
  currentNode.data[connection.sourceHandle]++;
});

onNodesChange(async (changes) => {
  const nextChanges = []

  for (const change of changes) {
    if (change.type === 'remove') {
      isNode.value = 0
      nextChanges.push(change)
    } else {
      nextChanges.push(change)
    }
  }

  applyNodeChanges(nextChanges)
})

// 添加参数
const addParam = () => {
  if (newParamKey.value && newParamValue.value) {
    current.value.data.params[newParamKey.value] = newParamValue.value;
    newParamKey.value = "";
    newParamValue.value = "";
  }
};

// 删除参数
const removeParam = (key) => {
  delete current.value.data.params[key];
};

// 保存函数
const onSave = () => {
  const tempNodes = getNodes.value;
  const tempEdges = getEdges.value;
  const data = tempNodes.sort((a, b) => a.typeNumber - b.typeNumber).map((element) => {
    const nextPropertyIds = tempEdges
      .filter((edge) => edge.source === element.id)
      .sort((a, b) => (element.data.isLeft ? b.sourceHandle.localeCompare(a.sourceHandle) : a.sourceHandle.localeCompare(b.sourceHandle)))
      .map((edge) => edge.target);

    return {
      pattern: element.typeNumber,
      propertyId: element.id,
      nextPropertyIds,
      msg: element.data.label,
      executionLogic: element.data.executionLogic,
      params: element.data.params,
      x: element.computedPosition.x,
      y: element.computedPosition.y,
      width: element.dimensions.width,
      height: element.dimensions.height,
    };
  });

  const xml = jsonToXml(data);
  downloadXmlFile(xml, `${flowName.value}.xml`);
};

// 上传文件并加载数据
const onUpload = () => {
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = '.xml';
  fileInput.onchange = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    try {
      const content = await file.text();
      const jsons = xmlToJson(content);
      const propertyValues = jsons.propertyValues.propertyValue;

      // 添加节点
      propertyValues.forEach((element) => {
        const type = conversionTypeNumberToType(parseInt(element.pattern['#text']));
        const position = { x: parseFloat(element.x['#text']), y: parseFloat(element.y['#text']) };

        const param = type === 'judge'
          ? { label: element.msg['#text'], 'source-a': 1, 'source-b': 1, executionLogic: element.executionLogic['#text'], params: element.param }
          : { label: element.msg['#text'], source: 1, executionLogic: element.executionLogic['#text'], params: element.param };

        addNodes({
          id: element.propertyId['#text'],
          type,
          typeNumber: element.pattern['#text'],
          data: param,
          position,
        });
      });

      // 添加连线
      propertyValues.forEach((element) => {
        const sourceId = element.propertyId['#text'];
        const nextIds = element.nextPropertyIds;

        if (parseInt(element.pattern['#text']) === 2) {
          addEdges([
            { id: `edge-${sourceId}-${nextIds[0]['#text']}-1`, source: sourceId, target: nextIds[0]['#text'], sourceHandle: 'source-a', style: { stroke: '#10D27A', strokeWidth: 2 }, markerEnd: MarkerType.ArrowClosed },
            { id: `edge-${sourceId}-${nextIds[1]['#text']}-2`, source: sourceId, target: nextIds[1]['#text'], sourceHandle: 'source-b', style: { stroke: '#10D27A', strokeWidth: 2 }, markerEnd: MarkerType.ArrowClosed },
          ]);
        } else {
          addEdges([
            { id: `edge-${sourceId}-${nextIds['#text']}`, source: sourceId, target: nextIds['#text'], sourceHandle: 'source', style: { stroke: '#10D27A', strokeWidth: 2 }, markerEnd: MarkerType.ArrowClosed },
          ]);
        }
      });
    } catch (error) {
      console.error('文件上传或解析失败:', error);
    }
  };
  fileInput.click();
};

const closeSetting = () => {
  isNode.value = 0
}
</script>

<style>
.component-sidebar {
  width: 200px;
  background-color: #f0f0f0;
  padding: 10px;
}

.drag-item {
  background-color: #ddd;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 5px;
  cursor: pointer;
}

.vue-flow-container {
  flex: 1;
}
</style>