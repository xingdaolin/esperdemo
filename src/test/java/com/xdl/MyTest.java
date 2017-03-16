package com.xdl;

import lombok.extern.slf4j.Slf4j;

/**
 * test
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-15 上午11:10
 */
@Slf4j
public class MyTest {
    public static void main(String[] args) {
        long l = (Long.MAX_VALUE/1000000)/(60*60*24*360);
        log.info("long:{}",l);
    }
}
