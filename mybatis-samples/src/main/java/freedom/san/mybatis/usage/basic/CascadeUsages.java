package freedom.san.mybatis.usage.basic;

import java.util.List;

import freedom.san.mybatis.MybatisApplication;
import freedom.san.mybatis.usage.basic.entity.Student;
import freedom.san.mybatis.usage.basic.mapper.ClassesMapper;
import freedom.san.mybatis.usage.basic.model.ClassStudentDO;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 级联使用
 */
@FixMethodOrder(value = MethodSorters.JVM)
public class CascadeUsages extends MybatisApplication {
	static final Logger log = LoggerFactory.getLogger(CascadeUsages.class);

	static SqlSession sqlSession;
	static ClassesMapper mapper;

	@BeforeClass
	public static void init() {
		log.info("before class");
		buildSqlSessionFactory("freedom/san/mybatis/usage/basic/mybatis-basic.xml");
		sqlSession = getSessionFactory().openSession();
		mapper = sqlSession.getMapper(ClassesMapper.class);
	}

	@Before
	public void before() {
	}


	@Test
	public void selectStudentByClass() {
		List<ClassStudentDO> classStudentDOS = mapper.selectStudents(1);
		for (ClassStudentDO classStudentDO : classStudentDOS) {
			log.info("Student:{}", classStudentDO);
		}
	}

	@Test
	public void selectAllStudent() {
		List<ClassStudentDO> classStudentDOS = mapper.selectAllStudents();
		for (ClassStudentDO classStudentDO : classStudentDOS) {
			log.info("Student:{}", classStudentDO);
		}
	}

	@Test
	public void selectAllStudentLazy() {
		List<ClassStudentDO> classStudentDOS = mapper.selectAllStudentsLazy();
		List<Student> students = classStudentDOS.get(0).getStudents();
		log.info("Lazy Student:{}", students);
//		for (ClassStudentDO classStudentDO : classStudentDOS) {
//			log.info("Lazy Student:{}", classStudentDO);
//			break;
//		}
	}

	@Test
	public void selectAllStudentsCascade() {
		List<ClassStudentDO> classStudentDOS = mapper.selectAllStudentsCascade();
		log.info("Cascade Student:{}", classStudentDOS.size());
		for (ClassStudentDO classStudentDO : classStudentDOS) {
			log.info("Cascade Student:{}", classStudentDO);
		}
	}

	@After
	public void commit() {
		sqlSession.commit();
		log.info("commit");
	}

	@AfterClass
	public static void afterClass() {
		sqlSession.close();
		log.info("close");
	}

}
