package freedom.san.mybatis.domain;

import java.io.Serializable;

/**
 * 
 * @TableName card
 */
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
    private Integer studentid;

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
    public Integer getStudentid() {
        return studentid;
    }

    /**
     * 学生id
     */
    public void setStudentid(Integer studentid) {
        this.studentid = studentid;
    }
}