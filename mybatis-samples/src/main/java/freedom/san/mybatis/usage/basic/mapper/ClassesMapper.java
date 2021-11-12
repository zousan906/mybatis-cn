package freedom.san.mybatis.usage.basic.mapper;

import java.util.List;

import freedom.san.mybatis.usage.basic.entity.Classes;
import freedom.san.mybatis.usage.basic.model.ClassStudentDO;

/**
 * @Entity freedom.san.mybatis.domain.Classes
 */
public interface ClassesMapper {
	public List<Classes> selectAll();
	public void  update(Classes classes);
	public void  insert(Classes classes);
	public void delete(int id);

	public List<ClassStudentDO> selectStudents(int id);
	public List<ClassStudentDO> selectAllStudents();
	public List<ClassStudentDO> selectAllStudentsLazy();
	public List<ClassStudentDO> selectAllStudentsCascade();

}




