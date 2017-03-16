package com.xdl.esper.demo.eventtype;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 复杂数据类型嵌套
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-16 下午1:48
 */
@Data
public class Person implements Serializable {
    String name;
    List<Child> children;
    Map<String,Integer> phones;
    Address address;
    public Child getChild(int index){
        return this.children.get(index);
    }

    public void setPhones(String name,Integer number){
        phones.put(name,number);
    }
}
