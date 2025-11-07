package cn.chenyukun.synapse.infrastructure.external.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE 连接管理器
 * 用于管理 Server-Sent Events 连接的生命周期
 */
@Component
public class SseEmitterManager {

    private final Logger logger = LoggerFactory.getLogger(SseEmitterManager.class);

    /**
     * 存储所有活跃的 SSE 连接
     * Key: 连接ID, Value: SseEmitter
     */
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 默认超时时间：5分钟
     */
    private static final long DEFAULT_TIMEOUT = 5 * 60 * 1000L;

    /**
     * 创建新的 SSE 连接
     *
     * @param connectionId 连接唯一标识
     * @return SseEmitter
     */
    public SseEmitter createEmitter(String connectionId) {
        return createEmitter(connectionId, DEFAULT_TIMEOUT);
    }

    /**
     * 创建新的 SSE 连接（指定超时时间）
     *
     * @param connectionId 连接唯一标识
     * @param timeout      超时时间（毫秒）
     * @return SseEmitter
     */
    public SseEmitter createEmitter(String connectionId, long timeout) {
        SseEmitter emitter = new SseEmitter(timeout);

        // 完成时移除
        emitter.onCompletion(() -> {
            logger.info("SSE 连接完成: {}", connectionId);
            emitters.remove(connectionId);
        });

        // 超时时移除
        emitter.onTimeout(() -> {
            logger.warn("SSE 连接超时: {}", connectionId);
            emitters.remove(connectionId);
        });

        // 错误时移除
        emitter.onError((e) -> {
            logger.error("SSE 连接错误: {}, 原因: {}", connectionId, e.getMessage());
            emitters.remove(connectionId);
        });

        emitters.put(connectionId, emitter);
        logger.info("创建 SSE 连接: {}, 当前连接数: {}", connectionId, emitters.size());

        return emitter;
    }

    /**
     * 发送事件
     *
     * @param connectionId 连接ID
     * @param eventName    事件名称
     * @param data         数据
     * @return 是否发送成功
     */
    public boolean sendEvent(String connectionId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(connectionId);
        if (emitter == null) {
            logger.warn("SSE 连接不存在: {}", connectionId);
            return false;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
            return true;
        } catch (IOException e) {
            logger.error("发送 SSE 事件失败: {}, 事件: {}, 原因: {}",
                    connectionId, eventName, e.getMessage());
            removeEmitter(connectionId);
            return false;
        }
    }

    /**
     * 发送数据（默认事件名为 "message"）
     *
     * @param connectionId 连接ID
     * @param data         数据
     * @return 是否发送成功
     */
    public boolean sendData(String connectionId, Object data) {
        return sendEvent(connectionId, "message", data);
    }

    /**
     * 完成并关闭连接
     *
     * @param connectionId 连接ID
     */
    public void complete(String connectionId) {
        SseEmitter emitter = emitters.get(connectionId);
        if (emitter != null) {
            try {
                emitter.complete();
                logger.info("SSE 连接正常关闭: {}", connectionId);
            } catch (Exception e) {
                logger.error("关闭 SSE 连接失败: {}, 原因: {}", connectionId, e.getMessage());
            } finally {
                emitters.remove(connectionId);
            }
        }
    }

    /**
     * 完成并发送关闭事件
     *
     * @param connectionId 连接ID
     * @param closeMessage 关闭消息
     */
    public void completeWithMessage(String connectionId, String closeMessage) {
        sendEvent(connectionId, "close", closeMessage);
        complete(connectionId);
    }

    /**
     * 移除连接（不发送完成信号）
     *
     * @param connectionId 连接ID
     */
    public void removeEmitter(String connectionId) {
        SseEmitter emitter = emitters.remove(connectionId);
        if (emitter != null) {
            logger.info("移除 SSE 连接: {}", connectionId);
        }
    }

    /**
     * 检查连接是否存在
     *
     * @param connectionId 连接ID
     * @return 是否存在
     */
    public boolean exists(String connectionId) {
        return emitters.containsKey(connectionId);
    }

    /**
     * 获取当前活跃连接数
     *
     * @return 连接数
     */
    public int getActiveConnectionCount() {
        return emitters.size();
    }

    /**
     * 发送心跳（保持连接活跃）
     *
     * @param connectionId 连接ID
     * @return 是否发送成功
     */
    public boolean sendHeartbeat(String connectionId) {
        SseEmitter emitter = emitters.get(connectionId);
        if (emitter == null) {
            return false;
        }

        try {
            emitter.send(SseEmitter.event().comment("heartbeat"));
            return true;
        } catch (IOException e) {
            logger.error("发送心跳失败: {}, 原因: {}", connectionId, e.getMessage());
            removeEmitter(connectionId);
            return false;
        }
    }
}

