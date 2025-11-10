import { API_CONFIG, API_ENDPOINTS } from "./api-config"

export async function streamChat(
  message: string, 
  sessionId?: string, 
  signal?: AbortSignal,
  options?: {
    model?: string
    temperature?: number
    maxTokens?: number
  }
) {
  if (!sessionId) {
    throw new Error("Session ID is required")
  }

  // 使用后端的流式聊天接口
  const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.CHAT(sessionId)}`

  // 构建请求体，匹配后端的 StreamChatRequest
  const requestBody: {
    message: string
    model?: string
    temperature?: number
    maxTokens?: number
  } = {
    message: message,
  }
  
  // 只添加有值的可选字段
  if (options?.model) {
    requestBody.model = options.model
  }
  if (options?.temperature !== undefined) {
    requestBody.temperature = options.temperature
  }
  if (options?.maxTokens !== undefined) {
    requestBody.maxTokens = options.maxTokens
  }

  // 发送请求
  try {
    console.log("发送流式聊天请求:", url, requestBody) // 添加日志以便调试
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "text/event-stream",
        "Cache-Control": "no-cache",
        Connection: "keep-alive",
      },
      body: JSON.stringify(requestBody),
      // 使用传入的signal用于取消请求
      signal,
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => null)
      throw new Error(errorData?.error || `API request failed with status ${response.status}`)
    }

    return response
  } catch (error) {
    console.error("Stream chat error:", error)
    throw error
  }
}

