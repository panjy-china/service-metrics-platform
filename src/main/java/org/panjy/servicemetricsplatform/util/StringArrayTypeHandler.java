package org.panjy.servicemetricsplatform.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字符串数组类型处理器
 * 用于处理ClickHouse的groupArray函数返回的数组类型
 */
@MappedTypes(String[].class)
@MappedJdbcTypes(JdbcType.ARRAY)
public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
        // 设置参数，这里我们不需要实现，因为我们只做查询
        ps.setObject(i, parameter);
    }

    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从ResultSet中获取数组结果
        Object array = rs.getObject(columnName);
        return convertToArray(array);
    }

    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 从ResultSet中获取数组结果
        Object array = rs.getObject(columnIndex);
        return convertToArray(array);
    }

    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 从CallableStatement中获取数组结果
        Object array = cs.getObject(columnIndex);
        return convertToArray(array);
    }

    /**
     * 将数据库返回的对象转换为字符串数组
     * @param obj 数据库返回的对象
     * @return 字符串数组
     */
    private String[] convertToArray(Object obj) {
        if (obj == null) {
            return null;
        }
        
        // 处理不同数据库返回的数组格式
        if (obj instanceof String[]) {
            return (String[]) obj;
        } else if (obj instanceof Object[]) {
            Object[] objArray = (Object[]) obj;
            String[] strArray = new String[objArray.length];
            for (int i = 0; i < objArray.length; i++) {
                strArray[i] = objArray[i] != null ? objArray[i].toString() : null;
            }
            return strArray;
        } else if (obj instanceof String) {
            // 如果是逗号分隔的字符串，按逗号分割
            String str = (String) obj;
            if (str.startsWith("[") && str.endsWith("]")) {
                // 去除方括号
                str = str.substring(1, str.length() - 1);
            }
            return str.split(",");
        } else {
            // 其他情况，转换为字符串再处理
            return new String[]{obj.toString()};
        }
    }
}