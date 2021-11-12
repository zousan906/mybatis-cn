package freedom.san.mybatis.usage.basic.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class StringArrayTypeHandler  extends BaseTypeHandler<String []> {
	public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {

	}

	public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return new String[0];
	}

	public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return new String[0];
	}

	public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return new String[0];
	}
}
