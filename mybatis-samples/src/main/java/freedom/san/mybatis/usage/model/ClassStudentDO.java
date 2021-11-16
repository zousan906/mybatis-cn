package freedom.san.mybatis.usage.model;

import java.util.List;

import freedom.san.mybatis.usage.entity.Classes;
import freedom.san.mybatis.usage.entity.Student;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Alias("ClassStudent")
@Getter
@Setter
public class ClassStudentDO extends Classes {
	private List<Student> students;

	private String[] names;

	@Override
	public String toString() {
		return "ClassStudentDO{}" + super.toString() + " student size:" + students.size();
	}

}
