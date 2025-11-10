package cn.chenyukun.synapse.module.agent.service.impl;

import cn.chenyukun.synapse.module.agent.converter.AgentConverter;
import cn.chenyukun.synapse.module.agent.mapper.AgentMapper;
import cn.chenyukun.synapse.module.agent.model.dto.CreateAgentRequest;
import cn.chenyukun.synapse.module.agent.model.dto.UpdateAgentRequest;
import cn.chenyukun.synapse.module.agent.model.entity.AgentEntity;
import cn.chenyukun.synapse.module.agent.model.vo.AgentDetailVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentListVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentListItemVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentStatisticsVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentVO;
import cn.chenyukun.synapse.module.agent.service.AgentService;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Agent服务实现类
 */
@Service
public class AgentServiceImpl implements AgentService {

    private final Logger logger = LoggerFactory.getLogger(AgentServiceImpl.class);

    @Resource
    private AgentMapper agentMapper;

    @Resource
    private AgentConverter agentConverter;

    private static final String DEFAULT_MODEL = "deepseek-chat";
    private static final Double DEFAULT_TEMPERATURE = 0.7;
    private static final Integer DEFAULT_MAX_TOKENS = 2000;
    private static final String DEFAULT_TYPE = "CHAT";
    private static final String DEFAULT_STATUS = "DRAFT";
    private static final String DEFAULT_VERSION = "1.0.0";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgentVO createAgent(CreateAgentRequest request, String userId) {
        logger.info("创建Agent，用户ID: {}, 名称: {}", userId, request.getName());

        // 检查名称是否已存在
        LambdaQueryWrapper<AgentEntity> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(AgentEntity::getName, request.getName())
                .eq(AgentEntity::getUserId, userId)
                .isNull(AgentEntity::getDeletedAt);
        if (agentMapper.selectCount(checkWrapper) > 0) {
            throw new RuntimeException("Agent名称已存在");
        }

        // 构建Entity
        AgentEntity entity = new AgentEntity();
        entity.setId(IdUtil.simpleUUID());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setAvatar(request.getAvatar());
        entity.setSystemPrompt(request.getSystemPrompt());
        entity.setUserId(userId);
        entity.setEnabled(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // 设置Agent类型（CHAT/TASK/TOOL -> 1/2/3）
        String type = StrUtil.isNotBlank(request.getType()) ? request.getType() : DEFAULT_TYPE;
        entity.setAgentType(convertTypeToInteger(type));

        // 构建modelConfig JSON
        Map<String, Object> modelConfig = new HashMap<>();
        modelConfig.put("model", StrUtil.isNotBlank(request.getModel()) ? request.getModel() : DEFAULT_MODEL);
        modelConfig.put("temperature", request.getTemperature() != null ? request.getTemperature() : DEFAULT_TEMPERATURE);
        modelConfig.put("maxTokens", request.getMaxTokens() != null ? request.getMaxTokens() : DEFAULT_MAX_TOKENS);
        entity.setModelConfig(modelConfig);

        // 设置工具列表
        if (request.getTools() != null && !request.getTools().isEmpty()) {
            List<Map<String, Object>> toolsList = request.getTools().stream()
                    .map(toolName -> {
                        Map<String, Object> tool = new HashMap<>();
                        tool.put("name", toolName);
                        tool.put("enabled", true);
                        return tool;
                    })
                    .collect(Collectors.toList());
            entity.setTools(toolsList);
        }

        // 设置知识库ID列表
        entity.setKnowledgeBaseIds(request.getKnowledgeBaseIds());

        // 保存到数据库
        agentMapper.insert(entity);

        logger.info("Agent创建成功，Agent ID: {}", entity.getId());

        // 转换为VO
        AgentVO vo = agentConverter.toVO(entity);
        vo.setStatus(DEFAULT_STATUS);
        vo.setVersion(DEFAULT_VERSION);
        vo.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : false);
        return vo;
    }

    @Override
    public AgentListVO getAgentList(Integer page, Integer pageSize, String type, String status,
                                    String keyword, Boolean isPublic, String userId) {
        logger.info("获取Agent列表，用户ID: {}, 页码: {}, 每页数量: {}", userId, page, pageSize);

        // 参数校验和默认值
        page = (page == null || page < 1) ? 1 : page;
        pageSize = (pageSize == null || pageSize < 1) ? 20 : Math.min(pageSize, 100);

        // 构建查询条件
        LambdaQueryWrapper<AgentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentEntity::getUserId, userId)
                .isNull(AgentEntity::getDeletedAt);

