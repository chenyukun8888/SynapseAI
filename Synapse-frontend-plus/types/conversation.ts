// 会话类型定义
export interface Session {
  id: string
  title: string
  description: string | null
  createdAt: string
  updatedAt: string
  archived: boolean
}

// 会话列表项（后端返回的简化版本）
export interface SessionListItem {
  sessionId: string
  title: string
  model?: string
  messageCount?: number
  lastMessage?: {
    role: string
    content: string
    createdAt: string
  }
  createdAt: string
  updatedAt: string
}

// 会话列表响应（匹配后端 SessionListVO）
export interface SessionListVO {
  total: number
  page: number
  pageSize: number
  totalPages: number
  sessions: SessionListItem[]
}

// API响应基本结构
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 创建会话请求参数
export interface CreateSessionParams {
  title: string
  userId?: string
  description?: string
}

// 获取会话列表请求参数
export interface GetSessionsParams {
  userId?: string
  archived?: boolean
  page?: number
  pageSize?: number
  sortBy?: string
  order?: string
}

// 更新会话请求参数
export interface UpdateSessionParams {
  title?: string
  description?: string
  archived?: boolean
}

// 消息项（匹配后端 MessageItemVO）
export interface MessageItem {
  id: string
  role: "user" | "assistant" | "system"
  content: string
  model?: string
  tokenCount?: number
  createdAt: string
}

// 消息列表响应（匹配后端 MessageListVO）
export interface MessageListVO {
  sessionId: string
  total: number
  page: number
  pageSize: number
  totalPages: number
  messages: MessageItem[]
}

// 获取消息列表请求参数
export interface GetMessagesParams {
  page?: number
  pageSize?: number
}

