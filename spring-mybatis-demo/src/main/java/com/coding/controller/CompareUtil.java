package com.coding.controller;

import com.coding.entity.ChannelTransaction;
import com.coding.entity.LocalTransaction;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-05-06
 */
@Slf4j
public class CompareUtil {

    @Data
    @ToString
    public class FailRecord{
        private String Id;
        private String type;
        private String reason;

    }
    private static List<FailRecord> failRecordList = new CopyOnWriteArrayList<>();
    private void handleMismatch(FailRecord record) {
        failRecordList.add(record);
    }

    //双指针遍历
    public void sortMergeReconcile(List<LocalTransaction> localList, List<ChannelTransaction> channelList) {
        log.info("开始对账 sortMergeReconcile");
        long s = System.currentTimeMillis();
        // 确保数据已按订单号排序（假设已排序）
        int i = 0, j = 0;
        while (i < localList.size() && j < channelList.size()) {
            LocalTransaction local = localList.get(i);
            ChannelTransaction channel = channelList.get(j);
            int compare = local.getOrderId().compareTo(channel.getOrderId());

            if (compare == 0) {
                // 订单号匹配，比较金额和状态
                if (!(local.getAmount().compareTo(channel.getAmount())==0)) {
//                    handleAmountMismatch(local.getOrderId(), local.getAmount(), channel.getAmount());
                    FailRecord failRecord = new FailRecord();
                    failRecord.setId(local.getOrderId());
                    failRecord.setType("1");
                    failRecord.setReason("金额不一致");
                    handleMismatch(failRecord);
                }
//                if (!statusMapping(local.getStatus()).equals(channel.getChannelStatus())) {
//                    handleStatusMismatch(local.getOrderId(), local.getStatus(), channel.getChannelStatus());
//                }
                i++;
                j++;
            } else if (compare < 0) {
                // 本地存在但渠道缺失
                FailRecord failRecord = new FailRecord();
                failRecord.setId(local.getOrderId());
                failRecord.setType("2");
                failRecord.setReason("本地存在但渠道缺失");
                handleMismatch(failRecord);
                i++;
            } else {
                // 渠道存在但本地缺失
                FailRecord failRecord = new FailRecord();
                failRecord.setId(channel.getChannelOrderId());
                failRecord.setType("3");
                failRecord.setReason("渠道存在但本地缺失");
                handleMismatch(failRecord);
                j++;
            }
        }

        // 处理剩余未匹配的本地订单
        while (i < localList.size()) {
            FailRecord failRecord = new FailRecord();
            failRecord.setId(localList.get(i).getOrderId());
            failRecord.setType("2");
            failRecord.setReason("本地存在但渠道缺失");
            handleMismatch(failRecord);
            i++;
        }

        // 处理剩余未匹配的渠道订单
        while (j < channelList.size()) {
            FailRecord failRecord = new FailRecord();
            failRecord.setId(channelList.get(i).getChannelOrderId());
            failRecord.setType("3");
            failRecord.setReason("渠道存在但本地缺失");
            handleMismatch(failRecord);
            j++;
        }
        long e = System.currentTimeMillis();
        log.info("对账结束 sortMergeReconcile 耗时：{}ms", e - s);
    }

    //将数据按订单号哈希分片，每个分片独立比对，利用多线程并行加速
    // 分片数量（根据CPU核心数调整）
    private static final int SHARD_COUNT = 4;

//    public void shardedReconcile(List<LocalTransaction> localList, List<ChannelTransaction> channelList) {
//        log.info("开始对账 shardedReconcile");
//        long s = System.currentTimeMillis();
//
//        // 分片存储本地和渠道数据（使用ConcurrentHashMap提升并发性能）
//        Map<Integer, List<LocalTransaction>> localShards = new ConcurrentHashMap<>();
//        Map<Integer, List<ChannelTransaction>> channelShards = new ConcurrentHashMap<>();
//
//        // 分片填充数据（改为串行处理避免parallelStream的线程安全问题）
//        for (LocalTransaction tx : localList) {
//            int shardId = Math.abs(Objects.hashCode(tx.getOrderId()) % SHARD_COUNT);
//            localShards.computeIfAbsent(shardId, k -> new ArrayList<>()).add(tx);
//        }
//
//        for (ChannelTransaction tx : channelList) {
//            int shardId = Math.abs(Objects.hashCode(tx.getOrderId()) % SHARD_COUNT);
//            channelShards.computeIfAbsent(shardId, k -> new ArrayList<>()).add(tx);
//        }
//
//        // 使用ForkJoinPool提升并行处理性能
//        ForkJoinPool forkJoinPool = new ForkJoinPool(SHARD_COUNT);
//        try {
//            forkJoinPool.submit(() -> {
//                // 并行处理每个分片
//                localShards.keySet().parallelStream().forEach(currentShard -> {
//                    List<LocalTransaction> locals = localShards.getOrDefault(currentShard, Collections.emptyList());
//                    List<ChannelTransaction> channels = channelShards.getOrDefault(currentShard, Collections.emptyList());
//
//                    // 为每个分片创建独立的CompareUtil实例，避免共享failRecordList
//                    CompareUtil compareUtil = new CompareUtil();
//                    compareUtil.sortMergeReconcile(locals, channels);
//
//                    // 合并结果（可选）
//                    // 注意：如果不需要合并结果，可以完全隔离状态，提高并发性能
//                });
//            }).get();
//        } catch (Exception e) {
//            Thread.currentThread().interrupt();
//        } finally {
//            forkJoinPool.shutdown();
//        }
//
//        long e = System.currentTimeMillis();
//        log.info("对账结束 shardedReconcile 耗时：{}ms", e - s);
//    }
    public static void main(String[] args) {
        // 生成100条测试数据
        TransactionGenerator.Pair<List<LocalTransaction>, List<ChannelTransaction>> data =
                TransactionGenerator.generateTransactions(100000,0);

        List<LocalTransaction> localList = data.first;
        List<ChannelTransaction> channelList = data.second;


        CompareUtil compareUtil = new CompareUtil();
        compareUtil.sortMergeReconcile(localList, channelList);//耗时：28ms  100000
        log.info("对账结果大小：{}", failRecordList.size());
        log.info("-------------------------");
        failRecordList.clear();
        log.info(String.valueOf(failRecordList.size()));
//        compareUtil.shardedReconcile(localList, channelList);
        log.info("对账结果大小：{}", failRecordList.size());


//        failRecordList.forEach(record -> {
//            log.info("对账结果：{}", record);
//        });

        ///
//        failRecordList.clear();

    }
}
