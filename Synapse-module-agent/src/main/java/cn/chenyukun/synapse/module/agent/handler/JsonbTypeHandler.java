package cn.chenyukun.synapse.module.agent.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL JSONB 类型处理器
 * 用于处理 Java 对象与 PostgreSQL JSONB 类型之间的转换
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbTypeHandler extends BaseTypeHandler<Object> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String JSONB_TYPE = "jsonb";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将 Java 对象转换为 JSON 字符串
            String json = objectMapper.writeValueAsString(parameter);
            
            // 创建 PGobject 并设置为 jsonb 类型
            PGobject pgObject = new PGobject();
            pgObject.setType(JSONB_TYPE);
            pgObject.setValue(json);
            
            ps.setObject(i, pgObject);
        } catch (Exception e) {
            throw new SQLException("Error converting object to JSONB: " + e.getMessage(), e);
        }
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    private Object parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            // 尝试解析为 Map
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSONB: " + e.getMessage(), e);
        }
    }
}

