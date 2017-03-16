package com.xdl.esper.demo.eventtype;

import lombok.Data;

import java.io.Serializable;

/**
 * 地址
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-16 下午1:44
 */
@Data
public class Address implements Serializable{
    String road;
    String street;
    int houseNo;
}
