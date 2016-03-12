package feipai.qiangdan.javabean;

import java.util.List;

/**
 * 转账记录
 */
public class TransList {
    private int size;
    private List<TransBean> list;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<TransBean> getList() {
        return list;
    }

    public void setList(List<TransBean> list) {
        this.list = list;
    }
}
