package com.shengzhish.xyj.util;

import java.util.List;

/**
 * java常用工具方法
 *
 *
 */
public class JavaUtil {

    public static JavaUtil getInstance(){

        return new JavaUtil();
    }

    /**
     *
     * @param list 传入list
     * @return 字符串   例如：shu,ye,zi

     */
    public String listToString(List list){
        String result="";
        for(int i=0;i<list.size();i++){
            if(i+1==list.size()){
                result+=list.get(i).toString();
            }else{
                result+=list.get(i).toString()+",";
            }
        }
        return result;
    }
}
