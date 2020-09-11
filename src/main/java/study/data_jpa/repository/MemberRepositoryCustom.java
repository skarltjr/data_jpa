package study.data_jpa.repository;

import study.data_jpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    /**  만약에 스프링데이터jpa 말고 따로 특정한 기능의 레퍼지토리가 필요한 경우 기존 레퍼지토리로 구현할려면
     * 엄청 많은걸 다 구현해야해서 불가 이렇게 인터페이스로 따로 만들어놓고 직접 구현하는 클래스도 만들어야한다
     *
     * 이런거 말고도 그냥 하던대로 @Repository로 하나 더 만들어서 사용해도 된다 특히 동적쿼리같은거 쿼리dsl쓸 때
     * */
    List<Member> findMemberCustom();
}
