package freedom.san.mybatis.usage.basic.domain;

import java.io.Serializable;

/**
 * 
 * @TableName classes
 */
public class Classes implements Serializable {
    /**
     * 班级id
     */
    private Integer id;

    /**
     * 班级名称
     */
    private String name;

    private static final long serialVersionUID = 1L;

    /**
     * 班级id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 班级id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 班级名称
     */
    public String getName() {
        return name;
    }

    /**
     * 班级名称
     */
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Classes{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}