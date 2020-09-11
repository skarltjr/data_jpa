package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember()
    {
        /**사실은 jpa의 모든 데이터변경 save같은거는 다 트랜잭션안에서 해야하는데 스프링데이터 jpa는 이미
         * 레퍼지토리 안에 이걸 구현해놨다. 그래서 여기 없는거다 트랜잭션 */

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);// 스프링데이터jpa 내부에 다 구현되어있다
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        Assertions.assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    public void basicCRUD()
    {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        //딘건 검증
        Assertions.assertThat(findMember1).isEqualTo(member1);

        //리스트 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long count2 = memberRepository.count();
        Assertions.assertThat(count2).isEqualTo(0);
    }

    /**
     * 멤버 jpa레퍼지토리 테스트를 그대로 옮겼는데도 실행이된다
     * ★★ 스프링데이터jpa를 이용하면 직접구현없이 구현된 내용을 편리하게 사용할 수 있다.
     * jpa를 이용해서  미리 구현한 내용을 사용하는 것이기때문에 당연히 jpa부터 잘 알아야한다
     * */

    @Test
    public void findByUsernameAndAgeGreaterThan()
    {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> aaa = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        Assertions.assertThat(aaa.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(aaa.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testQuery()
    {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Assertions.assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUsernameList()
    {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> username = memberRepository.findUsername();
        Assertions.assertThat(username.size()).isEqualTo(2);
    }

    @Test
    public void findMemberDto()
    {
        Team team=new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        memberRepository.save(member1);
        member1.setTeam(team);


        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println(dto);
        }
    }

    @Test
    public void findByNames()
    {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> names = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member name : names) {
            System.out.println(name);
        }
    }

    @Test
    public void returnType()
    {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> listByUsername = memberRepository.findListByUsername("AAA");
        //★★ 컬렉션은 null이 아니다 절대 빈 컬렉션을 반환해준다
        Member aaa = memberRepository.findMemberByUsername("AAA");
        //★ 그래서 주의해야한다 컬렉션은 결과가없으면 빈 컬렉션돌려주는데 단건은 그냥 null 리턴해버린다  ->그래서 옵셔널써라
        Optional<Member> aaa1 = memberRepository.findOptionalByUsername("AAA");
        //만약 단건인데 값이 2개이상이면 옵셔널이든 아니든 예외가터진다

    }

    /**    ★★★ 가장 중요할 수 있는 테스트 */
    @Test
    public void paging()
    {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5", 10));

        int age=10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        //인터페이스로만  / 0~3까지의 페이지를 가져오도록 구현체와 심지어 유저네임 내림차순으로 소팅까지가능 이걸 파라미터로 넣어주면 끝
        //0부터 3까지 그리고 멤버엔티티의 유저네임을 내림차순으로 정렬하는 것을
        //멤버레퍼지토리 findByAge매써드로 파라미터로 받는 age와 동일한 모든 데이터 가져와서 실행
        //3개씩 가져오니까 만약에 총데이터가 99개면 33페이지가 되는것 -> 페이징은 0부터다 주의 그치만 99개면 어차피 0부터 99개라 상관x
        
        
        //when
        Page<Member> page = memberRepository.findByAge(age,pageRequest);
        //반환타입을 페이지로 받으면 페이징뿐만아니라 토탈카운트 쿼리도 날려서 가져옴
        //만약에 더보기 같은 기능으로 바꾸고싶다면 레퍼지토리와 위 식을  page가 아닌 slice타입으로 리턴받으면된다.
        /**당연히 이거 그대로 api로 돌려보내면 큰일난다 member는 엔티티다 */
        Page<MemberDto> result = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        //그럼 이걸 api로 반환하면된다 !!!★


        //then
        List<Member> content = page.getContent();
        long elements = page.getTotalElements(); // 토탈카운트
        //아주 유용하다 

        for (Member member : content) {
            System.out.println(content);
        }
        System.out.println(elements);

        //★ 페이지 번호까지 가능하다
        int pageNumber = page.getNumber();
        //총페이지갯수도 가능 지금 3개씩 끊었는데 총 5개니까 3 ,2  -> 2페이지
        int totalPages = page.getTotalPages();

    }

    @Test //bulk
    public void bulkUpdate()
    {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",20));
        memberRepository.save(new Member("member3",30));
        memberRepository.save(new Member("member4",40));
        memberRepository.save(new Member("member5",50));

        //치명적인 오류가 생길 수 있는데 jpa는 영속성컨텍스트를 관리. 즉 벌크연산은 db를 업데이트하는데 jpa영속성 컨텍스트를
        //clear를 안하면 flush로 이미 보낸 데이터값은 +1이 되지만 clear를 안해주면  다시 찾을 때 영속성컨텍스트에 우선 검색을 하니까
        //거기는 +1하기 전 데이터들이 남아있으니까 그걸 가져온다ㅣ

        //when
        int i = memberRepository.bulkAgePlus(20);
        //jpql은 쿼리나가기전 일단 flush로 db에 보낸다
        //em.flush();
        em.clear();  //★ 이것도 인터페이스에서 modifying옵션으로 clear 추가하면 알아서해준다

        //then
        Assertions.assertThat(i).isEqualTo(4);
    }

    @Test
    public void findMemberLazy()  // fetch join 을 스프링데이터jpa에서는?
    {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();
        //when

        List<Member> all = memberRepository.findAll();
        //주의할 점은 당연히 lazy로딩은 여기서는 team을 사용! 할 때 일어난다
        for (Member member : all) {
            System.out.println(member.getTeam().getName());
        }
    }

    @Test
    public void queryHint()
    {
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

       /* Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setUsername("member2");
        em.flush();*/

        //이렇게 하면 어찌되었든 변경감지를 하기위해선 원본과 변경된 데이터 2개가 있어야만 당연히 비교가 가능해서
        //2개를 갖고있게되는데 그럼 당연히 메모리 더 먹는다
        //근데 ★ 이렇게 set처럼 변경안하고 그냥 갖고오기만해도 내부적으론 원본을 만들어놔서 그래도 2개
        //이 때 나는 정말 오로지 읽기용으로만 변경은 죽어도 안할건데 이러면 손해니까 그럴 때 사용하는것이 hint

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("123"); // -> readOnly라서 안된다.
        em.flush();

    }

    //따로 레퍼지토리 구현한 것 테스트
    @Test
    public void callCustom()
    {
        List<Member> memberCustom = memberRepository.findMemberCustom();

    }

    @Test
    public void nativeQuery()
    {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();
        Member member11 = memberRepository.findByNativeQuery("member1");
        System.out.println(member11);

    }
}