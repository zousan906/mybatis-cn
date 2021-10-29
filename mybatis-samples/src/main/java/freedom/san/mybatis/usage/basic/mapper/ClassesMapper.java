package freedom.san.mybatis.usage.basic.mapper;

import java.util.List;

import freedom.san.mybatis.usage.basic.domain.Classes;

/**
 * @Entity freedom.san.mybatis.domain.Classes
 */
public interface ClassesMapper {
	public List<Classes> selectAll();
	public void  update(Classes classes);
	public void  insert(Classes classes);
	public void delete(int id);
}




