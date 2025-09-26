package org.panjy.servicemetricsplatform.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Address字段的自定义TypeHandler
 * 用于在Mapper层对address字段进行特殊处理
 * 插入时保持原始地址，查询时进行处理
 */
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.LONGVARCHAR)
public class AddressTypeHandler extends BaseTypeHandler<String> {

    // 地址脱敏的正则表达式模式
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("(\\S{3}).*(\\S{3})");
    
    // 敏感词过滤列表
    private static final String[] SENSITIVE_WORDS = {"敏感", "机密", "秘密"};

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // 插入时保持原始地址不变
        ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 查询时对address字段进行处理
        String address = rs.getString(columnName);
        return processAddressForRetrieval(address);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 查询时对address字段进行处理
        String address = rs.getString(columnIndex);
        return processAddressForRetrieval(address);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 查询时对address字段进行处理
        String address = cs.getString(columnIndex);
        return processAddressForRetrieval(address);
    }

    /**
     * 查询后对address字段进行处理
     * @param address 原始地址
     * @return 处理后的地址
     */
    private String processAddressForRetrieval(String address) {
        if (address == null) {
            return null;
        }
        
        // 1. 过滤敏感词
        String filteredAddress = filterSensitiveWords(address);
        
        // 2. 格式化显示 - 根据新需求处理地址
        return formatAddressForDisplay(filteredAddress);
    }

    /**
     * 过滤敏感词
     * @param address 原始地址
     * @return 过滤后的地址
     */
    private String filterSensitiveWords(String address) {
        String filtered = address;
        for (String sensitiveWord : SENSITIVE_WORDS) {
            filtered = filtered.replace(sensitiveWord, "***");
        }
        return filtered;
    }

    /**
     * 格式化地址用于显示 - 实现新需求
     * @param address 原始地址
     * @return 格式化后的地址
     */
    private String formatAddressForDisplay(String address) {
        // 检查是否包含至少两个"-"
        if (address == null || countChar(address, '-') < 2) {
            // 不足两个"-"的地址不进行处理
            return address;
        }
        
        // 按照新规则处理地址
        return processAddressWithDashes(address);
    }
    
    /**
     * 统计字符串中某个字符的出现次数
     * @param str 字符串
     * @param ch 要统计的字符
     * @return 字符出现次数
     */
    private int countChar(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 处理包含"-"的地址
     * @param address 原始地址
     * @return 处理后的地址
     */
    private String processAddressWithDashes(String address) {
        // 按"-"分割地址
        String[] parts = address.split("-");
        
        // 构建新地址格式
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            
            if (i < parts.length - 1) {
                // 非最后一部分，取前两个字符
                if (part.length() >= 2) {
                    result.append(part.substring(0, 2));
                } else {
                    result.append(part);
                }
                
                // 添加分隔符
                result.append("%");
            } else {
                // 最后一部分，特殊处理
                result.append(processLastPart(part));
            }
        }
        
        return result.toString();
    }
    
    /**
     * 处理最后一部分
     * 根据新规则：
     * - 如果前两个字符和后两个字符不重合，则取前两个字符和后两个字符，中间添加%
     * - 否则直接取全部字符
     * 
     * 示例：
     * "顾山镇锡张路422号" -> "顾山镇锡张路422号" (长度为8，处理后也为8，无缩短效果)
     * "翟营南大街458号海天花园小区12-4-702" -> "翟营%02" (长度为15，处理后为5，有缩短效果)
     * 
     * @param lastPart 最后一部分
     * @return 处理后的最后一部分
     */
    private String processLastPart(String lastPart) {
        // 如果长度小于4，直接返回原字符串
        if (lastPart.length() < 4) {
            return lastPart;
        }
        
        // 取前两个字符
        String prefix = lastPart.substring(0, 2);
        
        // 取后两个字符
        String suffix = lastPart.substring(lastPart.length() - 2);
        
        // 检查前两个字符和后两个字符是否有重合
        // 前两个字符位置：0, 1
        // 后两个字符位置：lastPart.length()-2, lastPart.length()-1
        // 如果前两个字符的结束位置(1) >= 后两个字符的开始位置(lastPart.length()-2)，则有重合
        
        if (1 >= (lastPart.length() - 2)) {
            // 有重合，直接返回原字符串
            return lastPart;
        } else {
            // 无重合，处理后的字符串为 prefix + "%" + suffix，长度为5
            // 只有当处理后的字符串比原字符串短时才进行处理
            if (lastPart.length() > 5) {
                // 处理后的字符串更短，返回处理后的结果
                return prefix + "%" + suffix;
            } else {
                // 处理后的字符串不更短，直接返回原字符串
                return lastPart;
            }
        }
    }
}