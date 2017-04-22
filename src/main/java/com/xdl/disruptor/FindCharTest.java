package com.xdl.disruptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>类描述:</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-18 下午1:56
 */
public class FindCharTest {
    public static void main(String[] args) {
        String sql = "select sum(*) from Tran where tg>#{test}# and sd=#{name}#";
//        String regx = "\\$\\{(\\w+\\}\\$)";
        String regx = "#\\{(\\w+\\}#)";
        Pattern pattern = Pattern.compile(regx);
        Matcher m = pattern.matcher(sql);
        while (m.find()){
            System.out.println(m.group().replace("${",""));
        }
        test();
    }
    private static void test(){
        String str = "from tt(st=?) where name=? and test=?";
        String regex = "(\\W\\w+=\\?)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        while(m.find()){
            System.out.println(m.group().substring(1));
        }
    }
}
