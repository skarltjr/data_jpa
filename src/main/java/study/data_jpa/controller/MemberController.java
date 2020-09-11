package study.data_jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }
    //그러니까 스프링이 파라미터로 받은 id로 그냥 바로 멤버를 찾아서  그냥 이런게 있다만 알고 실제론 거의 사용 x

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size=10) Pageable pageable) // 이렇게하면 디폴트 10개씩
    {
        Page<Member> page = memberRepository.findAll(pageable);
        //그리고 엔티티 외부노출은 당연히 안되니까
        Page<MemberDto> dtos = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
        return dtos;
    }
    //그러면 api로 보낼때 http://localhost:8080/members?page=0->1->2->3 해보면 기본 20개씩 짤라서 나온다
    /**http://localhost:8080/members?page=0&size=3  까지 해주면  한 페이지에 컨텐트3개씩
     * http://localhost:8080/members?page=0&size=3&sort=id,desc 로 하면id로 내림차순정렬해서도 다 가능
     * */


    //데이터없으니까 그냥 하나 넣어주기용으로
    @PostConstruct
    public void init() {
        for(int i=0;i<100;i++)
        {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
