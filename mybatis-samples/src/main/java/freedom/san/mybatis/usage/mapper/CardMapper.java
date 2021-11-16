package freedom.san.mybatis.usage.mapper;

import freedom.san.mybatis.usage.entity.Card;
import freedom.san.mybatis.usage.entity.Student;
import freedom.san.mybatis.usage.model.CardStudentDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * @Entity freedom.san.mybatis.domain.Card
 */

@Mapper
public interface CardMapper {

	@Select("select * from card where id=#{id}")
	@Results(id = "cardRM", value = {
			@Result(id = true, property = "id", column = "id"),
			@Result(property = "number", column = "number"),
			@Result(property = "studentId", column = "studentId"),
			@Result(property = "brief", column = "brief"),
	})
	public Card selectById(Integer id);

	@Select("select * from card where id=#{id}")
	@Results(id = "cardStuRM", value = {
			@Result(id = true, property = "id", column = "id"),
			@Result(property = "number", column = "number"),
			@Result(property = "studentId", column = "studentId"),
			@Result(property = "brief", column = "brief"),
			@Result(property = "stu", column = "brief", javaType = Student.class)
	})
	public CardStudentDO selectCardStudent(Integer id);

	@Insert("insert into card(number,studentId,brief)values(#{number},#{studentId},#{brief})")
	public int insert(Card card);
}




