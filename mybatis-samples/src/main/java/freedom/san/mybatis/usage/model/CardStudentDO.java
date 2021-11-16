package freedom.san.mybatis.usage.model;

import freedom.san.mybatis.usage.entity.Card;
import freedom.san.mybatis.usage.entity.Student;
import lombok.Data;

@Data
public class CardStudentDO extends Card {
	private Student stu;
}
