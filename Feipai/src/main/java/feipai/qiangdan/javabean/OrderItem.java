package feipai.qiangdan.javabean;

import java.io.Serializable;

/**
 * 单个订单
 */
public class OrderItem implements Serializable {
    private int id;
    private String orderNum;
    private String jAddress;
    private String sAddress;
    private String distance;
    private String price;
    private String weight;
    /**
     * 快递员
     */
    private String employee;
    /**
     * 接单到取件计划用时(单位：分钟；当type为买时，该字段为0)
     */
    private String jTimePlan;
    /**
     * 取件到收件计划用时(单位：分钟)
     */
    private String sTimePlan;
    /**
     * /**
     * 是否可以被抢(1:是；0：否)
     */
    private int canRob;
    /**
     * 是否属于定时取 (1:是；0：否)
     */
    private int isTime;

    /**
     * 定时取时间(当isTime=0时，该字段为“”)
     */
    private String jTime;
    /**
     * 定时送时间(当isSong=0时，该字段为“”)
     */
    private String endTime;
    /**
     * 是否属于定时送 (1:是；0：否)
     */
    private int isSong;
    /**
     * 订单状态
     */
    private String type;
    /**
     * 寄件人电话
     */
    private String jPhone;
    /**
     * 寄件人电话
     */
    // 收件人电话
    private String sPhone;
    /**
     * 寄件人姓名
     */
    private String jName;
    /**
     * 收件人姓名
     */
    private String sName;
    /**
     * 物品名称
     */
    private String goodsName;
    /**
     * 客户备注
     */
    private String remark;
    /**
     * 支付类型
     */
    private String paymentType;
    /**
     * 发布时间
     */
    private String publishTime;
    /**
     * 抢单时间
     */
    private String robOrderTime;
    /**
     * 是否使用优惠券 (1:使用；0：没使用)
     */
    private int useCode;
    /**
     * 是否需要发票(1:是；0：否)
     */
    private String haveInvoice;
    /**
     * 订单状态
     */
    private String state;
    /**
     * 优惠金额
     */
    private String code_price;

    public String getCode_price() {
        return code_price;
    }

    public void setCode_price(String code_price) {
        this.code_price = code_price;
    }

    public String getjPhone() {
        return jPhone;
    }

    public void setjPhone(String jPhone) {
        this.jPhone = jPhone;
    }

    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public String getjName() {
        return jName;
    }

    public void setjName(String jName) {
        this.jName = jName;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getRobOrderTime() {
        return robOrderTime;
    }

    public void setRobOrderTime(String robOrderTime) {
        this.robOrderTime = robOrderTime;
    }

    public int getUseCode() {
        return useCode;
    }

    public void setUseCode(int useCode) {
        this.useCode = useCode;
    }

    public String getHaveInvoice() {
        return haveInvoice;
    }

    public void setHaveInvoice(String haveInvoice) {
        this.haveInvoice = haveInvoice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getjAddress() {
        return jAddress;
    }

    public void setjAddress(String jAddress) {
        this.jAddress = jAddress;
    }

    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getIsTime() {
        return isTime;
    }

    public void setIsTime(int isTime) {
        this.isTime = isTime;
    }

    public int getIsSong() {
        return isSong;
    }

    public void setIsSong(int isSong) {
        this.isSong = isSong;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCanRob() {
        return canRob;
    }

    public void setCanRob(int canRob) {
        this.canRob = canRob;
    }

    public String getjTimePlan() {
        return jTimePlan;
    }

    public void setjTimePlan(String jTimePlan) {
        this.jTimePlan = jTimePlan;
    }

    public String getsTimePlan() {
        return sTimePlan;
    }

    public void setsTimePlan(String sTimePlan) {
        this.sTimePlan = sTimePlan;
    }

    public String getjTime() {
        return jTime;
    }

    public void setjTime(String jTime) {
        this.jTime = jTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }
}
