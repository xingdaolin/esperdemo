package com.xdl.esper.demo.eventtype;

import lombok.Data;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author: xingdaolin@msn.cn
 * @date :17-3-18
 */
@Data
public class Apple implements Serializable {
    int price;
    String name;
}
