# SynapseAI 前端 - 流式对话界面

## 🎯 功能特性

- ✅ **SSE 流式对话**：实时显示 AI 回复，逐字呈现
- ✅ **打字效果**：闪烁光标和"正在输入..."提示
- ✅ **自动滚动**：内容实时跟随，无需手动滚动
- ✅ **美观界面**：现代化设计，响应式布局
- ✅ **快捷键支持**：Enter 发送，Shift+Enter 换行

## 🚀 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

前端将在 `http://localhost:4397` 启动。

### 3. 构建生产版本

```bash
npm run build
```

## 📱 使用说明

### 发送消息

1. 在输入框中输入消息
2. 按 **Enter** 发送（或点击发送按钮）
3. AI 回复将**逐字显示**，就像真人打字一样

### 流式对话特点

- **实时响应**：无需等待完整回复，AI 开始生成后立即显示
- **视觉反馈**：
  - 闪烁的 **▊** 光标表示正在输入
  - "正在输入..." 提示显示流式状态
- **自动滚动**：随着内容生成自动滚动到底部

### 清空聊天

点击右上角的 🗑️ 按钮可以清空所有聊天记录。

## 🎨 界面预览

### 欢迎页面
```
🤖
欢迎使用 SynapseAI
体验流式对话，AI 回复逐字呈现！

💡 提示：按 Enter 发送消息
⚡ 流式响应：实时看到 AI 的思考过程
```

### 对话中
```
用户消息（右侧，紫色渐变）
├─ 消息内容
└─ 时间 | 模型

AI 回复（左侧，白色背景）
├─ 消息内容 ▊  ← 闪烁光标
└─ 时间 | 模型 | 正在输入...
```

## 🛠️ 技术栈

- **Vue 3** - 响应式框架
- **Pinia** - 状态管理
- **PrimeVue** - UI 组件库
- **Vite** - 构建工具
- **Fetch API** - SSE 流式请求

## 📦 核心文件

```
synapse-frontend/
├── src/
│   ├── views/
│   │   └── HomeView.vue        # 主聊天界面
│   ├── stores/
│   │   └── chat.js             # 聊天状态管理
│   ├── router/
│   │   └── index.js            # 路由配置
│   └── main.js                 # 入口文件
├── public/
├── package.json
└── vite.config.js              # Vite 配置
```

## 🔧 API 配置

前端通过 Vite 代理与后端通信：

```javascript
// vite.config.js
export default {
  server: {
    port: 4397,
    proxy: {
      '/api': {
        target: 'http://localhost:4396',  // 后端地址
        changeOrigin: true
      }
    }
  }
}
```

## 📡 流式对话实现

### 在 store 中使用

```javascript
// stores/chat.js
export const useChatStore = defineStore('chat', () => {
  const sendStreamMessage = async (messageText, model, temperature, maxTokens, onChunk) => {
    // 使用 Fetch API 发送流式请求
    const response = await fetch('/api/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream'
      },
      body: JSON.stringify({ message, model, temperature, maxTokens })
    })
    
    // 读取 SSE 流
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      
      // 解析 SSE 数据
      const chunk = decoder.decode(value)
      // 处理每个事件：start/chunk/done/error
    }
  }
  
  return { sendStreamMessage }
})
```

### 在组件中使用

```vue
<script setup>
import { useChatStore } from '@/stores/chat'

const chatStore = useChatStore()

const sendMessage = async () => {
  await chatStore.sendStreamMessage(
    message,
    'Qwen/Qwen2.5-7B-Instruct',
    0.7,
    2048,
    (chunk) => {
      // 每收到内容分片时的回调
      scrollToBottom()
    }
  )
}
</script>
```

## 🎯 自定义配置

### 修改默认模型

在 `HomeView.vue` 中修改：

```javascript
await chatStore.sendStreamMessage(
  message,
  'your-model-name',  // 修改这里
  0.7,
  2048,
  onChunk
)
```

### 修改温度参数

```javascript
await chatStore.sendStreamMessage(
  message,
  'Qwen/Qwen2.5-7B-Instruct',
  0.9,  // 0.0-1.0，越高越随机
  2048,
  onChunk
)
```

### 修改最大 Token 数

```javascript
await chatStore.sendStreamMessage(
  message,
  'Qwen/Qwen2.5-7B-Instruct',
  0.7,
  4096,  // 增加到 4096
  onChunk
)
```

## 🐛 常见问题

### 1. 流式对话不显示

**问题**：发送消息后没有流式显示

**解决**：
- 检查后端是否正常启动（http://localhost:4396）
- 查看浏览器控制台是否有错误
- 确认 Vite 代理配置正确

### 2. 内容不自动滚动

**问题**：消息显示但不自动滚动到底部

**解决**：
- 确保 `onChunk` 回调中调用了 `scrollToBottom()`
- 检查 `messagesContainer` ref 是否正确绑定

### 3. 闪烁光标不显示

**问题**：流式输入时看不到闪烁光标

**解决**：
- 检查 `message.streaming` 属性是否正确设置
- 确认 CSS 动画已正确加载

## 📊 性能优化

- **防抖处理**：滚动操作使用 `setTimeout` 防抖
- **虚拟滚动**：长对话历史建议使用虚拟滚动
- **内存管理**：定期清理历史消息

## 🎨 主题定制

所有颜色和样式都在 `HomeView.vue` 的 `<style>` 中：

```css
/* 主题色 */
--primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

/* 消息气泡 */
.message.user { background: var(--primary-gradient); }
.message.assistant { background: white; }

/* 闪烁光标颜色 */
.streaming-cursor { color: #667eea; }
```

## 📝 开发笔记

- 使用 Vue 3 Composition API
- Pinia store 管理全局状态
- SSE 通过 Fetch API 实现
- 响应式设计支持移动端

---

**版本**: v1.2  
**最后更新**: 2025-11-07

