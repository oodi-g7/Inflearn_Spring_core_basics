package hello.core.autowired;

import hello.core.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class AutowiredTest {

    @Test
    void AutowiredTest(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
    }

    static class TestBean {
        
        // 호출 안됨
        @Autowired(required = false)
        public void setNoBean1(Member noBean1){
            // Member 클래스는 스프링 컨테이너가 관리하지 않는 클래스
            // 따라서 @Autowired를 해놔도 컨테이너에 Member빈이 없으므로 아무것도 주입되지 않음
            // 기본값인 required = true 상태에서 테스트 실행시 'NoSuchBeanDefinitionException' 발생
            System.out.println("noBean1 = " + noBean1);
        }
        
        // null 호출
        @Autowired
        public void setNoBean2(@Nullable Member noBean2){
            System.out.println("noBean2 = " + noBean2);
        }
        
        // Optional.empty 호출
        @Autowired
        public void setNoBean3(Optional<Member> noBean3){
            System.out.println("noBean3 = " + noBean3);
        }
    }
}
