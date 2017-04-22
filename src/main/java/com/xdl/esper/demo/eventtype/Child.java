package com.xdl.esper.demo.eventtype;

import lombok.Data;

import java.io.Serializable;

/**
 * child
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-16 下午1:47
 */
@Data
public class Child implements Serializable {
    String name;
    int age;
}
