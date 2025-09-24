package org.panjy.servicemetricsplatform.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.Date;

@MappedJdbcTypes(JdbcType.TIMESTAMP)
@MappedTypes(Date.class)
public class ClickHouseDateTypeHandler extends BaseTypeHandler<Date> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        // 将Java Date转换为ClickHouse兼容的格式，去除毫秒部分
        Timestamp timestamp = new Timestamp(parameter.getTime());
        timestamp.setNanos(0); // 去除纳秒（毫秒）部分
        ps.setTimestamp(i, timestamp);
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp != null ? new Date(timestamp.getTime()) : null;
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnIndex);
        return timestamp != null ? new Date(timestamp.getTime()) : null;
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Timestamp timestamp = cs.getTimestamp(columnIndex);
        return timestamp != null ? new Date(timestamp.getTime()) : null;
    }
}