package org.panjy.servicemetricsplatform.entity;

import java.time.LocalDateTime;

/**
 * 客户信息实体类
 * 对应ClickHouse中的aikang.tbl_Client表
 * 
 * @author System Generated
 */
public class Client {

    /**
     * 客户ID
     */
    private String colCltID;

    /**
     * 员工ID（客户归属人）
     */
    private String colEmpID;

    /**
     * 部门ID
     */
    private String colDptID;

    /**
     * 电话
     */
    private String colPhs;

    /**
     * 客户姓名
     */
    private String colName;

    /**
     * 性别
     */
    private String colGender;

    /**
     * 地址
     */
    private String colAddress;

    /**
     * 省份
     */
    private String colProvince;

    /**
     * 城市
     */
    private String colCity;

    /**
     * 年龄
     */
    private Integer colAge;

    /**
     * 邮编
     */
    private String colZip;

    /**
     * QQ号码
     */
    private String colQQ;

    /**
     * MSN账号
     */
    private String colMSN;

    /**
     * 备注
     */
    private String colDemo;

    /**
     * 创建时间
     */
    private LocalDateTime colCrtTim;

    /**
     * 是否启用
     */
    private Integer colEnable;

    /**
     * 客户等级
     */
    private Integer colLevel;

    /**
     * 来源媒体ID
     */
    private String colMediaID;

    /**
     * 最近登录时间
     */
    private LocalDateTime collastlogintime;

    /**
     * 职业
     */
    private String colProfession;

    /**
     * 兴趣活动
     */
    private String colActivity;

    /**
     * 是否推荐客户
     */
    private Integer colRecommend;

    /**
     * 身高
     */
    private Integer colHeight;

    /**
     * 体重
     */
    private Integer colWeight;

    /**
     * 腰围
     */
    private Integer colWaist;

    /**
     * 胸围
     */
    private Integer colBust;

    /**
     * 分类ID
     */
    private Integer colCataLog;

    /**
     * 地区
     */
    private String colArea;

    /**
     * 血型
     */
    private String colBloodType;

    /**
     * 健康状况
     */
    private String colHealth;

    /**
     * 收入水平
     */
    private String colIncome;

    /**
     * 属性分类
     */
    private String colAttrClass;
    
    // 构造函数
    public Client() {
    }
    
    public Client(String colCltID, String colEmpID, String colDptID, String colPhs, String colName, 
                  String colGender, String colAddress, String colProvince, String colCity, Integer colAge, 
                  String colZip, String colQQ, String colMSN, String colDemo, LocalDateTime colCrtTim, 
                  Integer colEnable, Integer colLevel, String colMediaID, LocalDateTime collastlogintime, 
                  String colProfession, String colActivity, Integer colRecommend, Integer colHeight, 
                  Integer colWeight, Integer colWaist, Integer colBust, Integer colCataLog, String colArea, 
                  String colBloodType, String colHealth, String colIncome, String colAttrClass) {
        this.colCltID = colCltID;
        this.colEmpID = colEmpID;
        this.colDptID = colDptID;
        this.colPhs = colPhs;
        this.colName = colName;
        this.colGender = colGender;
        this.colAddress = colAddress;
        this.colProvince = colProvince;
        this.colCity = colCity;
        this.colAge = colAge;
        this.colZip = colZip;
        this.colQQ = colQQ;
        this.colMSN = colMSN;
        this.colDemo = colDemo;
        this.colCrtTim = colCrtTim;
        this.colEnable = colEnable;
        this.colLevel = colLevel;
        this.colMediaID = colMediaID;
        this.collastlogintime = collastlogintime;
        this.colProfession = colProfession;
        this.colActivity = colActivity;
        this.colRecommend = colRecommend;
        this.colHeight = colHeight;
        this.colWeight = colWeight;
        this.colWaist = colWaist;
        this.colBust = colBust;
        this.colCataLog = colCataLog;
        this.colArea = colArea;
        this.colBloodType = colBloodType;
        this.colHealth = colHealth;
        this.colIncome = colIncome;
        this.colAttrClass = colAttrClass;
    }
    
    // Getter和Setter方法
    public String getColCltID() {
        return colCltID;
    }
    
    public void setColCltID(String colCltID) {
        this.colCltID = colCltID.trim();
    }
    
    public String getColEmpID() {
        return colEmpID;
    }
    
    public void setColEmpID(String colEmpID) {
        this.colEmpID = colEmpID;
    }
    
    public String getColDptID() {
        return colDptID;
    }
    
    public void setColDptID(String colDptID) {
        this.colDptID = colDptID;
    }
    
    public String getColPhs() {
        return colPhs;
    }
    
    public void setColPhs(String colPhs) {
        this.colPhs = colPhs;
    }
    
    public String getColName() {
        return colName;
    }
    
    public void setColName(String colName) {
        this.colName = colName;
    }
    
    public String getColGender() {
        return colGender;
    }
    
    public void setColGender(String colGender) {
        this.colGender = colGender;
    }
    
    public String getColAddress() {
        return colAddress;
    }
    
    public void setColAddress(String colAddress) {
        this.colAddress = colAddress;
    }
    
    public String getColProvince() {
        return colProvince;
    }
    
    public void setColProvince(String colProvince) {
        this.colProvince = colProvince;
    }
    
    public String getColCity() {
        return colCity;
    }
    
    public void setColCity(String colCity) {
        this.colCity = colCity;
    }
    
