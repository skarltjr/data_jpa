package study.data_jpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberTest {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity()
    {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",20,teamA);
        Member member3 = new Member("member3",30,teamB);
        Member member4 = new Member("member4",40,teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush(); // 확실하게 하기위해 영속성컨텍스트에 모아둔 내용 플러쉬로 디비에 보낸다
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team = "+member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws Exception {
        //given
        Member member1 = new Member("member1");
        memberRepository.save(member1);//이 때 prepersist가 발생
        Thread.sleep(100);

        member1.setUsername("member2");
        em.flush();// 이 떄 preupdate발생
        em.clear();

        //when
        Member member = memberRepository.findById(member1.getId()).get();

        //then
        System.out.println("created " + member.getCreatedDate());
        System.out.println("updeated "+ member.getLastModifiedDate());
        System.out.println("createdBy " + member.getCreatedBy());
        System.out.println("modiBy " + member.getLastModifiedBy());
    }
}