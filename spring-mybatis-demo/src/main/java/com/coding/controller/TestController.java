package com.coding.controller;

import com.coding.entity.ChannelTransaction;
import com.coding.entity.LocalTransaction;
import com.coding.mapper.ChannelTransactionMapper;
import com.coding.mapper.LocalTransactionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-05-08
 */
@RestController
@Slf4j
public class TestController {

    @Autowired
    private ChannelTransactionMapper channelTransactionMapper;
    @Autowired
    private LocalTransactionMapper  localTransactionMapper;

    @GetMapping("/generate")
    public  void generate() {
        TransactionGenerator.Pair<List<LocalTransaction>, List<ChannelTransaction>> pair =
                TransactionGenerator.generateTransactions(100000,0);
        List<LocalTransaction> localList = pair.first;
        List<ChannelTransaction> channelList = pair.second;
        localTransactionMapper.insertBatch(localList);
        channelTransactionMapper.insertBatch(channelList);
    }


    @GetMapping("/compare")
    public void compare(@RequestParam("size") int size) {
        reconcile("2025-05-08", "2025-05-08",size);
    }

//    private static final int PAGE_SIZE = 5000; // 根据内存调整

    public void reconcile(String startTime, String endTime, int PAGE_SIZE) {
        long s1 = System.currentTimeMillis();
        log.info("reconcile 开始时间：{} ", s1);
        int pageNum = 0;
        while (true) {
            long s = System.currentTimeMillis();
            log.info("处理分页：{} 开始时间：{}", pageNum,s );
            // 分页查询本地数据
            List<LocalTransaction> localList = localTransactionMapper.selectByTimeRange(
                    startTime, endTime, pageNum * PAGE_SIZE, PAGE_SIZE
            );

            if (localList.isEmpty()) break;

            // 提取订单号批量查询渠道数据  ch中的数据现在肯定是lo的子集，遗漏了ch中有lo中没有的记录
            List<String> orderIds = localList.stream()
                    .map(LocalTransaction::getOrderId)
                    .collect(Collectors.toList());
            List<ChannelTransaction> channelList = channelTransactionMapper.selectByOrderIds(orderIds);

            log.info("localList size：{} channelList size：{}", localList.size(),channelList.size());
            // 排序后比对
            sortMergeReconcile(localList, channelList);


            long e = System.currentTimeMillis();
            log.info("处理分页：{} 结束时间：{} 耗时：{}", pageNum,e ,e-s);
            pageNum++;
        }
        long e1 = System.currentTimeMillis();
        log.info("reconcile 结束时间：{} 耗时：{}", e1,  e1-s1);
    }
    private void sortMergeReconcile(
            List<LocalTransaction> localList,
            List<ChannelTransaction> channelList
    ) {
        // 确保数据已按order_id排序
//        localList.sort(Comparator.comparing(LocalTransaction::getOrderId));
//        channelList.sort(Comparator.comparing(ChannelTransaction::getOrderId));

        int i = 0, j = 0;
        while (i < localList.size() && j < channelList.size()) {
            LocalTransaction local = localList.get(i);
            ChannelTransaction channel = channelList.get(j);
            int cmp = local.getOrderId().compareTo(channel.getOrderId());

            if (cmp == 0) {
                if (!(local.getAmount().compareTo(channel.getAmount())==0)) {
                    saveDiffRecord(local, channel, "AMOUNT_MISMATCH");
                }
                i++;
                j++;
            } else if (cmp < 0) {
                saveDiffRecord(local, null, "MISSING_CHANNEL_ORDER");
                i++;
            } else {
                saveDiffRecord(null, channel, "MISSING_LOCAL_ORDER");
                j++;
            }
        }
        // 处理剩余记录...
    }
    @Autowired
    private CsvWriter csvWriter;
    private void saveDiffRecord(LocalTransaction local, ChannelTransaction channel, String diffType) {
        // 将差异记录写入文件或差异表（示例写入CSV）
        String record = String.format("%s,%s,%s",
                diffType,
                local != null ? local.getOrderId() : "",
                channel != null ? channel.getChannelOrderId()+ ":" + channel.getOrderId(): ""
        );
        // 实际应使用BufferedWriter写入文件或MyBatis插入数据库
//        System.out.println(record);
        csvWriter.addRecord(record);
    }
}
