package com.coding.controller;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-05-06
 */
import com.coding.entity.ChannelTransaction;
import com.coding.entity.LocalTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransactionGenerator {
    private static final Random random = new Random();

    /**
     * 生成测试数据集合
     * @param size 总数据量（建议 >= 100）
     * @return Pair<本地交易列表, 渠道交易列表>
     */
    public static Pair<List<LocalTransaction>, List<ChannelTransaction>> generateTransactions(int size,int startId) {
        List<LocalTransaction> localList = new ArrayList<>();
        List<ChannelTransaction> channelList = new ArrayList<>();

        // 计算匹配数量（90%）
        int matchCount = (int) (size * 0.9);
        // 剩余10%中：50%订单号相同金额不同，50%渠道独有订单
        int mismatchType1 = (size - matchCount) / 2; // 类型1：金额不同
        int mismatchType2 = size - matchCount - mismatchType1; // 类型2：渠道独有

        // 生成匹配数据（90%）
        for (int i = startId; i < matchCount+startId; i++) {
            String orderId = "LOCAL_" + i;
            BigDecimal amount = randomAmount();
            localList.add(new LocalTransaction(orderId, amount, "SUCCESS"));
            // 渠道订单号格式可能不同（此处模拟差异）
            channelList.add(new ChannelTransaction("CHANNEL_" + i, orderId, amount, "SUCCESS"));
        }

        // 生成不匹配数据：类型1（金额不同）
        for (int i = startId; i < mismatchType1+startId; i++) {
            // 从已存在的订单中随机选一个
            int index = random.nextInt(matchCount);
            LocalTransaction local = localList.get(index);
            // 修改金额（±10%浮动）
            BigDecimal delta = local.getAmount().multiply(BigDecimal.valueOf(0.1 * (random.nextBoolean() ? 1 : -1)));
            BigDecimal channelAmount = local.getAmount().add(delta).setScale(2, RoundingMode.HALF_UP);
            channelList.add(new ChannelTransaction("CHANNEL_MIS_" + i, local.getOrderId(), channelAmount, "SUCCESS"));
        }

        // 生成不匹配数据：类型2（渠道独有订单）
        for (int i = startId; i < mismatchType2+startId; i++) {
            String orderId = "CHANNEL_ONLY_" + i;
            channelList.add(new ChannelTransaction("CHANNEL_" + (matchCount + i), orderId,
                    randomAmount(), "SUCCESS"));
        }

        return new Pair<>(localList, channelList);
    }

    // 生成随机金额（0.01 ~ 1000.00）
    private static BigDecimal randomAmount() {
        return BigDecimal.valueOf(random.nextDouble() * 1000 + 0.01)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // 简单Pair类用于返回两个列表
    public static class Pair<A, B> {
        public final A first;
        public final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
}