package spring.restapi.model;

import javax.persistence.*;

@Entity
@Table(name = "message_list")
public class MessageList {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_cid")
    private Long fromCid;

    @Column(name = "to_cid")
    private Long toCid;

    @Column(name = "content")
    private String content;

    @Column(name = "state")
    private Integer state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromCid() {
        return fromCid;
    }

    public void setFromCid(Long fromCid) {
        this.fromCid = fromCid;
    }

    public Long getToCid() {
        return toCid;
    }

    public void setToCid(Long toCid) {
        this.toCid = toCid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
