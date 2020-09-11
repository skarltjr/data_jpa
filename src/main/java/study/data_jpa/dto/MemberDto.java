package study.data_jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;

    //dto는 엔티티를 파라미터로 받을 수 있다. 당연히  위 필드로 들어가는건 절대 x ->그래서 걍 멤버받는 생성자만들면 좀 더ㅁ편함
}
