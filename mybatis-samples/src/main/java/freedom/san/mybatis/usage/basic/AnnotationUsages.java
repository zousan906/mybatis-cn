package freedom.san.mybatis.usage.basic;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

import freedom.san.mybatis.MybatisApplication;
import freedom.san.mybatis.domain.Student;
import freedom.san.mybatis.usage.basic.entity.Card;
import freedom.san.mybatis.usage.basic.mapper.CardMapper;
import freedom.san.mybatis.usage.basic.mapper.StudentMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(value = MethodSorters.JVM)
public class AnnotationUsages extends MybatisApplication {
	static final Logger log = LoggerFactory.getLogger(AnnotationUsages.class);

	StudentMapper mapper;

	CardMapper cardDao;

	SqlSession sqlSession;

	@BeforeClass
	public static void init() {
		buildSqlSessionFactory("freedom/san/mybatis/usage/basic/mybatis-basic.xml");
	}

	@Before
	public void before() {
		sqlSession = getSessionFactory().openSession();
		mapper = sqlSession.getMapper(StudentMapper.class);
		cardDao = sqlSession.getMapper(CardMapper.class);
		log.info("before");
	}


	@After
	public void commit() {
		sqlSession.commit();
		sqlSession.close();
		log.info("commit");
	}

	@Test
	public void insertStudent() throws UnsupportedEncodingException {
		Student student = genStudent();
		mapper.insert(student);



		log.info("insert student:{}", student);
	}


	@Test
	public void selectAllById() {
		int id = new Random().nextInt(100) + 1;
		Student student = mapper.selectAllById(id);
		log.info("student with id:{} :{}", id, student);
	}

	@Test
	public void selectAll() {
		List<Student> students = mapper.selectAll();
		int index = 1;
		for (Student student : students) {
			log.info("first 10 student:index:{} : {}", index, student);
			if (++index > 10) break;
		}
	}

	@Test
	public void insertGetKey() throws UnsupportedEncodingException {
		Student student = genStudent();
		mapper.insertGetKey(student);
		log.info("student:{}", student);

		Card card = new Card();
		card.setNumber(student.getCardid());
		card.setStudentId(student.getId());
		card.setBrief(new String[] {student.getName(), String.valueOf(student.getAge()), String.valueOf(student.getSex())});
		cardDao.insert(card);
	}

	@Test
	public void insertGetSelectKey() throws UnsupportedEncodingException {
		Student student = genStudent();
		mapper.insertGetKey(student);
		log.info("student:{}", student);
	}

	@Test
	public void update() {

	}

	@Test
	public void delById() {
	}

	@Test
	public void selectById() {
		int id = new Random().nextInt(100) + 1;
		Student student = mapper.selectAllById(id);
		log.info("student with id:{} :{}", id, student);
	}


	private Student genStudent() throws UnsupportedEncodingException {
		Student student = new Student();
		student.setAge(new Random().nextInt(99) + 1);
		student.setSex(new Random().nextInt(1) + 1);
		student.setName(getChinese() + getChinese());
		student.setCid(new Random().nextInt(5) + 1);
		String id = student.getCid() + String.valueOf(new Random().nextInt(10) + 10000) + new Random().nextInt(10) + 1;
		student.setCardid(Integer.valueOf(id));
		return student;
	}

	public static String getChinese()
			throws UnsupportedEncodingException {
		String str = null;
		int highpos, lowpos;
		Random random = new Random(new Random().nextLong());
		highpos = (176 + Math.abs(random.nextInt(39)));
		lowpos = (161 + Math.abs(random.nextInt(93)));
		byte[] bb = new byte[2];
		bb[0] = new Integer(highpos).byteValue();
		bb[1] = new Integer(lowpos).byteValue();
		//String(byte[] bytes, Charset charset)
		//通过使用指定的 charset 解码指定的 byte 数组，构造一个新的 String。
		str = new String(bb, "GBK");
		return str;
	}
}
