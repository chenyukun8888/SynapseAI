"use client"

import type React from "react"

import { useEffect, useState, useRef, use } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import {
  MessageCircle,
  Upload,
  Trash,
  FileText,
  Workflow,
  Zap,
  Search,
  ArrowLeft,
  Power,
  PowerOff,
  History,
  RefreshCw,
} from "lucide-react"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Badge } from "@/components/ui/badge"
import { toast } from "@/components/ui/use-toast"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Switch } from "@/components/ui/switch"
import { Label } from "@/components/ui/label"
import { Slider } from "@/components/ui/slider"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Card, CardContent } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Skeleton } from "@/components/ui/skeleton"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

import {
  getAgentDetail,
  updateAgent,
  createAgentVersion,
  publishAgentVersion,
  deleteAgent,
  toggleAgentStatus,
  getAgentVersions,
} from "@/lib/agent-service"
import { PublishStatus } from "@/types/agent"
import type { AgentVersion } from "@/types/agent"

// 应用类型定义
type AgentType = "chat" | "agent"

// 模型选项
const modelOptions = [
  { value: "gpt-4o", label: "GPT-4o" },
  { value: "gpt-4-turbo", label: "GPT-4 Turbo" },
  { value: "gpt-3.5-turbo", label: "GPT-3.5 Turbo" },
  { value: "claude-3-opus", label: "Claude 3 Opus" },
  { value: "claude-3-sonnet", label: "Claude 3 Sonnet" },
  { value: "claude-3-haiku", label: "Claude 3 Haiku" },
  { value: "gemini-pro", label: "Gemini Pro" },
  { value: "llama-3-70b", label: "Llama 3 70B" },
]

// 工具选项
const toolOptions = [
  { id: "web-search", name: "网页搜索", description: "允许搜索互联网获取信息" },
  { id: "file-reader", name: "文件读取", description: "允许读取和分析上传的文件" },
  { id: "code-interpreter", name: "代码解释器", description: "允许执行代码并返回结果" },
  { id: "image-generation", name: "图像生成", description: "允许生成和编辑图像" },
  { id: "calculator", name: "计算器", description: "允许执行数学计算" },
]

// 知识库选项
const knowledgeBaseOptions = [
  { id: "kb-1", name: "产品文档", description: "包含产品说明、使用指南等" },
  { id: "kb-2", name: "常见问题", description: "常见问题及解答集合" },
  { id: "kb-3", name: "技术文档", description: "技术规范和API文档" },
  { id: "kb-4", name: "营销资料", description: "营销内容和宣传材料" },
]

interface AgentFormData {
  name: string
  avatar: string | null
  description: string
  systemPrompt: string
  welcomeMessage: string
  modelConfig: {
    modelName: string
    temperature: number
    maxTokens: number
  }
  tools: string[]
  knowledgeBaseIds: string[]
  enabled: boolean
  agentType: number
}

