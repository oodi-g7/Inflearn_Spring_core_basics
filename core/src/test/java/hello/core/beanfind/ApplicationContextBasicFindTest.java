package hello.core.beanfind;

import hello.core.AppConfig;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationContextBasicFindTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    
    @Test
    @DisplayName("빈 이름으로 조회")
    void findBeanByName(){
        // MemberService.class처럼 인터페이스를 조회하면 해당 인터페이스의 구현체를 대상으로 조회
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        
        // 검증은 Assertions로 하면 됨
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }
    @Test
    @DisplayName("이름 없이 타입으로만 조회")
    void findBeanByType(){
        MemberService memberService = ac.getBean(MemberService.class);

        // 검증은 Assertions로 하면 됨
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("구체 타입으로 조회")
    void findBeanBy2(){
        // 스프링은 컨테이너에 빈을 생성할때, 메소드의 이름과 반환타입을 컨테이너에 저장하는데
        // AppConfig에 memberService()를 보면 메소드 이름은 memberService이고, 반환타입이 MemberServiceImpl이므로
        // 이렇게 빈에 등록되어있는 객체이라면 구현체 자체를 검색해도 ㄱㅊ. 하지만 좋은 방법은 아님.(역할과 구현 구분, 역할에 의존하기! 이 방식은 구현에 의존)
        // 그런이유로 해당 구현체의 상위 타입인 인터페이스(MemberService.class)를 사용해왔던 것!
        // 혹시나 필요한 경우가 생겼을때만 사용. 그렇지 않다면 역할에 의존하는 코드 작성하기
        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);

        // 검증은 Assertions로 하면 됨
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    // 테스트는 항상 실패 테스트도 만들어야 한다 !
    @Test
    @DisplayName("빈 이름으로 조회X")
    void findBeanByNameX(){
        // 존재하지 않는 빈을 조회하면 어떻게 될까?
//        ac.getBean("xxxxx" , MemberService.class);
//        MemberService xxxxx = ac.getBean("xxxxx", MemberService.class);
        // 위 코드로 테스트 실행하면 존재하지 않는 빈을 조회해서 테스트 자체가 실패가 됨.
        // 그럼 테스트가 실패하지 않게 테스트 로직으로 검증하는 법을 알아보자.

        assertThrows(NoSuchBeanDefinitionException.class, // (2) 해당 예외가 터져야 한다.
                () -> ac.getBean("xxxxx", MemberService.class)); // (1) 이 람다식을 실행하면
        // (3) 예외 터져야 테스트 성공, 그렇지 않으면 테스트 실패

        // 어떤 예외가 발생하는지를 테스트하기 위해선 위와같은 코드를 사용하면 된다.
    }
}
