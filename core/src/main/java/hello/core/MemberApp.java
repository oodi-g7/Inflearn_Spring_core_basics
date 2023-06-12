package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// Member JAVA TEST
public class MemberApp {
    public static void main(String[] args) {
        // 스프링은 모든 것이 ApplicationContext에서 시작함(컨테이너에 등록된 모든 객체들을 관리해줌). 이것을 Spring컨테이너라고 보면 됨.
        // AppConfig는 어노테이션을 기반으로 Config를 하고 있으므로 AnnotationConfigApplicationContext를 new 해준다. 그리고 파라미터 값으로 AppConfig를 넣어주기
        // 이제 AppConfig에 있는 환경설정 정보들을 스프링 컨테이너 등록시키고, 등록된 객체들을 스프링이 관리해준다.
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        // 이전 코드를 보면,
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();
        //이렇게 new AppConfig해서 직접 찾아왔다. 이제는 스프링 컨테이너를 통해서 찾아와야한다.
        //getBean([이름],[타입])
        //@Bean을 붙어 스프링 컨테이너에 저장할때 해당 메소드의 이름을 키값으로 저장하기때문에 getBean()의 파라키터값으로 이름, 반환타입을 적는것!
        //실행해보면 콘솔에 appConfig와 더불어 appConfig내에 @Bean어노테이션을 붙여 뒀던 메소드들(memberService, memberRepository, orderService, discountPolicy)이 빈으로 생성되었다는 로그를 확인할 수 있다.
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);


        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + member.getName());
        System.out.println("find member = " + findMember.getName());
    }
}
