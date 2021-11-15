package freedom.san.mybatis.usage.basic.entity;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.ibatis.type.Alias;

/**
 *
 * @TableName card
 */
@Alias("IDCard")
public class Card implements Serializable {
	/**
	 * id
	 */
	private Integer id;

	/**
	 * 学生证id
	 */
	private Integer number;

	/**
	 * 学生id
	 */
	private Integer studentId;

	/**
	 * 简要信息
	 */
	private String [] brief;


	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 学生证id
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * 学生证id
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * 学生id
	 */
	public Integer getStudentId() {
		return studentId;
	}

	/**
	 * 学生id
	 */
	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public String[] getBrief() {
		return brief;
	}

	public void setBrief(String[] brief) {
		this.brief = brief;
	}


	@Override
	public String toString() {
		return "Card{" +
				"id=" + id +
				", number=" + number +
				", studentId=" + studentId +
				", brief=" + Arrays.toString(brief) +
				'}';
	}
}