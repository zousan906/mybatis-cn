package freedom.san.mybatis.usage.basic.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

//@MappedTypes(String [].class)
//@MappedJdbcTypes(JdbcType.VARCHAR)
public class StringArrayTypeHandler  extends BaseTypeHandler<String []> {
	public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, StringUtils.joinWith(",",parameter));
	}

	public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {

		return getStringArray(rs.getString(columnName));
	}

	public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getStringArray(rs.getString(columnIndex));
	}

	public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getStringArray(cs.getString(columnIndex));
	}


	private String[] getStringArray(String columnValue) {
		if (columnValue == null)
			return null;
		return columnValue.split(",");
	}
}
