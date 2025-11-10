package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.model.dto.CreateSessionRequest;
import cn.chenyukun.synapse.module.llm.model.dto.UpdateSessionRequest;
import cn.chenyukun.synapse.module.llm.model.vo.SessionListVO;
import cn.chenyukun.synapse.module.llm.model.vo.SessionVO;

/**
 * 会话服务接口
 */
public interface SessionService {

    /**
     * 创建会话
     * @param request 创建会话请求
     * @param userId 用户ID
     * @return 会话详情
     */
    SessionVO createSession(CreateSessionRequest request, String userId);

    /**
     * 获取会话列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param sortBy 排序字段
     * @param order 排序方式
     * @return 会话列表
     */
    SessionListVO getSessionList(String userId, Integer page, Integer pageSize, String sortBy, String order);

    /**
     * 获取会话详情
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 会话详情
     */
    SessionVO getSessionDetail(String sessionId, String userId);

    /**
     * 更新会话
     * @param sessionId 会话ID
     * @param request 更新会话请求
     * @param userId 用户ID
     * @return 更新后的会话详情
     */
    SessionVO updateSession(String sessionId, UpdateSessionRequest request, String userId);

    /**
     * 删除会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     */
    void deleteSession(String sessionId, String userId);

    /**
     * 归档会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     */
    void archiveSession(String sessionId, String userId);

    /**
     * 清空会话消息
     * @param sessionId 会话ID
     * @param userId 用户ID
     */
    void clearSessionMessages(String sessionId, String userId);
}

