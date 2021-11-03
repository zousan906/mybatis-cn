package freedom.san.mybatis.usage.basic.provider;

import org.apache.ibatis.jdbc.SQL;

public class StudentProvider {


	public String selectById(final Integer id) {
		return new SQL() {
			{
				SELECT("id,name,age,sex,cid");
				FROM("student");
				WHERE("id=#{id}");
			}
		}.toString();
	}
}
