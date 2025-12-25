package org.panjy.servicemetricsplatform.entity.order;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应ClickHouse中的tbl_Order表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    /** 订单ID */
    private String colID;
    
    /** 拣货单ID */
    private String colPickingID;
    
    /** 客户ID */
    private String colCltID;
    
    /** 客户等级 */
    private Integer colCltLvl;
    
    /** 员工ID（下单人/责任人） */
    private String colEmpID;
    
    /** 部门ID */
    private String colDptID;
    
    /** 配送方式 */
    private String colDlvMod;
    
    /** 组织ID */
    private String colOrgID;
    
    /** 订单来源组织 */
    private Integer colOrdOrg;
    
    /** 省份 */
    private String colProvince;
    
    /** 城市 */
    private String colCity;
    
    /** 收货人姓名 */
    private String colName;
    
    /** 收货地址 */
    private String colAddress;
    
    /** 订单描述 */
    private String colDesc;
    
    /** 单价 */
    private BigDecimal colPrice;
    
    /** 订单总额 */
    private BigDecimal colTotal;
    
    /** 应收金额 */
    private BigDecimal colCollectionsAmt;
    
    /** 实际收款金额 */
    private BigDecimal colCollectionsAmtCmp;
    
    /** 运费金额 */
    private BigDecimal colTransportAmtCmp;
    
    /** 订单金额 */
    private BigDecimal colOrderAmt;
    
    /** 结算金额 */
    private BigDecimal colResultAmt;
    
    /** 下单时间 */
    private LocalDateTime colOdrTim;
    
    /** 客户积分 */
    private BigDecimal colCltSpScore;
    
    /** 积分抵扣金额 */
    private BigDecimal colSpAmount;
    
    /** 优惠券编号 */
    private String colCouponNo;
    
    /** 优惠券金额 */
    private BigDecimal colCouponSum;
    
    /** 抵扣金额 */
    private BigDecimal colDeductionSum;
    
    /** 最大可抵扣金额 */
    private BigDecimal colDeductionMaxSum;
    
    /** 预存款 */
    private BigDecimal colPreStored;
    
    /** 奖励金额 */
    private BigDecimal colAwardSum;
    
    /** 订单积分 */
    private BigDecimal colOrderInt;
    
    /** 售后天数 */
    private Integer colAfterDays;
    
    /** 是否回访 */
    private Boolean colCallBack;
    
    /** 回访备注 */
    private String colCallBackDesc;
    
    /** 是否退货 */
    private Boolean colReturn;
    
    /** 退货原因 */
    private String colReasons;
    
    /** 退货类型 */
    private Integer colReturnType;
    
    /** 发货时间 */
    private LocalDateTime colDeliveredTime;
    
    /** 员工类型 */
    private String colIsEmpType;
    
    /** 责任主管 */
    private String colDutyDirector;
    
    /** 审核价格结果 */
    private Integer colVertifyPrice;
    
    /** 是否关联其它订单 */
    private Integer colVertifyOtherOrder;
    
    /** 促销审核结果 */
    private Integer colVertifyPrm;
    
    /** 客户经理ID */
    private String colCltEmpID;
    
    /** 客户部门ID */
    private String colCltDptID;
    
    /** 支付时间 */
    private LocalDateTime paymentTime;
    
    /** 支付银行 */
    private String paymentBank;
    
    /** 备注 */
    private String colMemo;
    
    /** 订单类型 */
    private String colOrderType;
    
    /** 使用积分 */
    private BigDecimal colUsedAvlInt;
    
    /** 新增积分 */
    private BigDecimal colAddAvlInt;
    
    /** 收款方式编码 */
    private Integer colRcvMcdType;
    
    /** 奖励金额 */
    private BigDecimal colResultAward;
    
    /** 账单状态 */
    private String colBillStatus;
    
    /** 订单性质 */
    private String colOrderNature;
    
    /** 目的地 */
    private String colDest;
    
    /** 标识 */
    private String colFlag;
    
    /** 区县 */
    private String colCounty;
}