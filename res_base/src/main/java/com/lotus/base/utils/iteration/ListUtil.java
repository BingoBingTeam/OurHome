package com.lotus.base.utils.iteration;

import java.util.ArrayList;
import java.util.List;

/**
 * list 工具
 * Created by pxf on 2018/11/16.
 */

public class ListUtil {

    /**
     * 拆分list
     * @param source 原始list
     * @param n 每段多少个元素
     * @param <T> 对象
     * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n){
        List<List<T>> result=new ArrayList<List<T>>();
        int remaider=source.size()%n; //(先计算出余数)
        int number=source.size()/n; //然后是商

        int offset = number;
        if (remaider > 0){
            offset++;
        }
        for (int i = 0; i < offset; i++) {
            List<T> value=null;
            if (i < number) {
                value = source.subList(i * n, (i + 1) * n);
            }else {
                value = source.subList(i * n, source.size());
            }
            result.add(value);
        }
        return result;
    }
}
