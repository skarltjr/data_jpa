package study.data_jpa.repository;

import study.data_jpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class MemberRepositoryImpl implements MemberRepositoryCustom{
/** 중요한 규칙이 하나 있는데 바로 MemberRepositoryImpl 이렇게 멤버레퍼지토리 뒤에 impl을 붙여준 이름을 사용해야한다 구현체*/


    @PersistenceContext
    private EntityManager em; //만약 직접 jpa이용할려고한다면


    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m").getResultList();
    }

    //이렇게 원하는 걸 직접 짠 다음에 다시 기존 멤버리포지토리 인터페이스에서 extends 추가해준다
}
