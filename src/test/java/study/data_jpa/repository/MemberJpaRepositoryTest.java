package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

import java.util.List;


@SpringBootTest
@Transactional//모든 테스트를 포함한 모든 jpa데이터변경은 필수
//@Rollback(false) 테스트끝날때 트랜잭션으로 롤백시키는데 공부할때 데이터확인할 필요있으면 롤백펄스
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember()
    {
        Member member=new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        Assertions.assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        Assertions.assertThat(findMember).isEqualTo(savedMember);
        //아직 이 멤버의 equals를 오버라이드안한 상태여서 디폴트설정으로 가는데
        //jpa는 하나의 트랜잭션안에서 == 비교를할 때 영속성컨텍스트를 보장해줌

    }
    @Test
    public void basicCRUD()
    {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        //딘건 검증
        Assertions.assertThat(findMember1).isEqualTo(member1);

        //리스트 검증
        List<Member> all = memberJpaRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        long count2 = memberJpaRepository.count();
        Assertions.assertThat(count2).isEqualTo(0);

    }
    @Test
    public void findByUsernameAndAgeGreaterThan()
    {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> aaa = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        Assertions.assertThat(aaa.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(aaa.get(0).getAge()).isEqualTo(20);
    }


    @Test
    public void paging()
    {
        //given
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",10));
        memberJpaRepository.save(new Member("member3",10));
        memberJpaRepository.save(new Member("member4",10));
        memberJpaRepository.save(new Member("member5", 10));
        int age=10;
        int offset=0;
        int limit=3;

        //when
        List<Member> byPage = memberJpaRepository.findByPage(age, offset, limit);
        long count = memberJpaRepository.totalCount(10);
        //then
        Assertions.assertThat(byPage.size()).isEqualTo(3);
        Assertions.assertThat(count).isEqualTo(5);
    }

    @Test //bulk
    public void bulkUpdate()
    {
        //given
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",20));
        memberJpaRepository.save(new Member("member3",30));
        memberJpaRepository.save(new Member("member4",40));
        memberJpaRepository.save(new Member("member5",50));

        //치명적인 부분 -> 멤버레퍼지토리 테스트에 설명
        //when
        int i = memberJpaRepository.bulkAgePlus(20);
        //then
        Assertions.assertThat(i).isEqualTo(4);
    }
}