# 1. 웹 애플리케이션과 싱글톤
- 스프링은 태생이 기업용 온라인 서비스 기술을 지원하기 위해 탄생
- 대부분의 스프링 애플리케이션은 웹 애플리케이션이다. 물론 웹이 아닌 애플리케이션 개발도 얼마든지 개발 가능
- 웹 애플리케이션은 보통 여러 고객이 동시에 요청을 한다.

## 현재코드 문제점
<img src="./image/sec05_1.png">

```
@Configuration 
public class AppConfig {

    @Bean 
    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }
    @Bean
    public OrderService orderService(){
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
    @Bean
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }
    @Bean
    public DiscountPolicy discountPolicy(){
        return new RateDiscountPolicy();
    }
}
```

- 현재 구현되어 있는 AppConfig같은 경우, 여러 고객이 동시에 memberService()를 요청하게 되면 각 고객에게 return new MemberServiceImpl(memberRepository())을 반환하면서 요청개수만큼의 객체가 생성이 되며, 각 고객에게 반환된 객체는 서로 다른 객체이다.
- 이것이 주는 문제점은 우선 **요청이 올때마다 계속 새로운 객체를 만들어야 한다는 것.** 또한 웹 애플리케이션은 고객의 요청을 끊임없이 받아야 하는데 이대로라면 **JVM메모리에 객체가 계속해서 생성되어 많은 공간을 차지하게 된다는 것**이다.

## 테스트코드로 검증하기
```
public class SingletonTest {
    
    @Test
    @DisplayName("스프링 없는 순수한 DI 컨테이너")
    void pureContainer(){
        AppConfig appConfig = new AppConfig();
        // 1. 조회 : 호출할때마다 객체를 생성
        MemberService memberService1 = appConfig.memberService();
        
        // 2. 조회 : 호출할때마다 객체를 생성
        MemberService memberService2 = appConfig.memberService();
        
        // 3. 참조값이 다른 것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);

        // 4. 검증 memberService1 != memberService2
        // 테스트코드는 3번처럼 눈으로 확인하는 코드가 아니라 자동으로 테스트가 가능한 코드를 작성해야함.
        Assertions.assertThat(memberService1).isNotSameAs(memberService2);
    }
}
```
<img src="./image/sec05_2.png">

- 우리가 만들었던 스프링 없는 순수한 DI컨테이너인 AppConfig는 요청을 할 때 마다 객체를 새로 생성한다.
- 고객 트래픽이 초당 100이 나오면 초당 100개 객체가 생성되고 소멸된다. <U>**→ 메모리 낭비가 심하다.**</U>
- 해결방안은 해당 객체가 딱 1개만 생성되고, 공유하도록 설계하면 된다. <U>**→ 싱글톤 패턴**</U>

# 2. 싱글톤 패턴
- 객체 인스턴스가 현재 자바 JVM안에 단 하나만 있어야 하는 패턴
- 한 JVM(자바서버)안에서는 객체 인스턴스가 딱 하나만 생성되도록 보장하는 패턴. 동일한 객체 인스턴스가 2개 이상 생성될 수는 없음
- 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴
- 그래서 객체 인스턴스를 2개이상 생성하지 못하도록 막아야 한다
    - private 생성자를 사용해서 외부에서 임의로 new 키워드를 사용하지 못하도록 막아야 한다.

