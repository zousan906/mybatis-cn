package freedom.san.mybatis.usage.mapper;

import java.util.List;

import freedom.san.mybatis.domain.Student;
import freedom.san.mybatis.usage.provider.StudentProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

/**
 * @Entity freedom.san.mybatis.domain.Student
 */
@Mapper
public interface StudentMapper {


	// results 对应XML resultMap  id 是次RM 的ID 可以供其他查询使用
	@Results(id = "studentRM", value = {
			@Result(property = "id", column = "id", id = true),
			@Result(property = "name", column = "name"),
			@Result(property = "age", column = "age"),
			@Result(property = "sex", column = "sex"),
			@Result(property = "cid", column = "cid"),
			@Result(property = "cardid", column = "cardId")
	})
	@Select({"select *  ", "from student ", "where id=#{id}"})
	public Student selectAllById(@Param("id") Integer id);

	@Select("Select * from student where cid=#{cid}")
	@ResultMap("studentRM")
	public List<Student> selectAllByCid(@Param("cid")Integer cid);

	@Select("Select * from student")
	// resultMap 可以引用已经定义的RM, 包括XML和 @Results 中定义的RM
	@ResultMap("studentRM")
	public List<Student> selectAll();


	@Insert("insert into student(name,age,sex,cid,cardId)values(#{name},#{age},#{sex},#{cid},#{cardid})")
	public int insert(Student student);

	@Insert("insert into student(name,age,sex,cid,cardId)values(#{name},#{age},#{sex},#{cid},#{cardid})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int insertGetKey(Student student);


	@Insert("insert into student(name,age,sex,cid,cardId)values(#{id},#{name},#{age},#{sex},#{cid},#{cardid})")
	@SelectKey(statement = "select max(id) + 2 from student", keyProperty = "id", resultType = Integer.class, before = true)
	public int insertGetSelectKey(Student student);

	@Update("update student set age=#{age} where id=#{id}")
	public int update(Student student);

	@Delete("delete from student where id=#{id}")
	public int delById();


	//provider
	//@SelectProvider
	//@InsertProvider
	//@UpdateProvider
	//@DeleteProvider

	@SelectProvider(type = StudentProvider.class, method = "selectById")
	@ResultMap("studentRM")
	public Student selectById(Integer id);


	//TODO @Results 还差 级联映射配置的case.
	//TODO @select  还差 <scripts/> 动态语句的case
	//TODO @lang    用途
}




