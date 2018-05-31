package top.sqmax.dto;

/**
 * Created by SqMax on 2018/5/29.
 */
public class Role {
    long id;
    String name;
    String note;

    public Role(long id, String name, String note) {
        this.id = id;
        this.name = name;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