    public Integer getColAge() {
        return colAge;
    }
    
    public void setColAge(Integer colAge) {
        this.colAge = colAge;
    }
    
    public String getColZip() {
        return colZip;
    }
    
    public void setColZip(String colZip) {
        this.colZip = colZip;
    }
    
    public String getColQQ() {
        return colQQ;
    }
    
    public void setColQQ(String colQQ) {
        this.colQQ = colQQ;
    }
    
    public String getColMSN() {
        return colMSN;
    }
    
    public void setColMSN(String colMSN) {
        this.colMSN = colMSN;
    }
    
    public String getColDemo() {
        return colDemo;
    }
    
    public void setColDemo(String colDemo) {
        this.colDemo = colDemo;
    }
    
    public LocalDateTime getColCrtTim() {
        return colCrtTim;
    }
    
    public void setColCrtTim(LocalDateTime colCrtTim) {
        this.colCrtTim = colCrtTim;
    }
    
    public Integer getColEnable() {
        return colEnable;
    }
    
    public void setColEnable(Integer colEnable) {
        this.colEnable = colEnable;
    }
    
    public Integer getColLevel() {
        return colLevel;
    }
    
    public void setColLevel(Integer colLevel) {
        this.colLevel = colLevel;
    }
    
    public String getColMediaID() {
        return colMediaID;
    }
    
    public void setColMediaID(String colMediaID) {
        this.colMediaID = colMediaID;
    }
    
    public LocalDateTime getCollastlogintime() {
        return collastlogintime;
    }
    
    public void setCollastlogintime(LocalDateTime collastlogintime) {
        this.collastlogintime = collastlogintime;
    }
    
    public String getColProfession() {
        return colProfession;
    }
    
    public void setColProfession(String colProfession) {
        this.colProfession = colProfession;
    }
    
    public String getColActivity() {
        return colActivity;
    }
    
    public void setColActivity(String colActivity) {
        this.colActivity = colActivity;
    }
    
    public Integer getColRecommend() {
        return colRecommend;
    }
    
    public void setColRecommend(Integer colRecommend) {
        this.colRecommend = colRecommend;
    }
    
    public Integer getColHeight() {
        return colHeight;
    }
    
    public void setColHeight(Integer colHeight) {
        this.colHeight = colHeight;
    }
    
    public Integer getColWeight() {
        return colWeight;
    }
    
    public void setColWeight(Integer colWeight) {
        this.colWeight = colWeight;
    }
    
    public Integer getColWaist() {
        return colWaist;
    }
    
    public void setColWaist(Integer colWaist) {
        this.colWaist = colWaist;
    }
    
    public Integer getColBust() {
        return colBust;
    }
    
    public void setColBust(Integer colBust) {
        this.colBust = colBust;
    }
    
    public Integer getColCataLog() {
        return colCataLog;
    }
    
    public void setColCataLog(Integer colCataLog) {
        this.colCataLog = colCataLog;
    }
    
    public String getColArea() {
        return colArea;
    }
    
    public void setColArea(String colArea) {
        this.colArea = colArea;
    }
    
    public String getColBloodType() {
        return colBloodType;
    }
    
    public void setColBloodType(String colBloodType) {
        this.colBloodType = colBloodType;
    }
    
    public String getColHealth() {
        return colHealth;
    }
    
    public void setColHealth(String colHealth) {
        this.colHealth = colHealth;
    }
    
    public String getColIncome() {
        return colIncome;
    }
    
    public void setColIncome(String colIncome) {
        this.colIncome = colIncome;
    }
    
    public String getColAttrClass() {
        return colAttrClass;
    }
    
    public void setColAttrClass(String colAttrClass) {
        this.colAttrClass = colAttrClass;
    }
    
    // toString方法
    @Override
    public String toString() {
        return "Client{" +
                "colCltID='" + colCltID + '\'' +
                ", colEmpID='" + colEmpID + '\'' +
                ", colDptID='" + colDptID + '\'' +
                ", colPhs='" + colPhs + '\'' +
                ", colName='" + colName + '\'' +
                ", colGender='" + colGender + '\'' +
                ", colAddress='" + colAddress + '\'' +
                ", colProvince='" + colProvince + '\'' +
                ", colCity='" + colCity + '\'' +
                ", colAge=" + colAge +
                ", colZip='" + colZip + '\'' +
                ", colQQ='" + colQQ + '\'' +
                ", colMSN='" + colMSN + '\'' +
                ", colDemo='" + colDemo + '\'' +
                ", colCrtTim=" + colCrtTim +
                ", colEnable=" + colEnable +
                ", colLevel=" + colLevel +
                ", colMediaID='" + colMediaID + '\'' +
                ", collastlogintime=" + collastlogintime +
                ", colProfession='" + colProfession + '\'' +
                ", colActivity='" + colActivity + '\'' +
                ", colRecommend=" + colRecommend +
                ", colHeight=" + colHeight +
                ", colWeight=" + colWeight +
                ", colWaist=" + colWaist +
                ", colBust=" + colBust +
                ", colCataLog=" + colCataLog +
                ", colArea='" + colArea + '\'' +
                ", colBloodType='" + colBloodType + '\'' +
                ", colHealth='" + colHealth + '\'' +
                ", colIncome='" + colIncome + '\'' +
                ", colAttrClass='" + colAttrClass + '\'' +
                '}';
    }
}