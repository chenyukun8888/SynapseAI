<script setup>
import { ref, onMounted } from 'vue'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import ProgressSpinner from 'primevue/progressspinner'
import { useChatStore } from '@/stores/chat'

// 获取聊天状态
const chatStore = useChatStore()
// 输入消息
const inputMessage = ref('')
// 消息容器ref
const messagesContainer = ref(null)

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) return
  
  const message = inputMessage.value
  inputMessage.value = ''
  
  await chatStore.sendMessage(message)
  // 滚动到底部
  scrollToBottom()
}

// 处理按键事件
const handleKeyDown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

// 滚动到底部
const scrollToBottom = () => {
  setTimeout(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  }, 50)
}

// 清空聊天
const clearChat = () => {
  if (confirm('确定要清空聊天记录吗？')) {
    chatStore.clearMessages()
  }
}

// 监听消息列表变化，自动滚动到底部
onMounted(() => {
  scrollToBottom()
})
</script>

<template>
  <main class="chat-main">
    <div class="chat-container">
      <div class="chat-header">
        <h1>💡 SynapseAI 智能助手</h1>
        <Button 
          v-if="chatStore.messages.length > 0"
          icon="pi pi-trash" 
          class="p-button-text p-button-sm clear-btn" 
          @click="clearChat"
          title="清空聊天"
        />
      </div>
      
      <div class="messages-container" ref="messagesContainer">
        <div v-if="chatStore.messages.length === 0" class="empty-chat">
          <div class="welcome-icon">🤖</div>
          <h2>欢迎使用 SynapseAI</h2>
          <p>开始与AI助手对话吧！</p>
          <div class="tips">
            <p>💡 提示：按 Enter 发送消息</p>
          </div>
        </div>
        <div v-else class="messages">
          <div 
            v-for="(message, index) in chatStore.messages" 
            :key="index" 
            :class="['message', message.role]"
          >
            <div class="message-content">{{ message.content }}</div>
            <div class="message-meta">
              <span class="time">{{ message.time }}</span>
              <span v-if="message.model" class="model">{{ message.model }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="input-container">
        <InputText 
          v-model="inputMessage" 
          placeholder="输入消息..." 
          class="message-input"
          @keydown="handleKeyDown"
          :disabled="chatStore.loading"
        />
        <Button 
          :disabled="chatStore.loading || !inputMessage.trim()" 
          icon="pi pi-send" 
          class="send-button p-button-rounded" 
          @click="sendMessage"
        />
      </div>
    </div>
    
    <div v-if="chatStore.loading" class="loading-overlay">
      <ProgressSpinner strokeWidth="4" />
    </div>
  </main>
</template>

<style scoped>
.chat-main {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  padding: 0;
  box-sizing: border-box;
  overflow: hidden;
}

.chat-container {
  width: 100%;
  max-width: 800px;
  height: 90vh;
  display: flex;
  flex-direction: column;
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  margin: 0 auto;
}

.chat-header {
  padding: 20px 24px;
  border-bottom: 1px solid var(--color-border);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h1 {
  font-size: 1.5rem;
  color: white;
  margin: 0;
  text-align: center;
  flex: 1;
}

.clear-btn {
  color: white !important;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background-color: #f7f9fc;
}

.empty-chat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #888;
  text-align: center;
  padding: 40px 20px;
}

.welcome-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-chat h2 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 12px;
}

.empty-chat p {
  font-size: 1.1rem;
  margin-bottom: 20px;
}

.tips {
  margin-top: 20px;
  padding: 12px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.tips p {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.messages {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  padding: 0;
}

.message {
  padding: 12px 16px;
  border-radius: 18px;
  max-width: 75%;
  word-break: break-word;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.user {
  align-self: flex-end;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant {
  align-self: flex-start;
  background-color: white;
  border: 1px solid #e5e7eb;
  border-bottom-left-radius: 4px;
}

.message.system {
  align-self: center;
  background-color: #fff0e0;
  font-style: italic;
  max-width: 90%;
  text-align: center;
  border: 1px solid #ffd699;
}

.message-content {
  line-height: 1.6;
  white-space: pre-wrap;
}

.message-meta {
  font-size: 0.75rem;
  margin-top: 8px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.message.user .message-meta {
  color: rgba(255, 255, 255, 0.8);
}

.message.assistant .message-meta {
  color: #888;
}

.input-container {
  display: flex;
  gap: 12px;
  padding: 20px 24px;
  background-color: white;
  border-top: 1px solid var(--color-border);
  position: relative;
}

.message-input {
  flex: 1;
  padding: 14px 20px;
  border-radius: 24px;
  border: 2px solid #e5e7eb;
  font-size: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.message-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.send-button {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  color: white !important;
}

.send-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  backdrop-filter: blur(4px);
}

@media (max-width: 900px) {
  .chat-container {
    width: 95%;
    height: 95vh;
    border-radius: 12px;
  }
}

@media (max-width: 600px) {
  .chat-container {
    width: 100%;
    height: 100vh;
    border-radius: 0;
    margin: 0;
    max-width: none;
  }
  
  .chat-main {
    padding: 0;
    background: white;
  }
  
  .message {
    max-width: 85%;
  }
  
  .chat-header h1 {
    font-size: 1.2rem;
  }
}
</style>
