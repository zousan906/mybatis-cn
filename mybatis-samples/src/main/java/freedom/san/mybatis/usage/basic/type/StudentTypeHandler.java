package freedom.san.mybatis.usage.basic.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import freedom.san.mybatis.usage.basic.entity.Student;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Student.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StudentTypeHandler extends BaseTypeHandler<Student> {
	public void setNonNullParameter(PreparedStatement ps, int i, Student parameter, JdbcType jdbcType) throws SQLException {
		String[] strings = {parameter.getName(), String.valueOf(parameter.getAge()), String.valueOf(parameter.getSex())};
		ps.setString(i, StringUtils.joinWith(",", strings));
	}

	public Student getNullableResult(ResultSet rs, String columnName) throws SQLException {

		return getStringArray(rs.getString(columnName));
	}

	public Student getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getStringArray(rs.getString(columnIndex));
	}

	public Student getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getStringArray(cs.getString(columnIndex));
	}


	private Student getStringArray(String columnValue) {
		if (columnValue == null)
			return null;
		String[] split = columnValue.split(",");
		Student student = new Student();
		student.setName(split[0]);
		student.setAge(Integer.valueOf(split[1]));
		student.setSex(Integer.valueOf(split[2]));
		return student;
	}
}
