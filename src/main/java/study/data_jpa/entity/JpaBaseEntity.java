package study.data_jpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class JpaBaseEntity {

    //실무에선 당연히 데이턱 언제, 누가 변경 등 알아야한다
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist()
    {
        LocalDateTime now = LocalDateTime.now();
        createdDate=now;
        updatedDate=now;
    }

    @PreUpdate
    public void preUpdate()
    {
        updatedDate=LocalDateTime.now();
    }

    //이러고 보통 사용할 때는 엔티티 여기서는 멤버 팀에 extends로

    /** 근데 스프링데이터 jpa 는 이것도 해준다 그게 baseEntity
     * 일단 application에 추가 EnableJpaAuditing
     *
     * */
}
