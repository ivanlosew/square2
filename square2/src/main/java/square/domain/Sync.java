package square.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Sync {
    @Id
    private Integer id;
    private String value;

    public Sync() {
    }

    public Sync(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