        // 类型筛选
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(AgentEntity::getAgentType, convertTypeToInteger(type));
        }

        // 状态筛选（enabled字段）
        if (StrUtil.isNotBlank(status)) {
            if ("ACTIVE".equals(status)) {
                wrapper.eq(AgentEntity::getEnabled, true);
            } else if ("ARCHIVED".equals(status)) {
                wrapper.eq(AgentEntity::getEnabled, false);
            }
        }

        // 关键词搜索（名称/描述）
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(AgentEntity::getName, keyword)
                    .or()
                    .like(AgentEntity::getDescription, keyword));
        }

        // 是否公开筛选
        // 注意：当前数据库中没有isPublic字段，这里先跳过

        wrapper.orderByDesc(AgentEntity::getUpdatedAt);

        // 分页查询
        Page<AgentEntity> pageParam = new Page<>(page, pageSize);
        IPage<AgentEntity> pageResult = agentMapper.selectPage(pageParam, wrapper);

        // 转换为VO列表
        List<AgentListItemVO> agentList = pageResult.getRecords().stream()
                .map(this::convertToListItemVO)
                .collect(Collectors.toList());

        // 构建返回结果
        AgentListVO result = new AgentListVO();
        result.setTotal(pageResult.getTotal());
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotalPages((int) Math.ceil((double) pageResult.getTotal() / pageSize));
        result.setAgents(agentList);

        logger.info("Agent列表查询成功，总数: {}, 当前页: {}", pageResult.getTotal(), page);
        return result;
    }

    @Override
    public AgentDetailVO getAgentById(String agentId, String userId) {
        logger.info("获取Agent详情，Agent ID: {}, 用户ID: {}", agentId, userId);

        AgentEntity entity = agentMapper.selectById(agentId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            logger.error("Agent不存在或无权访问，Agent ID: {}, 用户ID: {}", agentId, userId);
            throw new RuntimeException("Agent不存在或无权访问");
        }

        if (entity.getDeletedAt() != null) {
            throw new RuntimeException("Agent已被删除");
        }

        // 转换为详情VO
        AgentDetailVO vo = convertToDetailVO(entity);

        // 设置统计信息（TODO: 实际应从统计表查询）
        AgentStatisticsVO statistics = new AgentStatisticsVO();
        statistics.setUsageCount(0);
        statistics.setSessionCount(0);
        statistics.setAvgResponseTime(0);
        statistics.setSuccessRate(1.0);
        vo.setStatistics(statistics);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgentVO updateAgent(String agentId, UpdateAgentRequest request, String userId) {
        logger.info("更新Agent，Agent ID: {}, 用户ID: {}", agentId, userId);

        AgentEntity entity = agentMapper.selectById(agentId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            logger.error("Agent不存在或无权访问，Agent ID: {}, 用户ID: {}", agentId, userId);
            throw new RuntimeException("Agent不存在或无权访问");
        }

        if (entity.getDeletedAt() != null) {
            throw new RuntimeException("Agent已被删除");
        }

        // 更新字段
        boolean needUpdate = false;
        if (StrUtil.isNotBlank(request.getName())) {
            // 检查名称是否与其他Agent重复
            LambdaQueryWrapper<AgentEntity> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(AgentEntity::getName, request.getName())
                    .eq(AgentEntity::getUserId, userId)
                    .ne(AgentEntity::getId, agentId)
                    .isNull(AgentEntity::getDeletedAt);
            if (agentMapper.selectCount(checkWrapper) > 0) {
                throw new RuntimeException("Agent名称已存在");
            }
            entity.setName(request.getName());
            needUpdate = true;
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
            needUpdate = true;
        }
        if (request.getAvatar() != null) {
            entity.setAvatar(request.getAvatar());
            needUpdate = true;
        }
        if (StrUtil.isNotBlank(request.getSystemPrompt())) {
            entity.setSystemPrompt(request.getSystemPrompt());
            needUpdate = true;
        }

        // 更新模型配置
        Map<String, Object> modelConfig = entity.getModelConfig();
        if (modelConfig == null) {
            modelConfig = new HashMap<>();
        }
        if (StrUtil.isNotBlank(request.getModel())) {
            modelConfig.put("model", request.getModel());
            needUpdate = true;
        }
        if (request.getTemperature() != null) {
            modelConfig.put("temperature", request.getTemperature());
            needUpdate = true;
        }
        if (request.getMaxTokens() != null) {
            modelConfig.put("maxTokens", request.getMaxTokens());
            needUpdate = true;
        }
        if (needUpdate) {
            entity.setModelConfig(modelConfig);
        }

        // 更新工具列表
        if (request.getTools() != null) {
            List<Map<String, Object>> toolsList = request.getTools().stream()
                    .map(toolName -> {
                        Map<String, Object> tool = new HashMap<>();
                        tool.put("name", toolName);
                        tool.put("enabled", true);
                        return tool;
                    })
                    .collect(Collectors.toList());
            entity.setTools(toolsList);
            needUpdate = true;
        }

        // 更新知识库ID列表
        if (request.getKnowledgeBaseIds() != null) {
            entity.setKnowledgeBaseIds(request.getKnowledgeBaseIds());
            needUpdate = true;
        }

        if (needUpdate) {
            entity.setUpdatedAt(LocalDateTime.now());
            agentMapper.updateById(entity);
            logger.info("Agent更新成功，Agent ID: {}", agentId);
        }

        return agentConverter.toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAgent(String agentId, String userId) {
        logger.info("删除Agent，Agent ID: {}, 用户ID: {}", agentId, userId);

        AgentEntity entity = agentMapper.selectById(agentId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            logger.error("Agent不存在或无权访问，Agent ID: {}, 用户ID: {}", agentId, userId);
            throw new RuntimeException("Agent不存在或无权访问");
        }

        // 软删除
        entity.setDeletedAt(LocalDateTime.now());
        agentMapper.updateById(entity);

        logger.info("Agent删除成功，Agent ID: {}", agentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgentVO toggleAgentStatus(String agentId, String userId) {
        logger.info("切换Agent状态，Agent ID: {}, 用户ID: {}", agentId, userId);

        AgentEntity entity = agentMapper.selectById(agentId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            logger.error("Agent不存在或无权访问，Agent ID: {}, 用户ID: {}", agentId, userId);
            throw new RuntimeException("Agent不存在或无权访问");
        }

        if (entity.getDeletedAt() != null) {
            throw new RuntimeException("Agent已被删除");
        }

        // 切换enabled状态
        boolean newEnabled = !(entity.getEnabled() != null && entity.getEnabled());
        entity.setEnabled(newEnabled);
        entity.setUpdatedAt(LocalDateTime.now());
        agentMapper.updateById(entity);

        logger.info("Agent状态切换成功，Agent ID: {}, 新状态: {}", agentId, newEnabled);

        // 转换为VO并返回
        return agentConverter.toVO(entity);
    }

    /**
     * 转换类型字符串为整数
     */
    private Integer convertTypeToInteger(String type) {
        if (StrUtil.isBlank(type)) {
            return 1; // 默认CHAT
        }
        switch (type.toUpperCase()) {
            case "CHAT":
                return 1;
            case "TASK":
                return 2;
            case "TOOL":
                return 2; // TOOL也映射为功能性Agent
            default:
                return 1;
        }
    }

    /**
     * 转换整数为类型字符串
     */
    private String convertIntegerToType(Integer agentType) {
        if (agentType == null) {
            return "CHAT";
        }
        switch (agentType) {
            case 1:
                return "CHAT";
            case 2:
                return "TASK";
            default:
                return "CHAT";
        }
    }

    /**
     * 转换为列表项VO
     */
    private AgentListItemVO convertToListItemVO(AgentEntity entity) {
        AgentListItemVO vo = new AgentListItemVO();
        vo.setAgentId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setAvatar(entity.getAvatar());
        vo.setType(convertIntegerToType(entity.getAgentType()));
        vo.setStatus(entity.getEnabled() ? "ACTIVE" : "ARCHIVED");
        vo.setIsPublic(false); // TODO: 从数据库字段获取
        vo.setVersion("1.0.0"); // TODO: 从版本表获取
        vo.setUsageCount(0); // TODO: 从统计表获取
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    /**
     * 转换为详情VO
     */
    private AgentDetailVO convertToDetailVO(AgentEntity entity) {
        AgentDetailVO vo = new AgentDetailVO();
        vo.setAgentId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setAvatar(entity.getAvatar());
        vo.setType(convertIntegerToType(entity.getAgentType()));
        vo.setSystemPrompt(entity.getSystemPrompt());
        vo.setStatus(entity.getEnabled() ? "ACTIVE" : "ARCHIVED");
        vo.setIsPublic(false); // TODO: 从数据库字段获取
        vo.setVersion("1.0.0"); // TODO: 从版本表获取
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());

        // 设置模型配置
        Map<String, Object> modelConfig = entity.getModelConfig();
        if (modelConfig != null) {
            vo.setModel((String) modelConfig.get("model"));
            vo.setTemperature(modelConfig.get("temperature") != null ? 
                    ((Number) modelConfig.get("temperature")).doubleValue() : null);
            vo.setMaxTokens(modelConfig.get("maxTokens") != null ? 
                    ((Number) modelConfig.get("maxTokens")).intValue() : null);
        }

        // 设置工具列表
        if (entity.getTools() != null && !entity.getTools().isEmpty()) {
            List<AgentDetailVO.ToolVO> toolVOs = new ArrayList<>();
            for (Map<String, Object> tool : entity.getTools()) {
                AgentDetailVO.ToolVO toolVO = new AgentDetailVO.ToolVO();
                toolVO.setName((String) tool.get("name"));
                toolVO.setDisplayName((String) tool.getOrDefault("displayName", tool.get("name")));
                toolVO.setEnabled(tool.get("enabled") != null ? 
                        (Boolean) tool.get("enabled") : true);
                toolVOs.add(toolVO);
            }
            vo.setTools(toolVOs);
        }

        // 设置知识库ID列表
        vo.setKnowledgeBaseIds(entity.getKnowledgeBaseIds());

        return vo;
    }
}
