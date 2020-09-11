package study.data_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.data_jpa.entity.Team;

public interface TeamRepository extends JpaRepository<Team,Long> {
}

// ★ 인터페이스만 잘 잡으면 스프링 데이터jpa가 알아서 구현클래스를 프록시객체로 만들어서 주입해준다.
