package study.data_jpa.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;


import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

//ctrl p 파라미터
public interface MemberRepository extends JpaRepository<Member,Long>,MemberRepositoryCustom
{
    //List<Member> findByname(String name); 만약에 이렇게 공통파트가 아니라
    //특정 이름곽 같은 도메인에 따라 바뀔 수 있는 내용을 추가로 좀 구현해서 사용하고 싶다면?
    //따로 구현할려면 절대 못한다 수많은걸 오버라이딩해야해서
    //그런데 사실 위처럼그냥써도 된다.

    /***
     * ★ 바로 쿼리 매서드 ★
     * memberJpaRepository에서 구현한 findByUsernameAndAgeGreaterThan를 스프링데이터jpa를 이용하면
     */
    List<Member> findByUsernameAndAgeGreaterThan(String name, int age);
    //한 줄로 끝내버린다.
    /**
     * 어떻게 가능하냐 = findBy다음에 그냥 Username으로 나온 유저네임은 m.username= :username랑 똑같이 이퀄로 나오고
     * And 는 그대로 and조건
     * age는 뒤에 greater than이랑 함께 age>로 쿼리가 나간다
     * ★그래서 더 주의해야할 점이 이름이 관례기때문에 이름이 좀 바뀌면 작동안한다 예를들어 findByUsername2처럼하면 x
     * LIMIT: findFirst3, findFirst, findTop, findTop3 이런것도 있다
     * 진짜 큰 장점은 누군가가 엔티티를 건드리면 작동 x
     *
     * 스프링 데이터 jpa는 복잡한 쿼리에는 x -> 다른방법    (named쿼리는 사실상 안쓴다 ;; )
     * */



    /**
     * ★★ 위의방법은 조건이 길어지면 사실상 답이없다
     * 최고의 방법
     *
     * 결국 간단간단한 귀찮은 쿼리는 위의 쿼리매서드를 이용하다가 복잡한 쿼리를 아래처럼 풀어가면서 사용하면
     * 스프링데이터jpa good
     * 다만 이건 다 정적쿼리 --> 동적쿼리는 쿼리dsl이용하자
     */

    @Query("select m from Member m where m.username= :username and m.age =:age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
    

    /**
     * 엔티티가 아니라 단순한 값 타입embedded나 dto조회는?
     * */
    @Query("select m.username from Member m")
    List<String> findUsername();

    //dto인 경우
    @Query("select new study.data_jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();
    //dto 직접조회처럼 마치 dto를 생성자로 만들어서 객체돌려주듯이

    //컬렉션도 다 가능
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    //다양한 반환타입도 지원
    List<Member> findListByUsername(String username);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalByUsername(String username);

    /** ★★★*/
    //스프링데이터 jpa 필살기 페이징,소팅까지 다된다.
    // ★★실무에서중요 = @Query는 예시로 결국 고민인 부분은 데이터가져오는게 아니라 카운트 쿼리다. 매번 전부 갯수를 세야하니까. 그래서 필요한경우에 카운트쿼리를 따로 가능
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    //pageable 인터페이스   -> 매서드이름으로 쿼리생성하는 방식으로 파라미터로받은 age equal 쿼리를 대상으로 !


    //스프링데이터 jpa에서 벌크연산
    @Modifying(clearAutomatically = true) // excute역할은 해서 필수 clea~는 알아서 em.clear하겠다는 내용
    @Query("update Member m set m.age=m.age+1 where m.age>= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m join fetch m.team")
    List<Member> findMemberFetchJoin();     //fetch join 은 그냥 쓰던대로를 @Query로만 해주면된다 복잡하면 그냥 jpql쓴다.

    /**근데 이것마저도 스프링데이터 jpa가 도와준다*/
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
    //이렇게 해주면 멤버엔티티를 대상으로 findAll할 때 join fetch로 team도 가져온다

    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly",value = "true"))
    Member findReadOnlyByUsername(String username);

    //이거는 쿼리 건드리지말아라 하는 lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    //@Query~~~
    List<Member> findLockByUsername(String username);
    //-- > select for update 라는 sql과 같은것  but 신중하게 생각해야한다


    //혹시 필요할 수 있는 네이티브 쿼리
    @Query(value = "select * from member where username =?",nativeQuery = true)
    Member findByNativeQuery(String username);
    //사실 꼭 필요한 경우말고 한계가 많다 그럴빠엔 그냥 custom만들어서 쓰는게 당연히좋다

}
