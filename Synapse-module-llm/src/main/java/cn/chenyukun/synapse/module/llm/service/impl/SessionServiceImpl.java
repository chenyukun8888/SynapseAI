package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.module.llm.mapper.MessageMapper;
import cn.chenyukun.synapse.module.llm.mapper.SessionMapper;
import cn.chenyukun.synapse.module.llm.model.dto.CreateSessionRequest;
import cn.chenyukun.synapse.module.llm.model.dto.UpdateSessionRequest;
import cn.chenyukun.synapse.module.llm.model.entity.MessageEntity;
import cn.chenyukun.synapse.module.llm.model.entity.SessionEntity;
import cn.chenyukun.synapse.module.llm.model.vo.SessionListItemVO;
import cn.chenyukun.synapse.module.llm.model.vo.SessionListVO;
import cn.chenyukun.synapse.module.llm.model.vo.SessionVO;
import cn.chenyukun.synapse.module.llm.service.SessionService;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 会话服务实现
 */
@Service
public class SessionServiceImpl implements SessionService {

    private final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);

    @Resource
    private SessionMapper sessionMapper;

    @Resource
    private MessageMapper messageMapper;

    private static final String DEFAULT_MODEL = "Qwen/Qwen2.5-7B-Instruct";
    private static final String DEFAULT_TITLE_PREFIX = "新对话 ";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionVO createSession(CreateSessionRequest request, String userId) {
        logger.info("创建会话，用户ID: {}, 标题: {}", userId, request.getTitle());

        SessionEntity entity = new SessionEntity();
        entity.setId(IdUtil.simpleUUID());
        entity.setUserId(userId);
        entity.setTitle(StrUtil.isNotBlank(request.getTitle()) ? request.getTitle() : generateDefaultTitle());
        entity.setModel(StrUtil.isNotBlank(request.getModel()) ? request.getModel() : DEFAULT_MODEL);
        entity.setSystemPrompt(request.getSystemPrompt());
        entity.setDescription(request.getDescription());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setIsArchived(false);

        sessionMapper.insert(entity);

        logger.info("会话创建成功，会话ID: {}", entity.getId());

        return convertToVO(entity, 0, 0L);
    }

    @Override
    public SessionListVO getSessionList(String userId, Integer page, Integer pageSize, String sortBy, String order) {
        logger.info("获取会话列表，用户ID: {}, 页码: {}, 每页数量: {}", userId, page, pageSize);

        // 参数校验和默认值
        page = (page == null || page < 1) ? 1 : page;
        pageSize = (pageSize == null || pageSize < 1) ? 20 : Math.min(pageSize, 100);
        sortBy = StrUtil.isBlank(sortBy) ? "updatedAt" : sortBy;
        order = StrUtil.isBlank(order) ? "desc" : order;

        // 查询总数
        long total = sessionMapper.countByUserId(userId, false);

        // 查询会话列表
        List<SessionEntity> entities;
        if ("createdAt".equals(sortBy)) {
            entities = sessionMapper.selectByUserIdOrderByCreatedAt(userId, false);
        } else {
            entities = sessionMapper.selectByUserIdOrderByUpdatedAt(userId, false);
        }

        // 分页处理
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, entities.size());
        List<SessionEntity> pagedEntities = fromIndex < entities.size() 
            ? entities.subList(fromIndex, toIndex) 
            : new ArrayList<>();

        // 转换为 VO
        List<SessionListItemVO> sessionVOs = new ArrayList<>();
        for (SessionEntity entity : pagedEntities) {
            SessionListItemVO itemVO = new SessionListItemVO();
            itemVO.setSessionId(entity.getId());
            itemVO.setTitle(entity.getTitle());
            itemVO.setModel(entity.getModel());
            itemVO.setCreatedAt(entity.getCreatedAt());
            itemVO.setUpdatedAt(entity.getUpdatedAt());

            // 查询消息数量
            long messageCount = messageMapper.countBySessionId(entity.getId());
            itemVO.setMessageCount((int) messageCount);

            // 查询最后一条消息
            MessageEntity lastMessage = messageMapper.selectLastMessageBySessionId(entity.getId());
            if (lastMessage != null) {
                SessionListItemVO.LastMessageVO lastMessageVO = new SessionListItemVO.LastMessageVO();
                lastMessageVO.setRole(lastMessage.getRole());
                lastMessageVO.setContent(truncateContent(lastMessage.getContent(), 100));
                lastMessageVO.setCreatedAt(lastMessage.getCreatedAt());
                itemVO.setLastMessage(lastMessageVO);
            }

            sessionVOs.add(itemVO);
        }

        int totalPages = (int) Math.ceil((double) total / pageSize);
        SessionListVO result = new SessionListVO(total, page, pageSize, totalPages, sessionVOs);

        logger.info("会话列表查询成功，总数: {}, 当前页: {}", total, page);
        return result;
    }

    @Override
    public SessionVO getSessionDetail(String sessionId, String userId) {
        logger.info("获取会话详情，会话ID: {}, 用户ID: {}", sessionId, userId);

        SessionEntity entity = sessionMapper.selectById(sessionId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            logger.error("会话不存在或无权访问，会话ID: {}, 用户ID: {}", sessionId, userId);
            throw new RuntimeException("会话不存在或无权访问");
        }

        long messageCount = messageMapper.countBySessionId(sessionId);
        long tokensUsed = messageMapper.sumTokensBySessionId(sessionId);

        return convertToVO(entity, (int) messageCount, tokensUsed);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionVO updateSession(String sessionId, UpdateSessionRequest request, String userId) {
        logger.info("更新会话，会话ID: {}, 用户ID: {}", sessionId, userId);

        SessionEntity entity = sessionMapper.selectById(sessionId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            logger.error("会话不存在或无权访问，会话ID: {}, 用户ID: {}", sessionId, userId);
            throw new RuntimeException("会话不存在或无权访问");
        }

        // 更新字段
        if (StrUtil.isNotBlank(request.getTitle())) {
            entity.setTitle(request.getTitle());
        }
        if (request.getSystemPrompt() != null) {
            entity.setSystemPrompt(request.getSystemPrompt());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        entity.setUpdatedAt(LocalDateTime.now());

        sessionMapper.updateById(entity);

        logger.info("会话更新成功，会话ID: {}", sessionId);

        long messageCount = messageMapper.countBySessionId(sessionId);
        long tokensUsed = messageMapper.sumTokensBySessionId(sessionId);

        return convertToVO(entity, (int) messageCount, tokensUsed);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(String sessionId, String userId) {
        logger.info("删除会话，会话ID: {}, 用户ID: {}", sessionId, userId);

        SessionEntity entity = sessionMapper.selectById(sessionId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            logger.error("会话不存在或无权访问，会话ID: {}, 用户ID: {}", sessionId, userId);
            throw new RuntimeException("会话不存在或无权访问");
        }

        // 删除会话（外键会级联删除消息）
        sessionMapper.deleteById(sessionId);

        logger.info("会话删除成功，会话ID: {}", sessionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archiveSession(String sessionId, String userId) {
        logger.info("归档会话，会话ID: {}, 用户ID: {}", sessionId, userId);

        SessionEntity entity = sessionMapper.selectById(sessionId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            logger.error("会话不存在或无权访问，会话ID: {}, 用户ID: {}", sessionId, userId);
            throw new RuntimeException("会话不存在或无权访问");
        }

        entity.setIsArchived(true);
        entity.setUpdatedAt(LocalDateTime.now());
        sessionMapper.updateById(entity);

        logger.info("会话归档成功，会话ID: {}", sessionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearSessionMessages(String sessionId, String userId) {
        logger.info("清空会话消息，会话ID: {}, 用户ID: {}", sessionId, userId);

        SessionEntity entity = sessionMapper.selectById(sessionId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            logger.error("会话不存在或无权访问，会话ID: {}, 用户ID: {}", sessionId, userId);
            throw new RuntimeException("会话不存在或无权访问");
        }

        messageMapper.deleteBySessionId(sessionId);

        entity.setUpdatedAt(LocalDateTime.now());
        sessionMapper.updateById(entity);

        logger.info("会话消息清空成功，会话ID: {}", sessionId);
    }

    /**
     * 生成默认标题
     */
    private String generateDefaultTitle() {
        return DEFAULT_TITLE_PREFIX + System.currentTimeMillis();
    }

    /**
     * 转换为 VO
     */
    private SessionVO convertToVO(SessionEntity entity, int messageCount, long tokensUsed) {
        SessionVO vo = new SessionVO();
        vo.setSessionId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setModel(entity.getModel());
        vo.setSystemPrompt(entity.getSystemPrompt());
        vo.setDescription(entity.getDescription());
        vo.setMessageCount(messageCount);
        vo.setTokensUsed(tokensUsed);
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        vo.setIsArchived(entity.getIsArchived());
        return vo;
    }

    /**
     * 截断内容
     */
    private String truncateContent(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}

