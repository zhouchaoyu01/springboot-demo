package com.coding.mapper;


import com.coding.entity.ChannelTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ThinkPad
* @description 针对表【channel_transaction】的数据库操作Mapper
* @createDate 2025-05-08 09:57:07
* @Entity com.coding.ChannelTransaction
*/
@Mapper
public interface ChannelTransactionMapper {

    void insertBatch(List<ChannelTransaction> channelList);

    List<ChannelTransaction> selectByOrderIds(@Param("orderIds") List<String> orderIds);
}




