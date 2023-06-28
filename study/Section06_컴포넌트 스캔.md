# 1. 컴포넌트 스캔과 의존관계 자동 주입 시작하기
- 지금까지 배운 스프링 빈 등록 방법 : 자바코드의 @Bean 어노테이션 또는 XML의 bean 태그
- 만약 등록해야할 빈이 기하급수적으로 늘어난다면, 이와같은 방법은 관리가 어려워짐.
- 따라서 스프링은 설정 정보 없이 자동으로 스프링 빈을 등록하는 **컴포넌트 스캔** 기능을 제공한다.
- 또한 의존관계도 자동으로 주입하는 **@Autowired** 기능도 제공한다.

## AutoAppConfig.java
```
@Configuration
@ComponentScan(
    excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {

}
```
- 컴포넌트 스캔을 사용하기 위해선 @ComponentScan을 설정 정보에 붙여준다.
    - @ComponentScan : @Component 어노테이션이 붙은 클래스들을 찾아 자동으로 스프링 빈으로 등록시켜줌
    - 이전에 AppConfig에다 @Configuration 어노테이션을 붙여준 것처럼
- 기존의 AppConfig와는 다르게 @Bean으로 등록한 클래스가 하나도 없다.
- 컴포넌트 스캔을 사용하면 @Configuration이 붙은 설정 정보도 자동으로 스프링 빈에 등록된다. 때문에 AppConfig, TestConfig 등 @Configuration이 붙은 앞서 만들어둔 설정 정보도 함께 등록되고, 실행되어 버린다. 따라서 excludeFilters를 이용하여 설정정보는 컴포넌트 스캔 대상에서 제외한다.
    - 참고로 보통 설정 정보를 컴포넌트 스캔 대상에서 제외하는 경우는 드물다. 현재는 기존 공부내용을 남기기 위한 조치일 뿐
    - @Configuration이 컴포넌트 스캔의 대상이 된 이유는 @Configuration 소스코드를 열어보면 @Component 어노테이션이 붙어있기 때문
    - excludeFilters : 컴포넌트 스캔 중 뺄 것을 지정
        - type : 어노테이션 타입으로 지정
        - classes : configuration 어노테이션이 붙었으면 제외 대상

## @Component
- 컴포넌트 스캔의 대상이 되는 클래스에 @Component 어노테이션을 붙여준다.
```
// MemoryMemberRepository
@Component
public class MemoryMemberRepository implements MemberRepository { }


// RateDiscountPolicy
@Component
public class RateDiscountPolicy implements DiscountPolicy { }


// MemberServiceImpl
@Component
public class MemberServiceImpl implements MemberService { 

    private final MemberRepository memberRepository;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
}
```
- 이전 AppConfig에서는 @Bean으로 직접 설정 정보를 작성했고 의존관계도 직접 명시했다. 이제는 그런 설정 정보 자체가 없으므로, 의존관계 주입도 해당 클래스 내에서 해결해야 한다.
- @Autowired는 의존관계를 자동으로 주입해준다.

## @Autowired
```
@Component
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy){
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```
- @Autowired를 사용하면 생성자에서 여러 의존관계도 한번에 주입받을 수 있다.
    - MemberRepository, DiscountPolicy

## @ComponentScan, @Configuration, AutoAppConfig 정상작동 테스트 : AutoAppConfigTest.java
```
public class AutoAppConfigTest {

    @Test
    void basicScan(){
        // 설정정보로 AutoAppConfig 클래스를 넘겨줌
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class);

        // 스프링 빈으로 등록된 MemberSerivce타입 객체 인스턴스를 memberService 변수에 할당
        MemberService memberService = ac.getBean(MemberService.class);

        // 스프링빈으로 등록된 객체와 MemberService.class가 동일한 객체인지 확인
        assertThat(memberService).isInstanceOf(MemberService.class);
    }
}
```
- 테스트 결과
<img src="./image/sec06_1.png">
    - 컴포넌트 스캔은 물론, 의존관계 자동 주입까지 정상 동작하는 것을 확인가능

## 정리
<img src="./image/sec06_2.png">
<img src="./image/sec06_3.png">
<img src="./image/sec06_4.png">

# 2. 탐색 위치와 기본 스캔 대상
# 3. 필터
# 4. 중복 등록과 충돌