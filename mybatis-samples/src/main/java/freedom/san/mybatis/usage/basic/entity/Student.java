package freedom.san.mybatis.usage.basic.entity;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

/**
 * 
 * @TableName student
 */
@Alias("Student")
public class Student implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别 1 男 0 女
     */
    private Integer sex;

    /**
     * 班级id
     */
    private Integer cid;

    /**
     * 学生证id
     */
    private Integer cardid;

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
     * 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 性别 1 男 0 女
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 性别 1 男 0 女
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 班级id
     */
    public Integer getCid() {
        return cid;
    }

    /**
     * 班级id
     */
    public void setCid(Integer cid) {
        this.cid = cid;
    }

    /**
     * 学生证id
     */
    public Integer getCardid() {
        return cardid;
    }

    /**
     * 学生证id
     */
    public void setCardid(Integer cardid) {
        this.cardid = cardid;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", cid=" + cid +
                ", cardid=" + cardid +
                '}';
    }
}