## 싱글톤 패턴 적용하기
```
public class SingletonService {

    // 자기자신을 내부에 private으로 가짐 + static이므로 클래스 레벨에 올라가기때문에 딱 하나만 존재하게됨.
    private static final SingletonService instance = new SingletonService();

    // 사용방법
    public static SingletonService getInstance(){
        return instance;
    }
    
    // 밖에서 new SingletonService로 해당 객체를 생성하지 못하도록 기본 생성자 만들기 *private 으로*
    private SingletonService(){

    }

    public void logic(){
        System.out.println("싱긅톤 객체 로직 호출");
    }

    // 애플리케이션 실행시 동작순서
    // 1. 우선 애플리케이션이 실행되면서 SingletonService의 static영역에 new SingletonService()를 실행하여 해당 객체를 생성한 후 변수 instance에 참조를 걸어둠
    // 2. 즉 SingletonService 객체는 현재 static영역에 단 하나만 존재함.
    // 3. instance변수에 담긴 SingletonService객체에 대한 참조값을 사용할 수 있는 방법은 오롯이 getInstance() 이다.
    // 4. 또한 SingletonService를 생성할 수 있는 방법은 전혀 없음. (이미 기본생성자가 있는데 그게 private이므로)
}
```
- 1. static영역에 객체 instance를 미리 하나 생성해서 올려둔다.
- 2. 이 객체 인스턴스가 필요하면 오직 getInstance()메서드를 통해서만 조회할 수 있다. 이 메서드를 호출하면 항상 같은 인스턴스를 반환한다.
- 3. 딱 1개의 객체 인스턴스만 존재해야 하므로, 생성자를 private으로 막아서 혹시라도 외부에서 new키워드로 객체 인스턴스가 생성되는 것을 막는다.

## 테스트코드로 검증하기
```
@Test
    @DisplayName("싱글톤 패턴을 적용한 객체 사용해보기")
    void singletoneServiceTest(){
        // new SingletonService(); -> 컴파일오류발생 : private access

        // 호출할때마다 새로운 객체 생성?
        SingletonService singletonService1 = SingletonService.getInstance();
        SingletonService singletonService2 = SingletonService.getInstance();

        // 참조값 확인
        System.out.println("singletonService1 = " + singletonService1);
        System.out.println("singletonService2 = " + singletonService2);
        
        // 싱글톤 패턴을 적용한 객체이므로, 자바가 뜰때 static으로 이미 생성해둔 객체를 가져다 쓰는 것임
        // 그래서 호출할때마다 새로운 객체를 생성하는 것이 아니라, 이미 만들어둔 객체를 여기저기다 가져다 쓰는 것이므로 서로 동일한 객체임

        // 검증
        // same 와 equal 차이점 알기
        assertThat(singletonService1).isSameAs(singletonService2);
    }
```
<img src="./image/sec05_3.png">

- private으로 new키워드가 막힌것을 확인할 수 있음. → 컴파일오류발생
    - 가장 좋은 오류는 컴파일 오류! 컴파일 오류만으로 대부분의 오류가 다 잡히도록 설계하는 것이 잘 설계했다는 증거가 되기도 !
- 호출할때마다 같은 객체 인스턴스를 반환하는 것 또한 확인가능.

## 싱글톤패턴 문제점
- 싱글톤 패턴을 적용하면 고객의 요청이 올 때마다 객체를 생성하는 것이 아니라, 이미 만들어진 객체를 공유해서 효율적으로 사용할 수 있다. 하지만 싱글톤 패턴은 다음과 같은 문제점을 가지고 있다.
    - 싱글톤 패턴을 구현하는 코드 자체가 많이 들어감
    - 의존관계상 클라이언트가 구체 클래스에 의존한다. (ex)구체클래스.getInstance()
    - 클라이언트가 구체 클래스에 의존해서 OCP원칙을 위반할 가능성이 높다.
    - 테스트하기가 어렵다.
    - 내부 속성을 변경하거나 초기화 하기 어렵다.
    - private 생성자로 자식 클래스를 만들기 어렵다.
    - 결론적으로 유연성이 떨어져 안티패턴으로 불리기도 한다.

## 싱글톤 컨테이너
- 이제 AppConfig에 있는 것들을 전부 싱글톤 패턴으로 만들면 되는건가?
    - 아니다. 스프링 컨테이너를 사용하면 스프링 컨테이너가 기본적으로 객체를 전부 다 싱글톤으로 만들어서 관리해줌.
    - 심지어 스프링 프레임워크는 위의 싱글톤 패턴의 단점을 전부 해결하고 객체를 싱글톤으로 관리해줌 !

# 3. 싱글톤 컨테이너

# 4. 싱글톤 방식의 주의점

# 5. @Configuration과 싱글톤

# 6. @Configuration과 바이트코드 조작의 마법
