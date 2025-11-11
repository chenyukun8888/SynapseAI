# Token策略切换指南

本文档介绍如何在SynapseAI中切换和使用不同的Token溢出处理策略。

## 🎯 策略切换方式

### 1. 手动配置切换

```java
// 创建配置对象
TokenOverflowStrategy.TokenOverflowConfig config = new TokenOverflowStrategy.TokenOverflowConfig();

// 设置策略类型
config.setStrategyType("SLIDING_WINDOW"); // 或 "SUMMARY", "TRUNCATION"

// 设置策略参数
config.setMaxTokens(4000);
config.setReserveRatio(0.1); // 滑动窗口专用
config.setSummaryThreshold(20); // 摘要策略专用

// 执行策略
TokenOverflowStrategy strategy = TokenOverflowStrategyFactory.createStrategy(config);
TokenOverflowStrategy.TokenOverflowResult result = strategy.process(messages, config);
```

### 2. 枚举方式切换

```java
import cn.chenyukun.synapse.module.llm.enums.OverflowStrategy;

// 使用枚举创建策略
TokenOverflowStrategy strategy = TokenOverflowStrategyFactory.createStrategy(
    OverflowStrategy.SLIDING_WINDOW, config);
```

### 3. 字符串方式切换

```java
// 使用字符串创建策略
TokenOverflowStrategy strategy = TokenOverflowStrategyFactory.createStrategy(
    "SLIDING_WINDOW", config);
```

### 4. 运行时动态切换

```java
// 使用策略切换器
TokenStrategySwitcher switcher = new TokenStrategySwitcher(strategyFactory);

// 动态切换策略
TokenOverflowStrategy.TokenOverflowResult result = switcher.executeStrategy(
    "SUMMARY", messages, config);
```

## 🧠 智能策略选择

### 自动策略选择

```java
TokenStrategySwitcher switcher = new TokenStrategySwitcher(strategyFactory);

// 智能选择最合适的策略
TokenOverflowStrategy.TokenOverflowResult result = switcher.smartSwitch(messages, 4000);

// 系统会根据：
// - 消息数量（>30条 → 摘要策略）
// - Token使用率（>80% → 滑动窗口）
// - 其他情况 → 截断策略
```

### 策略切换建议

```java
// 获取切换建议
OverflowStrategy suggestedStrategy = switcher.suggestStrategySwitch(
    messages, OverflowStrategy.TRUNCATION, 4000);

if (suggestedStrategy != null) {
    System.out.println("建议切换到：" + suggestedStrategy);
    // 执行切换
    config.setStrategyType(suggestedStrategy.name());
}
```

## 📊 策略对比

### 对比不同策略效果

```java
TokenStrategySwitcher.StrategyComparison comparison =
    strategySwitcher.compareStrategies(messages, config);

// 查看各策略结果
System.out.println("截断策略Token数：" + comparison.getTruncationResult().getTotalTokens());
System.out.println("滑动窗口Token数：" + comparison.getSlidingWindowResult().getTotalTokens());
System.out.println("摘要策略Token数：" + comparison.getSummaryResult().getTotalTokens());

// 获取最佳策略推荐
OverflowStrategy recommended = comparison.getRecommendedStrategy();
```

## ⚙️ 配置参数说明

### 通用参数
- `strategyType`: 策略类型 ("TRUNCATION", "SLIDING_WINDOW", "SUMMARY")
- `maxTokens`: 最大Token数限制

### 滑动窗口策略参数
- `reserveRatio`: 预留缓冲比例 (0.1 = 10%缓冲空间)

### 摘要策略参数
- `summaryThreshold`: 摘要触发阈值 (消息数量)

## 🔄 策略生命周期

### 1. 初始化阶段
```java
// 在应用启动或会话开始时选择策略
TokenOverflowStrategy strategy = TokenOverflowStrategyFactory.createStrategy("SLIDING_WINDOW", config);
```

### 2. 执行阶段
```java
// 在每次发送消息前检查和处理
if (strategy.needsProcessing(messages)) {
    TokenOverflowStrategy.TokenOverflowResult result = strategy.process(messages, config);
    messages = result.getRetainedMessages();
}
```

### 3. 动态切换阶段
```java
// 根据运行时条件切换策略
if (需要切换条件) {
    strategy = TokenOverflowStrategyFactory.createStrategy("SUMMARY", newConfig);
}
```

## 🎛️ 高级用法

### 策略缓存
```java
// 可以缓存策略实例避免重复创建
private Map<String, TokenOverflowStrategy> strategyCache = new ConcurrentHashMap<>();

public TokenOverflowStrategy getCachedStrategy(String strategyType, TokenOverflowConfig config) {
    String key = strategyType + "_" + config.hashCode();
    return strategyCache.computeIfAbsent(key,
        k -> TokenOverflowStrategyFactory.createStrategy(strategyType, config));
}
```

### 策略链
```java
// 实现策略链：先滑动窗口，再摘要
public TokenOverflowResult processWithChain(List<TokenUsageDTO> messages, TokenOverflowConfig config) {
    // 第一步：滑动窗口
    SlidingWindowStrategy sliding = new SlidingWindowStrategy(config);
    TokenOverflowResult step1 = sliding.process(messages, config);

    // 第二步：如果还需要，执行摘要
    if (step1.getRetainedMessages().size() > config.getSummaryThreshold()) {
        SummaryStrategy summary = new SummaryStrategy(config);
        return summary.process(step1.getRetainedMessages(), config);
    }

    return step1;
}
```

## 🚀 最佳实践

1. **根据场景选择策略**：
   - 短对话：截断策略
   - 长对话但不需历史：滑动窗口
   - 需要历史摘要：摘要策略

2. **设置合理的参数**：
   - `maxTokens`: 根据模型限制设置 (GPT-4: 8192, GPT-3.5: 4096)
   - `reserveRatio`: 0.05-0.2 之间
   - `summaryThreshold`: 15-30 之间

3. **监控策略效果**：
   - 记录每次策略执行的Token节省量
   - 监控策略切换频率
   - 根据效果调整参数

4. **性能优化**：
   - 缓存策略实例
   - 异步执行摘要生成
   - 批量处理消息

## 📝 示例代码

查看 `TokenStrategyExamples.java` 获取完整的使用示例，包括：
- 手动策略切换
- 运行时动态切换
- 智能策略选择
- 策略对比分析
- 配置驱动的切换
