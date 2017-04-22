package com.xdl;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

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
        LocalTime ld = LocalTime.of(00,00,01);
        LocalTime ld1 = LocalTime.of(05,00,01);
        System.out.println(ld.toNanoOfDay());
        System.out.println(ld1.toNanoOfDay());
    }
}
