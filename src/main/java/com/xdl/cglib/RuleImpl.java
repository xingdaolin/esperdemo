package com.xdl.cglib;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>类描述:</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-06 下午4:02
 */
@Slf4j
public class RuleImpl implements IRule{
    @Override
    public void execute(Object... params) {
        if (params!=null){
            for (Object obj:params){
                log.info(obj.toString());
            }
        }
    }
}
