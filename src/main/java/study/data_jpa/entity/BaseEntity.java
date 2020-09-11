package study.data_jpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    //등록자 수정자
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    //근데 등록자 수정자는 이대로쓰면 안된다 어플에서 bean추가

    //보통 시간에 관한건 엔티티에서 다 쓴다 다만 생성 업데이트 누가했는지는 필요없을 수도있어서
    // 실무에선 시간관련 클래스랑 시간 + by모두 있는 클래스 따로 만들어놓고 엔티티가 필요한 내용만 extends해서 쓰면된다.
}
