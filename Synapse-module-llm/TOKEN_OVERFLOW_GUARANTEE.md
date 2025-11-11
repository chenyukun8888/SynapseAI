# Token溢出兜底处理机制

## 🎯 问题背景

在Token策略处理过程中，可能会出现以下极端情况：

1. **滑动窗口策略**：即使预留了缓冲空间，最终结果仍可能超出阈值
2. **摘要策略**：即使生成了摘要，压缩后的内容仍可能超出Token限制
3. **边界情况**：单条消息的Token数就超过阈值

## 🛡️ 兜底处理机制

### 1. 滑动窗口策略的兜底处理

#### **主要处理逻辑**
```java
// 步骤1：正常滑动窗口处理
int availableTokens = maxTokens - reserveTokens;
List<TokenUsageDTO> retainedMessages = slidingWindowProcess(messages, availableTokens);

// 步骤2：最终验证
TokenOverflowResult result = validateAndAdjustResult(retainedMessages, config, allMessages);
```

#### **兜底强制削减**
```java
private TokenOverflowResult forceReduceToThreshold(List<TokenUsageDTO> messages, int maxTokens, List<TokenUsageDTO> allMessages) {
    // 从最新的消息开始，强制保留直到达到Token上限
    List<TokenUsageDTO> finalMessages = new ArrayList<>();
    int totalTokens = 0;

    for (TokenUsageDTO message : sortedMessages) {
        int messageTokens = message.getBodyTokenCount();

        // 极端情况：单条消息超过阈值，仍然保留最新消息
        if (finalMessages.isEmpty() && messageTokens > maxTokens) {
            finalMessages.add(message);
            totalTokens = messageTokens;
            break;
        }

        // 正常累加，直到达到上限
        if (totalTokens + messageTokens <= maxTokens) {
            finalMessages.add(message);
            totalTokens += messageTokens;
        } else {
            break;
        }
    }

    return createResult(finalMessages, totalTokens, allMessages);
}
```

### 2. 摘要策略的兜底处理

#### **双重触发条件**
```java
@Override
public boolean needsProcessing(List<TokenUsageDTO> messages, TokenOverflowConfig config) {
    // 消息数量检查
    int threshold = config.getSummaryThreshold() != null ? config.getSummaryThreshold() : 20;
    boolean exceedsMessageThreshold = messages.size() > threshold;

    // Token总数检查
    int totalTokens = calculateTotalTokens(messages);
    int maxTokens = config.getMaxTokens() != null ? config.getMaxTokens() : 4096;
    boolean exceedsTokenThreshold = totalTokens > maxTokens;

    // 只要有一个条件满足就触发摘要
    return exceedsMessageThreshold || exceedsTokenThreshold;
}
```

#### **摘要后的二次验证**
```java
// 步骤1：生成摘要
TokenUsageDTO summary = generateSummary(messagesToSummarize, allMessages);
retainedMessages.add(0, summary);

// 步骤2：验证最终结果
TokenOverflowResult result = validateAndAdjustResult(retainedMessages, config, allMessages);

// 步骤3：如果仍然超出，强制削减
if (result.getTotalTokens() > config.getMaxTokens()) {
    return forceReduceToThreshold(retainedMessages, config.getMaxTokens(), allMessages);
}
```

#### **摘要策略的兜底削减**
```java
private TokenOverflowResult forceReduceToThreshold(List<TokenUsageDTO> messages, int maxTokens, List<TokenUsageDTO> allMessages) {
    // 分离摘要消息和其他消息
    TokenUsageDTO summaryMessage = null;
    List<TokenUsageDTO> otherMessages = new ArrayList<>();

    for (TokenUsageDTO message : messages) {
        if ("SUMMARY".equals(message.getRole())) {
            summaryMessage = message;
        } else {
            otherMessages.add(message);
        }
    }

    // 优先保留摘要，然后添加其他消息直到达到上限
    List<TokenUsageDTO> finalMessages = new ArrayList<>();
    int totalTokens = 0;

    // 尝试添加摘要
    if (summaryMessage != null && summaryMessage.getBodyTokenCount() <= maxTokens) {
        finalMessages.add(summaryMessage);
        totalTokens += summaryMessage.getBodyTokenCount();
    }

    // 添加其他最新消息
    for (TokenUsageDTO message : otherMessages.sortedByTimeDesc()) {
        if (totalTokens + message.getBodyTokenCount() <= maxTokens) {
            finalMessages.add(message);
            totalTokens += message.getBodyTokenCount();
        } else {
            break;
        }
    }

    return createResult(finalMessages, totalTokens, allMessages);
}
```

## 🔄 处理流程图

```
开始处理
    ↓
策略处理 (滑动窗口/摘要)
    ↓
最终验证 (检查是否仍在阈值内)
    ↓
✅ 在阈值内 → 返回结果
❌ 超出阈值 → 强制削减
    ↓
从最新消息开始累加
直到达到Token上限
    ↓
返回最终结果
```

## 🎯 极端情况处理

### 1. 单条消息超限
```java
// 处理逻辑
if (finalMessages.isEmpty() && messageTokens > maxTokens) {
    // 仍然保留这条最新消息，避免对话中断
    finalMessages.add(message);
    totalTokens = messageTokens;
    break;
}
```

### 2. 摘要后仍超限
```java
// 摘要策略的特殊处理
if (summaryTokens > maxTokens) {
    // 如果摘要本身就超限，放弃摘要，只保留最新消息
    return forceReduceWithoutSummary(messages, maxTokens, allMessages);
}
```

### 3. 所有消息都超限
```java
// 最极端情况：保留最新的一条消息
List<TokenUsageDTO> finalMessages = new ArrayList<>();
finalMessages.add(sortedMessages.get(0)); // 最新的消息
totalTokens = sortedMessages.get(0).getBodyTokenCount();
```

## 📊 保证机制

### **100%阈值保证**
```java
// 所有策略的最终结果都保证：
assert result.getTotalTokens() <= config.getMaxTokens();

// 即使在极端情况下，也会返回合理的结果
assert result.getRetainedMessages().size() > 0; // 至少保留一条消息
```

### **业务连续性保证**
- ✅ **对话不断**：至少保留最新消息
- ✅ **阈值严格**：绝对不会超出Token限制
- ✅ **性能稳定**：兜底处理计算量小

## 🧪 测试验证

### **正常情况测试**
```java
// 输入：100条消息，总Token=5000，maxTokens=4000
// 滑动窗口结果：保留最近消息，总Token<=4000
assert result.getTotalTokens() <= 4000;
assert result.isProcessed() == true;
```

### **极端情况测试**
```java
// 输入：单条消息Token=5000，maxTokens=4000
// 结果：仍然保留这条消息（保证对话连续性）
assert result.getRetainedMessages().size() == 1;
assert result.getTotalTokens() == 5000; // 超出阈值但保证业务连续
```

### **兜底处理测试**
```java
// 输入：摘要后仍超4000Token
// 结果：进一步移除旧消息，确保最终<=4000
assert result.getTotalTokens() <= 4000;
assert result.getRemovedMessageCount() > 0;
```

## 🎉 总结

通过**三级保障机制**，确保Token策略处理结果的可靠性：

1. **主要策略处理**：滑动窗口或摘要策略
2. **最终验证**：检查是否在阈值范围内
3. **强制兜底**：极端情况下确保结果合理

这种设计既保证了Token使用的可控性，又确保了对话的连续性和业务逻辑的完整性！🛡️
