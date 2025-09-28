package com.coding.cz.recon.parser;

import java.util.List;
import java.util.Map;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
public interface DataParser {
    // parse raw bytes into list of map<targetField, rawValue>
    List<Map<String,String>> parse(byte[] raw) throws Exception;
}