export default function EditAgentPage({ params }: { params: { id: string } }) {
  // 使用React.use()解包params
  const id = use(params).id;
  const router = useRouter()
  const [selectedType, setSelectedType] = useState<AgentType>("chat")
  const [activeTab, setActiveTab] = useState("basic")
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [isDeleting, setIsDeleting] = useState(false)
  const [isPublishing, setIsPublishing] = useState(false)
  const [isTogglingStatus, setIsTogglingStatus] = useState(false)
  const [isLoadingVersions, setIsLoadingVersions] = useState(false)
  const [isRollingBack, setIsRollingBack] = useState(false)
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)
  const [showPublishDialog, setShowPublishDialog] = useState(false)
  const [showVersionsDialog, setShowVersionsDialog] = useState(false)
  const [versionNumber, setVersionNumber] = useState("")
  const [changeLog, setChangeLog] = useState("")
  const [versions, setVersions] = useState<AgentVersion[]>([])
  const [selectedVersion, setSelectedVersion] = useState<AgentVersion | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  // 表单数据
  const [formData, setFormData] = useState<AgentFormData>({
    name: "",
    avatar: null,
    description: "",
    systemPrompt: "",
    welcomeMessage: "",
    modelConfig: {
      modelName: "gpt-4o",
      temperature: 0.7,
      maxTokens: 2000,
    },
    tools: [],
    knowledgeBaseIds: [],
    enabled: true,
    agentType: 1,
  })

  // 加载助理详情
  useEffect(() => {
    async function fetchAgentDetail() {
      try {
        setIsLoading(true)
        const response = await getAgentDetail(id)

        if (response.code === 200 && response.data) {
          const agent = response.data

          // 设置表单数据
          setFormData({
            name: agent.name,
            avatar: agent.avatar,
            description: agent.description,
            systemPrompt: agent.systemPrompt,
            welcomeMessage: agent.welcomeMessage,
            modelConfig: {
              modelName: agent.modelConfig.modelName,
              temperature: agent.modelConfig.temperature || 0.7,
              maxTokens: agent.modelConfig.maxTokens || 2000,
            },
            tools: agent.tools.map((tool) => tool.id),
            knowledgeBaseIds: agent.knowledgeBaseIds,
            enabled: agent.enabled,
            agentType: agent.agentType,
          })

          // 设置助理类型
          setSelectedType(agent.agentType === 1 ? "chat" : "agent")
        } else {
          toast({
            title: "获取助理详情失败",
            description: response.message,
            variant: "destructive",
          })
          router.push("/studio")
        }
      } catch (error) {
        console.error("获取助理详情错误:", error)
        toast({
          title: "获取助理详情失败",
          description: "请稍后再试",
          variant: "destructive",
        })
        router.push("/studio")
      } finally {
        setIsLoading(false)
      }
    }

    fetchAgentDetail()
  }, [id, router])

  // 更新表单字段
  const updateFormField = (field: string, value: any) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }))
  }

  // 更新模型配置
  const updateModelConfig = (field: string, value: any) => {
    setFormData((prev) => ({
      ...prev,
      modelConfig: {
        ...prev.modelConfig,
        [field]: value,
      },
    }))
  }

  // 切换工具
  const toggleTool = (toolId: string) => {
    setFormData((prev) => {
      const tools = [...prev.tools]
      if (tools.includes(toolId)) {
        return { ...prev, tools: tools.filter((id) => id !== toolId) }
      } else {
        return { ...prev, tools: [...tools, toolId] }
      }
    })
  }

  // 切换知识库
  const toggleKnowledgeBase = (kbId: string) => {
    setFormData((prev) => {
      const knowledgeBaseIds = [...prev.knowledgeBaseIds]
      if (knowledgeBaseIds.includes(kbId)) {
        return { ...prev, knowledgeBaseIds: knowledgeBaseIds.filter((id) => id !== kbId) }
      } else {
        return { ...prev, knowledgeBaseIds: [...knowledgeBaseIds, kbId] }
      }
    })
  }

  // 处理头像上传
  const handleAvatarUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    // 检查文件类型
    if (!file.type.startsWith("image/")) {
      toast({
        title: "文件类型错误",
        description: "请上传图片文件",
        variant: "destructive",
      })
      return
    }

    // 检查文件大小 (限制为2MB)
    if (file.size > 2 * 1024 * 1024) {
      toast({
        title: "文件过大",
        description: "头像图片不能超过2MB",
        variant: "destructive",
      })
      return
    }

    // 创建文件预览URL
    const reader = new FileReader()
    reader.onload = (e) => {
      updateFormField("avatar", e.target?.result as string)
    }
    reader.readAsDataURL(file)
  }

  // 移除头像
  const removeAvatar = () => {
    updateFormField("avatar", null)
    if (fileInputRef.current) {
      fileInputRef.current.value = ""
    }
  }

  // 触发文件选择
  const triggerFileInput = () => {
    fileInputRef.current?.click()
  }

  // 处理更新助理
  const handleUpdateAgent = async () => {
    if (!formData.name.trim()) {
      toast({
        title: "请输入名称",
        variant: "destructive",
      })
      return
    }

    setIsSubmitting(true)

    try {
      // 准备API请求参数
      const agentData = {
        name: formData.name,
        avatar: formData.avatar,
        description: formData.description || "",
        systemPrompt: selectedType === "chat" ? formData.systemPrompt : "",
        welcomeMessage: selectedType === "chat" ? formData.welcomeMessage : "",
        modelConfig: {
          modelName: formData.modelConfig.modelName,
          temperature: formData.modelConfig.temperature,
          maxTokens: formData.modelConfig.maxTokens,
        },
        tools: formData.tools.map((toolId) => {
          const tool = toolOptions.find((t) => t.id === toolId)
          return {
            id: toolId,
            name: tool?.name || toolId,
            description: tool?.description || "",
          }
        }),
        knowledgeBaseIds: selectedType === "chat" ? formData.knowledgeBaseIds : [],
      }

      // 调用API更新助理
      const response = await updateAgent(id, agentData)

      if (response.code === 200) {
        toast({
          title: "更新成功",
          description: `已更新${selectedType === "chat" ? "聊天助理" : "功能性助理"}: ${formData.name}`,
        })
      } else {
        toast({
          title: "更新失败",
          description: response.message,
          variant: "destructive",
        })
      }
    } catch (error) {
      console.error("更新失败:", error)
      toast({
        title: "更新失败",
        description: "请稍后再试",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  // 处理删除助理
  const handleDeleteAgent = async () => {
    setIsDeleting(true)

    try {
      const response = await deleteAgent(id)

      if (response.code === 200) {
        toast({
          title: "删除成功",
          description: "助理已成功删除",
        })
        router.push("/studio")
      } else {
        toast({
          title: "删除失败",
          description: response.message,
          variant: "destructive",
        })
      }
    } catch (error) {
      console.error("删除失败:", error)
      toast({
        title: "删除失败",
        description: "请稍后再试",
        variant: "destructive",
      })
    } finally {
      setIsDeleting(false)
      setShowDeleteDialog(false)
    }
  }

  // 处理切换助理状态
  const handleToggleStatus = async () => {
    setIsTogglingStatus(true)

    try {
      const response = await toggleAgentStatus(id)

      if (response.code === 200) {
        toast({
          title: response.data.enabled ? "已启用" : "已禁用",
          description: `助理 "${formData.name}" ${response.data.enabled ? "已启用" : "已禁用"}`,
        })
        // 更新表单数据
        updateFormField("enabled", response.data.enabled)
      } else {
        toast({
          title: "操作失败",
          description: response.message,
          variant: "destructive",
        })
      }
    } catch (error) {
      console.error("操作失败:", error)
      toast({
        title: "操作失败",
        description: "请稍后再试",
        variant: "destructive",
      })
    } finally {
      setIsTogglingStatus(false)
    }
  }

  // 处理发布助理版本
  const handlePublishVersion = async () => {
    if (!versionNumber.trim()) {
      toast({
        title: "请输入版本号",
        variant: "destructive",
      })
      return
    }

    setIsPublishing(true)

    try {
      // 先创建版本
      const createResponse = await createAgentVersion(id, {
        versionNumber,
        changeLog: changeLog || `发布 ${versionNumber} 版本`,
        systemPrompt: formData.systemPrompt,
        welcomeMessage: formData.welcomeMessage,
        modelConfig: formData.modelConfig,
        tools: formData.tools.map((toolId) => {
          const tool = toolOptions.find((t) => t.id === toolId)
          return {
            id: toolId,
            name: tool?.name || toolId,
            description: tool?.description || "",
          }
        }),
        knowledgeBaseIds: formData.knowledgeBaseIds,
      })

      if (createResponse.code !== 200 || !createResponse.data) {
        throw new Error(createResponse.message || "创建版本失败")
      }

      // 获取创建的版本ID
      const versionId = createResponse.data.id || createResponse.data.versionId
      if (!versionId) {
        throw new Error("创建版本成功，但未返回版本ID")
      }

      // 然后发布版本
      const publishResponse = await publishAgentVersion(id, versionId)

      if (publishResponse.code === 200) {
        toast({
          title: "发布成功",
          description: `已发布版本 ${versionNumber}`,
        })
        setShowPublishDialog(false)
        setVersionNumber("")
        setChangeLog("")
      } else {
        toast({
          title: "发布失败",
          description: publishResponse.message,
          variant: "destructive",
        })
      }
    } catch (error) {
      console.error("发布失败:", error)
      toast({
        title: "发布失败",
        description: "请稍后再试",
        variant: "destructive",
      })
    } finally {
      setIsPublishing(false)
    }
  }

  // 加载助理版本列表
  const loadVersions = async () => {
    setIsLoadingVersions(true)
    setVersions([])

    try {
      const response = await getAgentVersions(id)

      if (response.code === 200) {
        setVersions(response.data)
      } else {
        toast({
          title: "获取版本列表失败",
          description: response.message,
          variant: "destructive",
        })
      }
    } catch (error) {
      console.error("获取版本列表失败:", error)
      toast({
        title: "获取版本列表失败",
        description: "请稍后再试",
        variant: "destructive",
      })
    } finally {
      setIsLoadingVersions(false)
    }
  }

  // 查看版本详情
  const viewVersionDetail = async (version: AgentVersion) => {
    setSelectedVersion(version)
  }

  // 回滚到特定版本
  const rollbackToVersion = async (version: AgentVersion) => {
    if (!version) return

    setIsRollingBack(true)

    try {
      // 更新表单数据，将版本数据写回当前编辑页面
      setFormData({
        name: version.name,
        avatar: version.avatar,
        description: version.description,
        systemPrompt: version.systemPrompt,
        welcomeMessage: version.welcomeMessage,
        modelConfig: {
          modelName: version.modelConfig.modelName,
          temperature: version.modelConfig.temperature || 0.7,
          maxTokens: version.modelConfig.maxTokens || 2000,
        },
        tools: version.tools.map((tool) => tool.id),
        knowledgeBaseIds: version.knowledgeBaseIds,
        enabled: formData.enabled, // 保持当前启用/禁用状态
        agentType: version.agentType,
      })

      // 设置助理类型
      setSelectedType(version.agentType === 1 ? "chat" : "agent")

      toast({
        title: "回滚成功",
        description: `已回滚到版本 ${version.versionNumber}`,
      })

      // 关闭对话框
      setSelectedVersion(null)
      setShowVersionsDialog(false)
    } catch (error) {
      console.error("回滚失败:", error)
      toast({
        title: "回滚失败",
        description: "请稍后再试",
        variant: "destructive",
      })
    } finally {
      setIsRollingBack(false)
    }
  }

  // 根据选择的类型更新可用的标签页
  const getAvailableTabs = () => {
    if (selectedType === "chat") {
      return [
        { id: "basic", label: "基本信息" },
        { id: "prompt", label: "提示词配置" },
        { id: "model", label: "模型配置" },
        { id: "tools", label: "工具与知识库" },
      ]
    } else {
      return [
        { id: "basic", label: "基本信息" },
        { id: "model", label: "模型配置" },
        { id: "tools", label: "工具配置" },
      ]
    }
  }

  // 获取发布状态文本
  const getPublishStatusText = (status: number) => {
    switch (status) {
      case PublishStatus.REVIEWING:
        return "审核中"
      case PublishStatus.PUBLISHED:
        return "已发布"
      case PublishStatus.REJECTED:
        return "已拒绝"
      case PublishStatus.REMOVED:
        return "已下架"
      default:
        return "未知状态"
    }
  }

  // 如果正在加载，显示加载状态
  if (isLoading) {
    return (
      <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 overflow-auto p-4">
        <div className="bg-white rounded-lg shadow-xl w-full max-w-7xl flex max-h-[95vh] overflow-hidden">
          <div className="w-3/5 p-8 overflow-auto">
            <div className="flex items-center justify-between mb-6">
              <Skeleton className="h-8 w-64" />
              <Skeleton className="h-10 w-10 rounded-full" />
            </div>
            <div className="space-y-6">
              <Skeleton className="h-10 w-full" />
              <div className="space-y-4">
                <Skeleton className="h-6 w-32" />
                <div className="grid grid-cols-2 gap-4">
                  <Skeleton className="h-32 w-full" />
                  <Skeleton className="h-32 w-full" />
                </div>
              </div>
              <div className="space-y-4">
                <Skeleton className="h-6 w-32" />
                <div className="flex gap-4 items-center">
                  <Skeleton className="h-20 w-full" />
                  <Skeleton className="h-20 w-32" />
                </div>
              </div>
            </div>
          </div>
          <div className="w-2/5 bg-gray-50 p-8 overflow-auto border-l">
            <Skeleton className="h-8 w-32 mb-2" />
            <Skeleton className="h-4 w-64 mb-6" />
            <Skeleton className="h-[500px] w-full mb-6" />
            <Skeleton className="h-6 w-32 mb-3" />
            <Skeleton className="h-40 w-full" />
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 overflow-auto p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-7xl flex max-h-[95vh] overflow-hidden">
        {/* 左侧表单 */}
        <div className="w-3/5 p-8 overflow-auto">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-2">
              <Button variant="ghost" size="icon" asChild className="mr-2">
                <Link href="/studio">
                  <ArrowLeft className="h-5 w-5" />
                  <span className="sr-only">返回</span>
                </Link>
              </Button>
              <h1 className="text-2xl font-bold">编辑{selectedType === "chat" ? "聊天助理" : "功能性助理"}</h1>
            </div>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                onClick={() => {
                  setShowVersionsDialog(true)
                  loadVersions()
                }}
              >
                <History className="mr-2 h-4 w-4" />
                版本历史
              </Button>
              <Button variant="outline" onClick={() => setShowPublishDialog(true)}>
                发布版本
              </Button>
              <Button
                variant={formData.enabled ? "outline" : "default"}
                onClick={handleToggleStatus}
                disabled={isTogglingStatus}
              >
                {isTogglingStatus ? (
                  <>
                    <RefreshCw className="mr-2 h-4 w-4 animate-spin" />
                    处理中...
                  </>
                ) : formData.enabled ? (
                  <>
                    <PowerOff className="mr-2 h-4 w-4" />
                    禁用
                  </>
                ) : (
                  <>
                    <Power className="mr-2 h-4 w-4" />
                    启用
                  </>
                )}
              </Button>
              <Button variant="destructive" onClick={() => setShowDeleteDialog(true)}>
                删除
              </Button>
            </div>
          </div>

          <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
            <TabsList
              className="grid w-full"
              style={{ gridTemplateColumns: `repeat(${getAvailableTabs().length}, minmax(0, 1fr))` }}
            >
              {getAvailableTabs().map((tab) => (
                <TabsTrigger key={tab.id} value={tab.id}>
                  {tab.label}
                </TabsTrigger>
              ))}
            </TabsList>

            <TabsContent value="basic" className="space-y-6">
              {/* 名称和头像 */}
              <div>
                <h2 className="text-lg font-medium mb-4">名称 & 头像</h2>
                <div className="flex gap-4 items-center">
                  <div className="flex-1">
                    <Label htmlFor="agent-name" className="mb-2 block">
                      名称
                    </Label>
                    <Input
                      id="agent-name"
                      placeholder={`给你的${selectedType === "chat" ? "聊天助理" : "功能性助理"}起个名字`}
                      value={formData.name}
                      onChange={(e) => updateFormField("name", e.target.value)}
                      className="mb-2"
                    />
                  </div>
                  <div>
                    <Label className="mb-2 block">头像</Label>
                    <div className="flex items-center gap-2">
                      <Avatar className="h-12 w-12">
                        <AvatarImage src={formData.avatar || ""} alt="Avatar" />
                        <AvatarFallback className="bg-blue-100 text-blue-600">
                          {formData.name ? formData.name.charAt(0).toUpperCase() : "🤖"}
                        </AvatarFallback>
                      </Avatar>
                      <div className="flex flex-col gap-1">
                        <Button variant="outline" size="sm" onClick={triggerFileInput}>
                          <Upload className="h-4 w-4 mr-2" />
                          上传
                        </Button>
                        {formData.avatar && (
                          <Button variant="outline" size="sm" onClick={removeAvatar}>
                            <Trash className="h-4 w-4 mr-2" />
                            移除
                          </Button>
                        )}
                      </div>
                      <input
                        type="file"
                        ref={fileInputRef}
                        className="hidden"
                        accept="image/*"
                        onChange={handleAvatarUpload}
                      />
                    </div>
                  </div>
                </div>
              </div>

              {/* 描述 */}
              <div>
                <h2 className="text-lg font-medium mb-2">描述</h2>
                <Textarea
                  placeholder={`输入${selectedType === "chat" ? "聊天助理" : "功能性助理"}的描述`}
                  value={formData.description}
                  onChange={(e) => updateFormField("description", e.target.value)}
                  rows={4}
                />
              </div>

              {/* 状态信息 */}
              <div>
                <h2 className="text-lg font-medium mb-4">状态信息</h2>
                <div className="flex items-center gap-2 p-4 bg-gray-50 rounded-lg border">
                  <div className="flex-1">
                    <p className="text-sm text-muted-foreground">当前状态</p>
                    <p className="font-medium">{formData.enabled ? "已启用" : "已禁用"}</p>
                  </div>
                  <Badge variant={formData.enabled ? "default" : "outline"}>
                    {formData.enabled ? "已启用" : "已禁用"}
                  </Badge>
                </div>
              </div>
            </TabsContent>

            {/* 仅聊天助理显示提示词配置 */}
            {selectedType === "chat" && (
              <TabsContent value="prompt" className="space-y-6">
                {/* 系统提示词 */}
                <div>
                  <h2 className="text-lg font-medium mb-2">系统提示词</h2>
                  <p className="text-sm text-muted-foreground mb-2">定义聊天助理的角色、能力和行为限制</p>
                  <Textarea
                    placeholder="输入系统提示词"
                    value={formData.systemPrompt}
                    onChange={(e) => updateFormField("systemPrompt", e.target.value)}
                    rows={8}
                  />
                </div>

                {/* 欢迎消息 */}
                <div>
                  <h2 className="text-lg font-medium mb-2">欢迎消息</h2>
                  <p className="text-sm text-muted-foreground mb-2">用户首次与聊天助理交互时显示的消息</p>
                  <Textarea
                    placeholder="输入欢迎消息"
                    value={formData.welcomeMessage}
                    onChange={(e) => updateFormField("welcomeMessage", e.target.value)}
                    rows={4}
                  />
                </div>
              </TabsContent>
            )}

            <TabsContent value="model" className="space-y-6">
              {/* 模型选择 */}
              <div>
                <h2 className="text-lg font-medium mb-2">选择模型</h2>
                <p className="text-sm text-muted-foreground mb-2">
                  选择{selectedType === "chat" ? "聊天助理" : "功能性助理"}使用的大语言模型
                </p>
                <Select
                  value={formData.modelConfig.modelName}
                  onValueChange={(value) => updateModelConfig("modelName", value)}
                >
                  <SelectTrigger className="w-full">
                    <SelectValue placeholder="选择模型" />
                  </SelectTrigger>
                  <SelectContent>
                    {modelOptions.map((model) => (
                      <SelectItem key={model.value} value={model.value}>
                        {model.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              {/* 温度设置 */}
              <div>
                <h2 className="text-lg font-medium mb-2">温度</h2>
                <p className="text-sm text-muted-foreground mb-2">
                  控制输出的随机性：较低的值使输出更确定，较高的值使输出更多样化
                </p>
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span className="text-sm">精确</span>
                    <span className="text-sm font-medium">{formData.modelConfig.temperature.toFixed(1)}</span>
                    <span className="text-sm">创意</span>
                  </div>
                  <Slider
                    value={[formData.modelConfig.temperature]}
                    min={0}
                    max={1}
                    step={0.1}
                    onValueChange={(value) => updateModelConfig("temperature", value[0])}
                  />
                </div>
              </div>

              {/* 最大Token */}
              <div>
                <h2 className="text-lg font-medium mb-2">最大输出Token</h2>
                <p className="text-sm text-muted-foreground mb-2">限制模型单次回复的最大长度</p>
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span className="text-sm">简短</span>
                    <span className="text-sm font-medium">{formData.modelConfig.maxTokens}</span>
                    <span className="text-sm">详细</span>
                  </div>
                  <Slider
                    value={[formData.modelConfig.maxTokens]}
                    min={500}
                    max={4000}
                    step={100}
                    onValueChange={(value) => updateModelConfig("maxTokens", value[0])}
                  />
                </div>
              </div>
            </TabsContent>

            <TabsContent value="tools" className="space-y-6">
              {/* 工具选择 */}
              <div>
                <h2 className="text-lg font-medium mb-2">可用工具</h2>
                <p className="text-sm text-muted-foreground mb-2">
                  选择{selectedType === "chat" ? "聊天助理" : "功能性助理"}可以使用的工具
                </p>
                <div className="grid grid-cols-2 gap-4 mt-4">
                  {toolOptions.map((tool) => (
                    <div
                      key={tool.id}
                      className={`border rounded-lg p-4 cursor-pointer transition-all ${
                        formData.tools.includes(tool.id) ? "border-blue-500 bg-blue-50" : "hover:border-gray-300"
                      }`}
                      onClick={() => toggleTool(tool.id)}
                    >
                      <div className="flex items-center justify-between mb-2">
                        <h3 className="font-medium">{tool.name}</h3>
                        <Switch checked={formData.tools.includes(tool.id)} />
                      </div>
                      <p className="text-sm text-muted-foreground">{tool.description}</p>
                    </div>
                  ))}
                </div>
              </div>

              {/* 知识库选择 - 仅聊天助理显示 */}
              {selectedType === "chat" && (
                <div>
                  <h2 className="text-lg font-medium mb-2">关联知识库</h2>
                  <p className="text-sm text-muted-foreground mb-2">选择聊天助理可以访问的知识库</p>
                  <div className="grid grid-cols-2 gap-4 mt-4">
                    {knowledgeBaseOptions.map((kb) => (
                      <div
                        key={kb.id}
                        className={`border rounded-lg p-4 cursor-pointer transition-all ${
                          formData.knowledgeBaseIds.includes(kb.id)
                            ? "border-blue-500 bg-blue-50"
                            : "hover:border-gray-300"
                        }`}
                        onClick={() => toggleKnowledgeBase(kb.id)}
                      >
                        <div className="flex items-center justify-between mb-2">
                          <h3 className="font-medium">{kb.name}</h3>
                          <Switch checked={formData.knowledgeBaseIds.includes(kb.id)} />
                        </div>
                        <p className="text-sm text-muted-foreground">{kb.description}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </TabsContent>
          </Tabs>

          {/* 底部按钮 */}
          <div className="flex justify-end pt-6 border-t mt-6">
            <div className="space-x-2">
              <Button variant="outline" asChild>
                <Link href="/studio">取消</Link>
              </Button>
              <Button onClick={handleUpdateAgent} disabled={isSubmitting}>
                {isSubmitting ? "保存中..." : "保存更改"}
              </Button>
            </div>
          </div>
        </div>

        {/* 右侧预览 - 根据类型显示不同内容 */}
        <div className="w-2/5 bg-gray-50 p-8 overflow-auto border-l">
          <div className="mb-6">
            <h2 className="text-xl font-semibold">预览</h2>
            <p className="text-muted-foreground">
              {selectedType === "chat" ? "查看聊天助理在对话中的表现" : "查看功能性助理处理复杂任务的界面"}
            </p>
          </div>

          {/* 聊天助理预览 */}
          {selectedType === "chat" && (
            <div className="border rounded-lg bg-white shadow-sm overflow-hidden">
              <div className="border-b p-3 flex items-center justify-between bg-gray-50">
                <div className="flex items-center gap-2">
                  <Avatar className="h-8 w-8">
                    <AvatarImage src={formData.avatar || ""} alt="Avatar" />
                    <AvatarFallback className="bg-blue-100 text-blue-600">
                      {formData.name ? formData.name.charAt(0).toUpperCase() : "🤖"}
                    </AvatarFallback>
                  </Avatar>
                  <span className="font-medium">{formData.name || "聊天助理"}</span>
                </div>
                <Badge variant="outline">{formData.modelConfig.modelName}</Badge>
              </div>

              <div className="h-[500px] flex flex-col">
                <div className="flex-1 p-4 overflow-auto space-y-4 bg-gray-50">
                  {/* 欢迎消息 */}
                  <div className="flex items-start gap-3">
                    <Avatar className="h-8 w-8 mt-1">
                      <AvatarImage src={formData.avatar || ""} alt="Avatar" />
                      <AvatarFallback className="bg-blue-100 text-blue-600">
                        {formData.name ? formData.name.charAt(0).toUpperCase() : "🤖"}
                      </AvatarFallback>
                    </Avatar>
                    <div className="bg-white rounded-lg p-3 shadow-sm max-w-[80%]">
                      {formData.welcomeMessage || "你好！我是你的AI助手，有什么可以帮助你的吗？"}
                    </div>
                  </div>

                  {/* 用户消息示例 */}
                  <div className="flex items-start gap-3 justify-end">
                    <div className="bg-blue-100 rounded-lg p-3 shadow-sm max-w-[80%] text-blue-900">你能做什么？</div>
                    <Avatar className="h-8 w-8 mt-1">
                      <AvatarImage src="/placeholder.svg?height=32&width=32" alt="User" />
                      <AvatarFallback className="bg-blue-500 text-white">U</AvatarFallback>
                    </Avatar>
                  </div>

                  {/* 助手回复示例 */}
                  <div className="flex items-start gap-3">
                    <Avatar className="h-8 w-8 mt-1">
                      <AvatarImage src={formData.avatar || ""} alt="Avatar" />
                      <AvatarFallback className="bg-blue-100 text-blue-600">
                        {formData.name ? formData.name.charAt(0).toUpperCase() : "🤖"}
                      </AvatarFallback>
                    </Avatar>
                    <div className="bg-white rounded-lg p-3 shadow-sm max-w-[80%]">
                      <p>我可以帮助你完成以下任务：</p>
                      <ul className="list-disc pl-5 mt-2 space-y-1">
                        <li>回答问题和提供信息</li>
                        <li>协助写作和内容创作</li>
                        {formData.tools.includes("web-search") && <li>搜索互联网获取最新信息</li>}
                        {formData.tools.includes("file-reader") && <li>分析和解读上传的文件</li>}
                        {formData.tools.includes("code-interpreter") && <li>编写和执行代码</li>}
                        {formData.tools.includes("image-generation") && <li>生成和编辑图像</li>}
                        {formData.tools.includes("calculator") && <li>执行数学计算</li>}
                        {formData.knowledgeBaseIds.length > 0 && <li>基于专业知识库提供准确信息</li>}
                      </ul>
                      <p className="mt-2">有什么具体问题我可以帮你解答吗？</p>
                    </div>
                  </div>
                </div>

                {/* 输入框 */}
                <div className="p-4 border-t">
                  <div className="flex gap-2">
                    <Input placeholder="输入消息..." className="flex-1" disabled />
                    <Button size="icon" disabled>
                      <MessageCircle className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* 功能性助理预览 */}
          {selectedType === "agent" && (
            <div className="border rounded-lg bg-white shadow-sm overflow-hidden">
              <div className="border-b p-3 flex items-center justify-between bg-gray-50">
                <div className="flex items-center gap-2">
                  <Avatar className="h-8 w-8">
                    <AvatarImage src={formData.avatar || ""} alt="Avatar" />
                    <AvatarFallback className="bg-purple-100 text-purple-600">
                      {formData.name ? formData.name.charAt(0).toUpperCase() : "🤖"}
                    </AvatarFallback>
                  </Avatar>
                  <span className="font-medium">{formData.name || "功能性助理"}</span>
                </div>
                <Badge variant="outline">{formData.modelConfig.modelName}</Badge>
              </div>

              <div className="h-[500px] flex flex-col">
                <div className="flex-1 p-4 overflow-auto space-y-4">
                  {/* 助理任务界面 */}
                  <div className="bg-gray-50 rounded-lg p-4 border">
                    <h3 className="font-medium mb-2">任务描述</h3>
                    <p className="text-sm text-muted-foreground mb-4">请助理帮我分析以下数据并生成报告。</p>
                    <div className="flex items-center gap-2 mb-4">
                      <Button variant="outline" size="sm" disabled>
                        <FileText className="h-4 w-4 mr-2" />
                        上传文件
                      </Button>
                      <Button variant="outline" size="sm" disabled>
                        <Workflow className="h-4 w-4 mr-2" />
                        选择工作流
                      </Button>
                    </div>
                  </div>

                  {/* 任务执行状态 */}
                  <div className="space-y-4">
                    <div className="bg-white rounded-lg p-4 border">
                      <div className="flex items-center justify-between mb-2">
                        <h3 className="font-medium">任务执行中</h3>
                        <Badge variant="outline" className="bg-blue-50">
                          进行中
                        </Badge>
                      </div>
                      <div className="space-y-3">
                        <div>
                          <div className="flex justify-between text-sm mb-1">
                            <span>分析数据</span>
                            <span>完成</span>
                          </div>
                          <Progress value={100} className="h-2" />
                        </div>
                        <div>
                          <div className="flex justify-between text-sm mb-1">
                            <span>生成报告</span>
                            <span>60%</span>
                          </div>
                          <Progress value={60} className="h-2" />
                        </div>
                        <div>
                          <div className="flex justify-between text-sm mb-1">
                            <span>格式化输出</span>
                            <span>等待中</span>
                          </div>
                          <Progress value={0} className="h-2" />
                        </div>
                      </div>
                    </div>

                    {/* 工具使用记录 */}
                    <div className="bg-white rounded-lg p-4 border">
                      <h3 className="font-medium mb-2">工具使用记录</h3>
                      <div className="space-y-2">
                        {formData.tools.includes("file-reader") && (
                          <div className="flex items-center gap-2 text-sm p-2 bg-gray-50 rounded">
                            <FileText className="h-4 w-4 text-blue-500" />
                            <span>已读取文件：数据分析.xlsx</span>
                          </div>
                        )}
                        {formData.tools.includes("code-interpreter") && (
                          <div className="flex items-center gap-2 text-sm p-2 bg-gray-50 rounded">
                            <Zap className="h-4 w-4 text-purple-500" />
                            <span>执行代码：数据处理脚本</span>
                          </div>
                        )}
                        {formData.tools.includes("web-search") && (
                          <div className="flex items-center gap-2 text-sm p-2 bg-gray-50 rounded">
                            <Search className="h-4 w-4 text-green-500" />
                            <span>搜索相关信息：市场趋势分析</span>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </div>

                {/* 底部操作栏 */}
                <div className="p-4 border-t">
                  <div className="flex gap-2">
                    <Button variant="outline" className="flex-1" disabled>
                      取消任务
                    </Button>
                    <Button className="flex-1" disabled>
                      查看结果
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* 配置摘要 */}
          <div className="mt-6">
            <h3 className="text-lg font-medium mb-3">配置摘要</h3>
            <Card>
              <CardContent className="p-4 space-y-3">
                <div className="flex justify-between">
                  <span className="text-sm text-muted-foreground">类型</span>
                  <span className="text-sm font-medium">{selectedType === "chat" ? "聊天助理" : "功能性助理"}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-muted-foreground">模型</span>
                  <span className="text-sm font-medium">{formData.modelConfig.modelName}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-muted-foreground">温度</span>
                  <span className="text-sm font-medium">{formData.modelConfig.temperature.toFixed(1)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-muted-foreground">工具数量</span>
                  <span className="text-sm font-medium">{formData.tools.length}</span>
                </div>
                {selectedType === "chat" && (
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">知识库数量</span>
                    <span className="text-sm font-medium">{formData.knowledgeBaseIds.length}</span>
                  </div>
                )}
                <div className="flex justify-between">
                  <span className="text-sm text-muted-foreground">状态</span>
                  <Badge variant={formData.enabled ? "default" : "outline"} className="text-xs">
                    {formData.enabled ? "已启用" : "已禁用"}
                  </Badge>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      {/* 删除确认对话框 */}
      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>确认删除</DialogTitle>
            <DialogDescription>您确定要删除这个助理吗？此操作无法撤销。</DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowDeleteDialog(false)}>
              取消
            </Button>
            <Button variant="destructive" onClick={handleDeleteAgent} disabled={isDeleting}>
              {isDeleting ? "删除中..." : "确认删除"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 发布版本对话框 */}
      <Dialog open={showPublishDialog} onOpenChange={setShowPublishDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>发布新版本</DialogTitle>
            <DialogDescription>发布新版本将创建当前配置的快照，用户可以使用此版本。</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="version-number">版本号</Label>
              <Input
                id="version-number"
                placeholder="例如: 1.0.0"
                value={versionNumber}
                onChange={(e) => setVersionNumber(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="change-log">更新日志</Label>
              <Textarea
                id="change-log"
                placeholder="描述此版本的更新内容"
                rows={4}
                value={changeLog}
                onChange={(e) => setChangeLog(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowPublishDialog(false)}>
              取消
            </Button>
            <Button onClick={handlePublishVersion} disabled={isPublishing}>
              {isPublishing ? "发布中..." : "发布版本"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 版本历史对话框 */}
      <Dialog open={showVersionsDialog} onOpenChange={setShowVersionsDialog}>
        <DialogContent className="max-w-4xl max-h-[80vh] overflow-hidden flex flex-col">
          <DialogHeader>
            <DialogTitle>版本历史</DialogTitle>
            <DialogDescription>查看和管理助理的历史版本</DialogDescription>
          </DialogHeader>
          <div className="flex-1 overflow-auto py-4">
            {isLoadingVersions ? (
              <div className="flex items-center justify-center py-8">
                <RefreshCw className="h-6 w-6 animate-spin text-blue-500" />
                <span className="ml-2">加载版本历史...</span>
              </div>
            ) : versions.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">暂无版本历史</div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>版本号</TableHead>
                    <TableHead>发布时间</TableHead>
                    <TableHead>状态</TableHead>
                    <TableHead>更新日志</TableHead>
                    <TableHead className="text-right">操作</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {versions.map((version) => (
                    <TableRow key={version.id}>
                      <TableCell className="font-medium">{version.versionNumber}</TableCell>
                      <TableCell>{new Date(version.publishedAt).toLocaleString()}</TableCell>
                      <TableCell>
                        <Badge variant={version.publishStatus === PublishStatus.PUBLISHED ? "default" : "outline"}>
                          {getPublishStatusText(version.publishStatus)}
                        </Badge>
                      </TableCell>
                      <TableCell className="max-w-[200px] truncate">{version.changeLog}</TableCell>
                      <TableCell className="text-right">
                        <Button variant="outline" size="sm" className="mr-2" onClick={() => viewVersionDetail(version)}>
                          查看
                        </Button>
                        <Button size="sm" onClick={() => rollbackToVersion(version)} disabled={isRollingBack}>
                          {isRollingBack ? "回滚中..." : "回滚"}
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </div>
        </DialogContent>
      </Dialog>

      {/* 版本详情对话框 */}
      {selectedVersion && (
        <Dialog open={!!selectedVersion} onOpenChange={(open) => !open && setSelectedVersion(null)}>
          <DialogContent className="max-w-3xl max-h-[80vh] overflow-auto">
            <DialogHeader>
              <DialogTitle>版本详情: {selectedVersion.versionNumber}</DialogTitle>
              <DialogDescription>发布于 {new Date(selectedVersion.publishedAt).toLocaleString()}</DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-4">
              <div className="flex items-center gap-4">
                <Avatar className="h-12 w-12">
                  <AvatarImage src={selectedVersion.avatar || ""} alt="Avatar" />
                  <AvatarFallback className="bg-blue-100 text-blue-600">
                    {selectedVersion.name ? selectedVersion.name.charAt(0).toUpperCase() : "🤖"}
                  </AvatarFallback>
                </Avatar>
                <div>
                  <h3 className="font-medium">{selectedVersion.name}</h3>
                  <p className="text-sm text-muted-foreground">{selectedVersion.description}</p>
                </div>
              </div>

              <div className="space-y-2">
                <h3 className="font-medium">更新日志</h3>
                <div className="p-3 bg-gray-50 rounded-md">{selectedVersion.changeLog}</div>
              </div>

              <div className="space-y-2">
                <h3 className="font-medium">配置信息</h3>
                <div className="space-y-1">
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">模型</span>
                    <span className="text-sm">{selectedVersion.modelConfig.modelName}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">温度</span>
                    <span className="text-sm">{selectedVersion.modelConfig.temperature?.toFixed(1) || "0.7"}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">最大Token</span>
                    <span className="text-sm">{selectedVersion.modelConfig.maxTokens || "2000"}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">工具数量</span>
                    <span className="text-sm">{selectedVersion.tools.length}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">知识库数量</span>
                    <span className="text-sm">{selectedVersion.knowledgeBaseIds.length}</span>
                  </div>
                </div>
              </div>

              {selectedVersion.agentType === 1 && (
                <>
                  <div className="space-y-2">
                    <h3 className="font-medium">系统提示词</h3>
                    <div className="p-3 bg-gray-50 rounded-md text-sm">
                      {selectedVersion.systemPrompt || "无系统提示词"}
                    </div>
                  </div>

                  <div className="space-y-2">
                    <h3 className="font-medium">欢迎消息</h3>
                    <div className="p-3 bg-gray-50 rounded-md text-sm">
                      {selectedVersion.welcomeMessage || "无欢迎消息"}
                    </div>
                  </div>
                </>
              )}
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={() => setSelectedVersion(null)}>
                关闭
              </Button>
              <Button onClick={() => rollbackToVersion(selectedVersion)} disabled={isRollingBack}>
                {isRollingBack ? "回滚中..." : "回滚到此版本"}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      )}
    </div>
  )
}

