package org.panjy.servicemetricsplatform.service.analysis;

import org.panjy.servicemetricsplatform.entity.analysis.WechatMessageAnalyzeAddress;
import org.panjy.servicemetricsplatform.mapper.analysis.WechatMessageAnalyzeAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信消息地址分析结果业务服务类
 * 提供地址分析结果的存储、查询和管理功能
 */
@Service
public class WechatMessageAnalyzeAddressService {
    
    @Autowired
    private WechatMessageAnalyzeAddressMapper addressMapper;
    
    @Autowired
    private AddressValidationService addressValidationService;
    
    /**
     * 完整执行方法：保存单条地址分析结果
     * @param record 地址分析结果
     * @return 执行结果对象，包含成功状态和详细信息
     */
    @Transactional
    public ExecuteResult execute(WechatMessageAnalyzeAddress record) {
        ExecuteResult result = new ExecuteResult();
        
        try {
            // 参数验证
            if (record == null) {
                result.setSuccess(false);
                result.setMessage("地址分析结果记录不能为空");
                result.setErrorCode("PARAM_NULL");
                return result;
            }
            
            if (record.getWechatId() == null || record.getWechatTime() == null) {
                result.setSuccess(false);
                result.setMessage("记录的微信ID和时间不能为空");
                result.setErrorCode("WECHAT_ID_TIME_NULL");
                return result;
            }
            
            // 检查记录是否已存在
            if (addressMapper.countByWechatIdAndTime(record.getWechatId(), record.getWechatTime()) > 0) {
                result.setSuccess(false);
                result.setMessage("记录已存在: " + record.getWechatId() + "-" + record.getWechatTime());
                result.setErrorCode("RECORD_EXISTS");
                result.setData(record.getWechatId() + "-" + record.getWechatTime());
                return result;
            }
            
            // 执行插入操作
            int insertedRows = addressMapper.insert(record);
            
            if (insertedRows > 0) {
                result.setSuccess(true);
                result.setMessage("地址分析结果保存成功");
                result.setData(record.getWechatId() + "-" + record.getWechatTime());
                System.out.println("成功保存地址分析结果，微信ID: " + record.getWechatId() + ", 时间: " + record.getWechatTime());
            } else {
                result.setSuccess(false);
                result.setMessage("保存失败，没有记录被插入");
                result.setErrorCode("INSERT_FAILED");
            }
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("保存地址分析结果时发生错误: " + e.getMessage());
            result.setErrorCode("SYSTEM_ERROR");
            result.setException(e);
            System.err.println("保存地址分析结果失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 简化执行方法：保存单条地址分析结果
     * @param record 地址分析结果
     * @return 是否保存成功
     */
    @Transactional
    public boolean executeSimple(WechatMessageAnalyzeAddress record) {
        ExecuteResult result = execute(record);
        return result.isSuccess();
    }
    
    /**
     * 完整执行方法：批量保存地址分析结果
     * @param records 地址分析结果列表
     * @return 执行结果对象，包含成功保存的记录数和详细信息
     */
    @Transactional
    public ExecuteResult executeBatch(List<WechatMessageAnalyzeAddress> records) {
        ExecuteResult result = new ExecuteResult();
        
        try {
            // 参数验证
            if (records == null || records.isEmpty()) {
                result.setSuccess(true);
                result.setMessage("输入列表为空，无需处理");
                result.setData(0);
                return result;
            }
            
            // 过滤有效记录和检查重复
            List<WechatMessageAnalyzeAddress> validRecords = new ArrayList<>();
            List<String> duplicateIds = new ArrayList<>();
            List<String> invalidRecords = new ArrayList<>();
            
            for (WechatMessageAnalyzeAddress record : records) {
                if (record == null || record.getWechatId() == null || record.getWechatTime() == null) {
                    invalidRecords.add("记录为空或微信ID/时间为空");
                    continue;
                }
                
                // 检查是否已存在
                if (addressMapper.countByWechatIdAndTime(record.getWechatId(), record.getWechatTime()) > 0) {
                    duplicateIds.add(record.getWechatId() + "-" + record.getWechatTime());
                    continue;
                }
                
                validRecords.add(record);
            }
            
            // 分批处理，避免SQL语句过长
            int batchSize = 100;
            int totalSaved = 0;
            List<String> errorMessages = new ArrayList<>();
            
            for (int i = 0; i < validRecords.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, validRecords.size());
                List<WechatMessageAnalyzeAddress> batch = validRecords.subList(i, endIndex);
                
                try {
                    int savedInBatch = addressMapper.batchInsert(batch);
                    totalSaved += savedInBatch;
                    System.out.println("批次 " + (i / batchSize + 1) + " 保存成功，记录数: " + savedInBatch);
                } catch (Exception e) {
                    String errorMsg = "批次 " + (i / batchSize + 1) + " 保存失败: " + e.getMessage();
                    errorMessages.add(errorMsg);
                    System.err.println(errorMsg);
                }
            }
            
            // 构建结果消息
            StringBuilder message = new StringBuilder();
            message.append("批量保存完成。");
            message.append("总记录数: ").append(records.size()).append(", ");
            message.append("成功保存: ").append(totalSaved).append(", ");
            message.append("重复跳过: ").append(duplicateIds.size()).append(", ");
            message.append("无效记录: ").append(invalidRecords.size());
            
            if (!duplicateIds.isEmpty()) {
                message.append("。重复记录: ").append(String.join(", ", duplicateIds));
            }
            
            if (!errorMessages.isEmpty()) {
                message.append("。错误信息: ").append(String.join("; ", errorMessages));
            }
            
            result.setSuccess(totalSaved > 0 || (validRecords.isEmpty() && duplicateIds.isEmpty()));
            result.setMessage(message.toString());
            result.setData(totalSaved);
            
            if (!errorMessages.isEmpty()) {
                result.setErrorCode("PARTIAL_FAILURE");
            }
            
            System.out.println("批量保存地址分析结果完成，成功保存: " + totalSaved + " 条");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("批量保存地址分析结果时发生系统错误: " + e.getMessage());
            result.setErrorCode("SYSTEM_ERROR");
            result.setException(e);
            System.err.println("批量保存地址分析结果失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 简化执行方法：批量保存地址分析结果
     * @param records 地址分析结果列表
     * @return 成功保存的记录数
     */
    @Transactional
    public int executeBatchSimple(List<WechatMessageAnalyzeAddress> records) {
        ExecuteResult result = executeBatch(records);
        return result.isSuccess() ? (Integer) result.getData() : 0;
    }
    
    /**
     * 根据微信ID和时间查询地址分析结果
     * @param wechatId 微信ID
     * @param wechatTime 微信消息时间
     * @return 地址分析结果
     */
    public WechatMessageAnalyzeAddress findByWechatIdAndTime(String wechatId, Long wechatTime) {
        if (wechatId == null || wechatId.trim().isEmpty() || wechatTime == null) {
            System.err.println("查询参数微信ID和时间不能为空");
            return null;
        }
        
        try {
            return addressMapper.selectByWechatIdAndTime(wechatId.trim(), wechatTime);
        } catch (Exception e) {
            System.err.println("根据微信ID和时间查询地址分析结果失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 根据微信ID查询地址分析结果
     * @param wechatId 微信号
     * @return 地址分析结果列表
     */
    public List<WechatMessageAnalyzeAddress> findByWechatId(String wechatId) {
        if (wechatId == null || wechatId.trim().isEmpty()) {
            System.err.println("查询参数微信ID不能为空");
            return new ArrayList<>();
        }
        
        try {
            List<WechatMessageAnalyzeAddress> results = addressMapper.selectByWechatId(wechatId.trim());
            return results != null ? results : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("根据微信ID查询地址分析结果失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 根据时间范围查询地址分析结果
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     * @return 地址分析结果列表
     */
    public List<WechatMessageAnalyzeAddress> findByTimeRange(Long startTime, Long endTime) {
        if (startTime == null || endTime == null) {
            System.err.println("查询参数开始时间和结束时间不能为空");
            return new ArrayList<>();
        }
        
        if (startTime > endTime) {
            System.err.println("开始时间不能大于结束时间");
            return new ArrayList<>();
        }
        
        try {
            List<WechatMessageAnalyzeAddress> results = addressMapper.selectByTimeRange(startTime, endTime);
            return results != null ? results : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("根据时间范围查询地址分析结果失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 根据微信ID和时间范围查询地址分析结果
     * @param wechatId 微信号
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     * @return 地址分析结果列表
     */
    public List<WechatMessageAnalyzeAddress> findByWechatIdAndTimeRange(String wechatId, Long startTime, Long endTime) {
        if (wechatId == null || wechatId.trim().isEmpty()) {
            System.err.println("查询参数微信ID不能为空");
            return new ArrayList<>();
        }
        
        if (startTime == null || endTime == null) {
            System.err.println("查询参数开始时间和结束时间不能为空");
            return new ArrayList<>();
        }
        
        if (startTime > endTime) {
            System.err.println("开始时间不能大于结束时间");
            return new ArrayList<>();
        }
        
        try {
            List<WechatMessageAnalyzeAddress> results = addressMapper.selectByWechatIdAndTimeRange(wechatId.trim(), startTime, endTime);
            return results != null ? results : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("根据微信ID和时间范围查询地址分析结果失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 更新地址分析结果
     * @param record 要更新的记录
     * @return 是否更新成功
     */
    @Transactional
    public boolean update(WechatMessageAnalyzeAddress record) {
        if (record == null || record.getWechatId() == null || record.getWechatTime() == null) {
            System.err.println("更新参数记录或微信ID/时间不能为空");
            return false;
        }
        
        try {
            int updatedRows = addressMapper.updateByWechatIdAndTime(record);
            boolean success = updatedRows > 0;
            
            if (success) {
                System.out.println("成功更新地址分析结果，微信ID: " + record.getWechatId() + ", 时间: " + record.getWechatTime());
            } else {
                System.out.println("更新失败，记录可能不存在，微信ID: " + record.getWechatId() + ", 时间: " + record.getWechatTime());
            }
            
            return success;
        } catch (Exception e) {
            System.err.println("更新地址分析结果失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据微信ID和时间删除地址分析结果
     * @param wechatId 微信ID
     * @param wechatTime 微信消息时间
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteByWechatIdAndTime(String wechatId, Long wechatTime) {
        if (wechatId == null || wechatId.trim().isEmpty() || wechatTime == null) {
            System.err.println("删除参数微信ID和时间不能为空");
            return false;
        }
        
        try {
            int deletedRows = addressMapper.deleteByWechatIdAndTime(wechatId.trim(), wechatTime);
            boolean success = deletedRows > 0;
            
            if (success) {
                System.out.println("成功删除地址分析结果，微信ID: " + wechatId + ", 时间: " + wechatTime);
            } else {
                System.out.println("删除失败，记录可能不存在，微信ID: " + wechatId + ", 时间: " + wechatTime);
            }
            
            return success;
        } catch (Exception e) {
            System.err.println("删除地址分析结果失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 检查记录是否已存在
     * @param wechatId 微信ID
     * @param wechatTime 微信消息时间
     * @return 是否存在
     */
    public boolean existsByWechatIdAndTime(String wechatId, Long wechatTime) {
        if (wechatId == null || wechatId.trim().isEmpty() || wechatTime == null) {
            return false;
        }
        
        try {
            return addressMapper.countByWechatIdAndTime(wechatId.trim(), wechatTime) > 0;
        } catch (Exception e) {
            System.err.println("检查记录是否存在时发生错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 分页查询地址分析结果
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 地址分析结果列表
     */
    public List<WechatMessageAnalyzeAddress> findByPage(int page, int size) {
        if (page < 1) {
            System.err.println("页码必须大于0");
            return new ArrayList<>();
        }
        
        if (size < 1) {
            System.err.println("每页大小必须大于0");
            return new ArrayList<>();
        }
        
        try {
            int offset = (page - 1) * size;
            List<WechatMessageAnalyzeAddress> results = addressMapper.selectAll(offset, size);
            return results != null ? results : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("分页查询地址分析结果失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 查询记录总数
     * @return 记录总数
     */
    public Long count() {
        try {
            Long totalCount = addressMapper.countAll();
            return totalCount != null ? totalCount : 0L;
        } catch (Exception e) {
            System.err.println("查询记录总数失败: " + e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }
    
    /**
     * 验证指定微信ID的地址是否合法（详细版本）
     * @param wechatId 微信ID
     * @return 详细的地址验证结果
     */
    public DetailedAddressValidationResult validateAddressByWechatIdDetailed(String wechatId) {
        try {
            List<WechatMessageAnalyzeAddress> records = findByWechatId(wechatId);
            if (records.isEmpty()) {
                return new DetailedAddressValidationResult(null, false, "未找到该微信ID的地址记录");
            }
            
            // 获取最新的地址记录
            WechatMessageAnalyzeAddress latestRecord = records.get(0);
            String address = latestRecord.getAddress();
            
            // 使用AddressValidationService进行详细验证
            boolean isValid = addressValidationService.isValidAddress(address);
            
            // 如果不合法，提供具体的验证失败原因
            String validationMessage = "";
            if (!isValid) {
                validationMessage = getValidationFailureReason(address);
            }
            
            return new DetailedAddressValidationResult(address, isValid, validationMessage);
        } catch (Exception e) {
            System.err.println("验证指定微信ID的地址是否合法时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new DetailedAddressValidationResult(null, false, "验证过程中发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取地址验证失败的具体原因
     * @param address 地址
     * @return 验证失败原因
     */
    private String getValidationFailureReason(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "地址为空";
        }
        
        String trimmedAddress = address.trim();
        
        if (trimmedAddress.length() < 3) {
            return "地址长度过短";
        }
        
        if (trimmedAddress.matches("^\\d+$")) {
            return "地址只包含数字";
        }
        
        if (trimmedAddress.matches(".*[!@#$%^&*()_+=\\[\\]{}|\\\\:\";'<>?,./`~].*")) {
            return "地址包含非法字符";
        }
        
        if (!trimmedAddress.matches(".*[省市县区镇村街道路街巷弄号].*")) {
            return "地址缺少地址关键字";
        }
        
        boolean containsProvinceOrCity = false;
        for (String provinceOrCity : java.util.Arrays.asList(
                "北京市", "天津市", "上海市", "重庆市", "河北省", "山西省", "辽宁省", "吉林省", "黑龙江省",
                "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省", "河南省", "湖北省", "湖南省",
                "广东省", "海南省", "四川省", "贵州省", "云南省", "陕西省", "甘肃省", "青海省", "台湾省",
                "内蒙古自治区", "广西壮族自治区", "西藏自治区", "宁夏回族自治区", "新疆维吾尔自治区",
                "香港特别行政区", "澳门特别行政区")) {
            if (trimmedAddress.contains(provinceOrCity)) {
                containsProvinceOrCity = true;
                break;
            }
        }
        
        if (!containsProvinceOrCity && !trimmedAddress.contains("省") && !trimmedAddress.contains("市")) {
            return "地址缺少省份或城市信息";
        }
        
        if (!trimmedAddress.contains(",") && !trimmedAddress.contains("，") && 
            !trimmedAddress.contains(" ") && trimmedAddress.length() < 10) {
            return "地址格式可能不完整";
        }
        
        return "地址验证未通过";
    }
    
    /**
     * 验证指定微信ID的地址是否合法
     * @param wechatId 微信ID
     * @return 地址验证结果
     */
    public AddressValidationResult validateAddressByWechatId(String wechatId) {
        try {
            List<WechatMessageAnalyzeAddress> records = findByWechatId(wechatId);
            if (records.isEmpty()) {
                return new AddressValidationResult(null, false);
            }
            
            // 获取最新的地址记录
            WechatMessageAnalyzeAddress latestRecord = records.get(0);
            String address = latestRecord.getAddress();
            
            boolean isValid = addressValidationService.isValidAddress(address);
            return new AddressValidationResult(address, isValid);
        } catch (Exception e) {
            System.err.println("验证指定微信ID的地址是否合法时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new AddressValidationResult(null, false);
        }
    }
    
    /**
     * 验证所有地址是否合法
     * @return 地址验证结果列表
     */
    public List<AddressValidationService.AddressValidationResult> validateAllAddresses() {
        try {
            return addressValidationService.validateAllProcessedAddresses();
        } catch (Exception e) {
            System.err.println("验证所有地址是否合法时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 按时间范围查询地址分析结果并验证地址合法性，返回所有合法的对象
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     * @return 合法的地址分析结果列表
     */
    public List<WechatMessageAnalyzeAddress> findValidAddressesByTimeRange(Long startTime, Long endTime) {
        // 1. 参数验证
        if (startTime == null || endTime == null) {
            System.err.println("查询参数开始时间和结束时间不能为空");
            return new ArrayList<>();
        }
        
        if (startTime > endTime) {
            System.err.println("开始时间不能大于结束时间");
            return new ArrayList<>();
        }
        
        try {
            // 2. 根据时间范围查询地址分析结果
            List<WechatMessageAnalyzeAddress> results = findByTimeRange(startTime, endTime);
            
            // 3. 如果查询结果为空，直接返回空列表
            if (results == null || results.isEmpty()) {
                System.out.println("在指定时间范围内未找到任何地址分析结果");
                return new ArrayList<>();
            }
            
            // 4. 过滤出地址合法的对象
            List<WechatMessageAnalyzeAddress> validAddresses = new ArrayList<>();
            
            for (WechatMessageAnalyzeAddress record : results) {
                // 检查地址是否合法
                if (record.getAddress() != null && addressValidationService.isValidAddress(record.getAddress())) {
                    validAddresses.add(record);
                }
            }
            
            System.out.println("时间范围查询完成，总记录数: " + results.size() + "，合法地址数: " + validAddresses.size());
            
            return validAddresses;
        } catch (Exception e) {
            System.err.println("按时间范围查询并验证地址合法性时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 按微信ID和时间范围查询地址分析结果并验证地址合法性，返回所有合法的对象
     * @param wechatId 微信ID
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     * @return 合法的地址分析结果列表
     */
    public List<WechatMessageAnalyzeAddress> findValidAddressesByWechatIdAndTimeRange(String wechatId, Long startTime, Long endTime) {
        // 1. 参数验证
        if (wechatId == null || wechatId.trim().isEmpty()) {
            System.err.println("查询参数微信ID不能为空");
            return new ArrayList<>();
        }
        
        if (startTime == null || endTime == null) {
            System.err.println("查询参数开始时间和结束时间不能为空");
            return new ArrayList<>();
        }
        
        if (startTime > endTime) {
            System.err.println("开始时间不能大于结束时间");
            return new ArrayList<>();
        }
        
        try {
            // 2. 根据微信ID和时间范围查询地址分析结果
            List<WechatMessageAnalyzeAddress> results = findByWechatIdAndTimeRange(wechatId.trim(), startTime, endTime);
            
            // 3. 如果查询结果为空，直接返回空列表
            if (results == null || results.isEmpty()) {
                System.out.println("在指定微信ID和时间范围内未找到任何地址分析结果");
                return new ArrayList<>();
            }
            
            // 4. 过滤出地址合法的对象
            List<WechatMessageAnalyzeAddress> validAddresses = new ArrayList<>();
            
            for (WechatMessageAnalyzeAddress record : results) {
                // 检查地址是否合法
                if (record.getAddress() != null && addressValidationService.isValidAddress(record.getAddress())) {
                    validAddresses.add(record);
                }
            }
            
            System.out.println("微信ID和时间范围查询完成，总记录数: " + results.size() + "，合法地址数: " + validAddresses.size());
            
            return validAddresses;
        } catch (Exception e) {
            System.err.println("按微信ID和时间范围查询并验证地址合法性时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 执行结果封装类
     */
    public static class ExecuteResult {
        private boolean success;
        private String message;
        private String errorCode;
        private Object data;
        private Exception exception;
        
        public ExecuteResult() {
            this.success = false;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
        
        public Object getData() {
            return data;
        }
        
        public void setData(Object data) {
            this.data = data;
        }
        
        public Exception getException() {
            return exception;
        }
        
        public void setException(Exception exception) {
            this.exception = exception;
        }
        
        @Override
        public String toString() {
            return "ExecuteResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", errorCode='" + errorCode + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
    
    /**
     * 地址验证结果封装类
     */
    public static class AddressValidationResult {
        private String address;
        private boolean valid;
        
        public AddressValidationResult(String address, boolean valid) {
            this.address = address;
            this.valid = valid;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        @Override
        public String toString() {
            return "AddressValidationResult{" +
                    "address='" + address + '\'' +
                    ", valid=" + valid +
                    '}';
        }
    }
    
    /**
     * 详细地址验证结果封装类
     */
    public static class DetailedAddressValidationResult {
        private String address;
        private boolean valid;
        private String validationMessage;
        
        public DetailedAddressValidationResult(String address, boolean valid, String validationMessage) {
            this.address = address;
            this.valid = valid;
            this.validationMessage = validationMessage;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getValidationMessage() {
            return validationMessage;
        }
        
        public void setValidationMessage(String validationMessage) {
            this.validationMessage = validationMessage;
        }
        
        @Override
        public String toString() {
            return "DetailedAddressValidationResult{" +
                    "address='" + address + '\'' +
                    ", valid=" + valid +
                    ", validationMessage='" + validationMessage + '\'' +
                    '}';
        }
    }
}