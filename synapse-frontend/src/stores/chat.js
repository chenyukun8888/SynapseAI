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

  // 流式发送消息（SSE）
  const sendStreamMessage = async (messageText, model = 'Qwen/Qwen2.5-7B-Instruct', temperature = 0.7, maxTokens = 2048, onChunk) => {
    // 添加用户消息
    const userMessage = {
      role: 'user',
      content: messageText,
      time: new Date().toLocaleTimeString()
    }
    messages.value.push(userMessage)
    
    // 创建临时的 AI 消息（用于逐字显示）
    const assistantMessage = {
      role: 'assistant',
      content: '',
      time: new Date().toLocaleTimeString(),
      model: model,
      streaming: true
    }
    messages.value.push(assistantMessage)
    const messageIndex = messages.value.length - 1
    
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
      
      // 使用 fetch API 发送流式请求
      const response = await fetch('/api/chat/stream', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'text/event-stream'
        },
        body: JSON.stringify(requestData)
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      // 处理 SSE 流
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''
      
      while (true) {
        const { done, value } = await reader.read()
        
        if (done) {
          console.log('流式读取完成')
          break
        }
        
        // 解码数据
        buffer += decoder.decode(value, { stream: true })
        
        // 按行分割
        const lines = buffer.split('\n')
        buffer = lines.pop() || '' // 保留最后一行（可能不完整）
        
        for (const line of lines) {
          // 兼容 "data:" 和 "data: " 两种格式
          if (line.startsWith('data:')) {
            // 移除 "data:" 前缀（可能有空格也可能没有）
            let data = line.substring(5).trim()
            if (data.startsWith(' ')) {
              data = data.substring(1).trim()
            }
            
            if (data === '') continue
            
            console.log('收到 SSE 数据:', data)
            
            try {
              const json = JSON.parse(data)
              
              switch (json.type) {
                case 'start':
                  console.log('✅ 流式开始:', json.messageId)
                  // SSE 连接已建立，立即关闭 loading
                  loading.value = false
                  break
                  
                case 'chunk':
                  // 追加内容
                  messages.value[messageIndex].content += json.content
                  console.log('📝 收到内容分片:', json.content)
                  // 触发回调（用于 UI 更新，如自动滚动）
                  if (onChunk) {
                    onChunk(json.content)
                  }
                  break
                  
                case 'done':
                  console.log('✅ 流式完成:', json.tokensUsed)
                  messages.value[messageIndex].streaming = false
                  messages.value[messageIndex].tokensUsed = json.tokensUsed
                  // loading 已在 start 事件时关闭，这里不需要再关闭
                  break
                  
                case 'error':
                  console.error('❌ 流式错误:', json.message)
                  messages.value[messageIndex].content = '错误: ' + json.message
                  messages.value[messageIndex].streaming = false
                  loading.value = false
                  break
              }
            } catch (e) {
              console.error('解析 SSE 数据失败:', e, data)
            }
          } else if (line.startsWith('event:')) {
            console.log('收到事件:', line)
          }
        }
      }
      
      // 确保流结束后关闭 streaming 状态
      if (messages.value[messageIndex].streaming) {
        console.log('⚠️ 流未正常结束，手动关闭状态')
        messages.value[messageIndex].streaming = false
      }
      // loading 应该在 start 事件时就已关闭，如果还没关闭说明连接有问题
      if (loading.value) {
        console.log('⚠️ Loading 未正常关闭（未收到 start 事件），手动关闭')
        loading.value = false
      }
      
      return messages.value[messageIndex]
      
    } catch (error) {
      console.error('流式发送消息失败:', error)
      // 添加错误消息
      messages.value[messageIndex].content = '发送消息失败: ' + (error.message || '未知错误')
      messages.value[messageIndex].streaming = false
      loading.value = false
      return messages.value[messageIndex]
    }
  }
  
  return { 
    messages, 
    loading, 
    sendMessage,
    sendStreamMessage,
    fetchHistory,
    clearMessages,
    fetchModels
  }
})
