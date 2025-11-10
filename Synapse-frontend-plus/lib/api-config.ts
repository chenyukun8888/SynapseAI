export const API_CONFIG = {
  BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:4396/api",
  CURRENT_USER_ID: "1", // 当前用户ID
}

// API 端点
export const API_ENDPOINTS = {
  // 会话相关
  SESSIONS: "/sessions",
  SESSION_DETAIL: (id: string) => `/sessions/${id}`,
  CHAT: (sessionId: string) => `/sessions/${sessionId}/messages/stream`,
  SESSION_MESSAGES: (sessionId: string) => `/sessions/${sessionId}/messages`,

  // 助理相关
  USER_AGENTS: () => `/agents`,
  AGENT_DETAIL: (id: string) => `/agents/${id}`,
  CREATE_AGENT: "/agents",
  UPDATE_AGENT: (id: string) => `/agents/${id}`,
  DELETE_AGENT: (id: string) => `/agents/${id}`,
  TOGGLE_AGENT_STATUS: (id: string) => `/agents/${id}/toggle-status`,
  AGENT_VERSIONS: (id: string) => `/agents/${id}/versions`,
  AGENT_VERSION_DETAIL: (id: string, version: string) => `/agents/${id}/versions/${version}`,
  CREATE_AGENT_VERSION: (id: string) => `/agents/${id}/versions`,
  PUBLISH_AGENT_VERSION: (id: string, versionId: string) => `/agents/${id}/versions/${versionId}/publish`,
  PUBLISHED_AGENTS: "/agents/published",
}

// 构建完整的API URL
export function buildApiUrl(endpoint: string, queryParams?: Record<string, any>): string {
  let url = `${API_CONFIG.BASE_URL}${endpoint}`

  if (queryParams && Object.keys(queryParams).length > 0) {
    const query = Object.entries(queryParams)
      .filter(([_, value]) => value !== undefined && value !== null)
      .map(([key, value]) => {
        if (typeof value === "boolean") {
          return value ? key : null
        }
        return `${encodeURIComponent(key)}=${encodeURIComponent(value)}`
      })
      .filter(Boolean)
      .join("&")

    if (query) {
      url += `?${query}`
    }
  }

  return url
}

