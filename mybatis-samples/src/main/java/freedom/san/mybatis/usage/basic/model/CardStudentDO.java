package freedom.san.mybatis.usage.basic.model;

import freedom.san.mybatis.usage.basic.entity.Card;
import freedom.san.mybatis.usage.basic.entity.Student;
import lombok.Data;

@Data
public class CardStudentDO extends Card {
	private Student stu;
}
