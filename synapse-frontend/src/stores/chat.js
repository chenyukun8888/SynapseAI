import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useChatStore = defineStore('chat', () => {
  // 消息历史
  const messages = ref([])
  // 加载状态
  const loading = ref(false)
  
  // 发送消息
  const sendMessage = async (messageText, model = 'Qwen/Qwen2.5-7B-Instruct', temperature = 0.7, maxTokens = 2048) => {
    // 添加用户消息
    const userMessage = {
      role: 'user',
      content: messageText,
      time: new Date().toLocaleTimeString()
    }
    messages.value.push(userMessage)
    
    // 设置加载状态
    loading.value = true
    
    try {
      // 准备请求数据
      const requestData = {
        message: messageText,
        model: model,
        temperature: temperature,
        maxTokens: maxTokens
      }
      
      // 发送请求到后端
      const response = await axios.post('/api/chat', requestData)
      
      // 处理响应
      if (response.data && response.data.code === 200) {
        // 添加AI回复
        const assistantMessage = {
          role: 'assistant',
          content: response.data.data.content,
          time: new Date().toLocaleTimeString(),
          model: response.data.data.model
        }
        messages.value.push(assistantMessage)
        return assistantMessage
      } else {
        // 处理错误响应
        const errorMessage = {
          role: 'system',
          content: '请求出错: ' + (response.data.message || '未知错误'),
          time: new Date().toLocaleTimeString()
        }
        messages.value.push(errorMessage)
        return errorMessage
      }
    } catch (error) {
      console.error('发送消息失败:', error)
      // 添加错误消息
      const errorMessage = {
        role: 'system',
        content: '发送消息失败: ' + (error.response?.data?.message || error.message || '未知错误'),
        time: new Date().toLocaleTimeString()
      }
      messages.value.push(errorMessage)
      return errorMessage
    } finally {
      loading.value = false
    }
  }
  
  // 获取历史消息
  const fetchHistory = async (limit = 20, offset = 0) => {
    try {
      const response = await axios.get('/api/chat/history', {
        params: { limit, offset }
      })
      
      if (response.data && response.data.code === 200) {
        const historyMessages = response.data.data.messages.map(msg => ({
          role: msg.role,
          content: msg.content,
          time: new Date(msg.createdAt).toLocaleTimeString(),
          model: msg.model
        }))
        messages.value = historyMessages.reverse() // 反转顺序，最新的在最后
        return historyMessages
      }
    } catch (error) {
      console.error('获取历史消息失败:', error)
    }
  }
  
  // 清空历史消息
  const clearMessages = async () => {
    try {
      const response = await axios.delete('/api/chat/history')
      if (response.data && response.data.code === 200) {
        messages.value = []
        console.log('已清除 ' + response.data.data.deletedCount + ' 条消息')
      }
    } catch (error) {
      console.error('清除历史消息失败:', error)
    }
  }
  
  // 获取可用模型列表
  const fetchModels = async () => {
    try {
      const response = await axios.get('/api/models')
      if (response.data && response.data.code === 200) {
        return response.data.data.models
      }
    } catch (error) {
      console.error('获取模型列表失败:', error)
      return []
    }
  }
  
  return { 
    messages, 
    loading, 
    sendMessage,
    fetchHistory,
    clearMessages,
    fetchModels
  }
})
