package ai.agent.data;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

/**
 * PostgreSQL 向量类型的Hibernate 自定义类型转换器
 * 用于在 Java float[] 数组和 PostgreSQL vector 类型之间进行转换
 */
public class VectorType implements UserType<float[]> {
    /**
     * 返回 SQl 类型标识
     * @return SQl 类型，使用 OTHER 表示自定义类型
     */
    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    /**
     * 返回 Java 类型
     */
    @Override
    public Class<float[]> returnedClass() {
        return float[].class;
    }

    /**
     * 比较两个向量是否相等
     * @param floats 第一个向量
     * @param j1 第二个向量
     * @return 是否相等 false 表示不相等 true 表示相等
     */
    @Override
    public boolean equals(float[] floats, float[] j1) {
        return false;
    }

    /**
     * 计算向量的哈希码
     */
    @Override
    public int hashCode(float[] floats) {
        return 0;
    }

    /**
     * 从向量库中读取向量数据并转换成 Java 对象
     * @param resultSet 结果集
     * @param position 列位置
     * @param session Hibernate 会话
     * @param owner 拥有者对象
     * @return 返回转换后的 float[] 数组
     */
    @Override
    public float[] nullSafeGet(ResultSet resultSet, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        PGobject pgObject = (PGobject) resultSet.getObject(position);
        if (pgObject.isNull()) {
            return null;
        }
        String value = pgObject.getValue();
        return parseVector(value); // 解析 vector 字符串
    }

    /**
     * 解析 PostgreSQl vector 字符串为 float[] 数组
     * @param value vector 字符串格式，如 "[1.0,2.0,3.0]"
     * @return 返回解析后的 float[] 数组
     */
    private float[] parseVector(String value) {
        // 字符串预处理：
        // "[1.0,2.0,3.0]" -> "1.0,2.0,3.0" -> ["1.0","2.0","3.0"]
        String[] parts = value.replaceAll("[\\[\\]]", "").split(",");
        // 数组初始化
        float[] vector = new float[parts.length];
        // 逐元素转换
        for (int i = 0; i < parts.length; i++) {
            // trim() 去除每个字符串元素的前后空白字符，处理可能存在的空格
            // parseFloat() 将字符串转换成 float 类型数值
            // "0.1".trim() → 0.1f
            vector[i] = Float.parseFloat(parts[i].trim());
        }
        return vector;
    }

    /**
     * 将 Java对象转换为数据库格式并设置到 prepareStatement 中
     * @param preparedStatement JDBC 预编译语句对象
     * @param value 要设置的 float[] 数组
     * @param index SQL参数的位置索引
     * @param session Hibernate 会话(是 Hibernate ORM 框架中，用于连接 Java 应用与数据库的核心接口)
     * 完成从 Java 应用到数据库的数据传输
     */
    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, float[] value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.OTHER);
            return;
        }
        PGobject pgObject = new PGobject(); // PostgreSQL JDBC 驱动提供的特殊类型
        pgObject.setType("vector"); // 明确指定数据库类型为 vector
        // [1.0, 2.0, 3.0] -> "[1.0, 2.0, 3.0]" -> "[1.0,2.0,3.0]"
        pgObject.setValue(Arrays.toString(value).replace(" ", "")); // 设置值
        // 将 PGobject 设置到指定的参数位置
        preparedStatement.setObject(index, pgObject);
    }

    /**
     * 创建对象的深拷贝
     */
    @Override
    public float[] deepCopy(float[] value) {
        return value == null ? null : Arrays.copyOf(value, value.length);
    }

    /**
     * 对象是否可变
     * @return false 表示不可变
     */
    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * 将对象分解为可序列化的形式
     */
    @Override
    public Serializable disassemble(float[] value) {
        return value;
    }

    /**
     * 从缓存的序列化形式重新组装对象
     * @param serializable 缓存的序列化对象
     * @param owner 拥有者对象
     * @return 重新组装的向量
     */
    @Override
    public float[] assemble(Serializable serializable, Object owner) {
        return (float[]) serializable;
    }
}
