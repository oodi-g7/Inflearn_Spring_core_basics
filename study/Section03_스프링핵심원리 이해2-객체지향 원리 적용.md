# 1. 새로운 할인 정책 개발
- 할인 정책을 추가하고 해당 정책에 대한 테스트 진행
```
class RateDiscountPolicyTest {

    RateDiscountPolicy discountPolicy = new RateDiscountPolicy();

    @Test
    @DisplayName("VIP는 10% 할인이 적용되어야 한다.")
    void vip_o(){
        //given
        Member member = new Member(1L, "memberVIP", Grade.VIP);
        //when
        int discount = discountPolicy.discount(member, 10000);
        //then
        assertThat(discount).isEqualTo(1000);
    }

    //  실패를 유도하는 테스트도 필요함
    @Test
    @DisplayName("VIP가 아니면 할인이 적용되지 않아야 한다")
    void vip_x(){
        //given
        Member member = new Member(2L, "memberBASIC", Grade.BASIC);
        //when
        int discount = discountPolicy.discount(member, 10000);
        //then
        assertThat(discount).isEqualTo(0);
    }
}
```

- JUnit5부터 @DisplayName을 통해 테스트명을 붙여줄 수 있다. 테스트 실행시, DisplayName에 적은 이름으로 결과가 표출된다. (아래)
<img src="./image/sec03_1.png">
- vip_x()의 경우, VIP가 아닌 고객은 할인이 적용되지 않아야 한다는 예외사항을 테스트하고 있다. 이처럼 어떤 기능을 개발했을때 해당 기능이 잘 동작되는지만 테스트할 것이 아니라, 오류 또는 예외가 발생해야하는 상황을 가정하여 오류나 예외가 잘 발생하는지 테스트해줄 필요가 있다.

# 2. 새로운 할인 정책 적용과 문제점
- 할인 정책을 애플리케이션에 적용하기

```
public class OrderServiceImpl implements OrderService{
    private final MemberRepository memberRepository = new MemoryMemberRepository();
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

}
```

- 문제점
    - 할인 정책을 변경하려면 할인(Discount)의 클라이언트인 주문(OrderServiceImpl) 코드를 변경해야한다.
    - 그 이유는 클라이언트 코드인 OrderServiceImpl는 추상(인터페이스)뿐만 아니라 동시에 구체(구현)클래스에도 의존을 하고 있기 때문 → DIP 위반
        - 추상(인터페이스)의존 : DiscountPolicy
        - 구체(구현)클래스 : FixDiscountPolicy, RateDiscountPolicy
    - 또한 기능을 확장해서 변경하면, 클라이언트 코드에 계속해서 영향을 줄 것이다. → OCP(변경없이확장) 위반

**실제의존관계**
<img src="./image/sec03_2.png">

**기능추가시 변경된 의존관계**
<img src="./image/sec03_3.png">

# 3. 관심사의 분리
- 현재코드의 문제점
    - 클라이언트는 어떤 구현체가 선택되더라도 본인의 역할을 수행할 수 있어야 함. → 하지만 현재 코드에서 클라이언트는 구현체가 변경될 때, 자신의 코드를 수정하지 않고서는 기능을 수행할 수 없음
    - 애플리케이션을 구성하고, 역할에 맞는 구현체를 선택하는 별도의 '기획자'가 필요. → AppConfig 클래스 생성

- AppConfig 등장
    - 애플리케이션의 전체 동작 방식을 구성하기 위해, 구현 객체를 생성하고, 연결하는 책임을 가지는 별도의 설정 클래스
    - 생성자 주입방식으로 구현 객체를 생성, 연결시켜 줌. → <U>**의존관계 주입**</U>
    ```
    public class AppConfig {
        
        public MemberService memberService(){
            return new MemberServiceImpl(new MemoryMemberRepository());
        }

        public OrderService orderService(){
            return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
        }
    }
    ```
    - 애플리케이션의 실제 동작에 필요한 구현 객체를 생성.
        - MemberServiceImpl
        - MemoryMemberRepository
        - OrderServiceImpl
        - FixDiscountPolicy
    - 생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입(연결)해줌.
        - MemberServiceImpl → MemoryMemberRepository
        - OrderServiceImpl → MemoryMemberRepository, FixDiscountPolicy

- AppConfig의 등장으로 변경된 코드
    ```
    public class MemberServiceImpl implements MemberService{

        //기존 - Impl에서 인터페이스(MemberRepository)와 구현체(MemoryMemberRepository) 모두 선택
        MemberRepository memberRepository = new MemoryMemberRepository();

        //변경 - Impl에서 인터페이스만 선택
        private final MemberRepository memberRepository;

        public MemberServiceImpl(MemberRepository memberRepository) {
            this.memberRepository = memberRepository;
        }
    }
    ```
    - 설계 변경으로 MemberServiceImpl은 더이상 MemoryMemberRepository를 의존하지 않음
    - MemberServiceImpl입장에선 생성자를 통해 어떤 구현 객체가 들어올지(주입될지) 알 수 없음
    - MemberServiceImpl의 생성자를 통해서 어떤 구현 객체를 주입할지는 오직 외부(AppConfig)에서 결정됨
    - 즉, MemberServiceImpl은 이제부터 의존관계에 대한 고민은 외부에 맡기고 실행에만 집중하면 됨.

- 클래스 다이어그램
    > 기존
    <img src="./image/sec03_5.png">
    > 변경 후
    <img src="./image/sec03_4.png">
    - 객체의 생성과 연결은 AppConfig가 담당한다.
    - **DIP 완성 :** MemberServiceImpl은 MemberRepository인 추상에만 의존하면 된다. 이제 구체 클래스를 몰라도 된다.
    - **관심사의 분리:** 객체를 생성하고 연결하는 역할과 실행하는 역할이 명확히 분리되었다.

- 회원 객체 인스턴스 다이어그램
    <img src="./image/sec03_5.png">
    - appConfig 객체는 memoryMemberRepository 객체를 생성하고 그 참조값을 memberServiceImpl을 생성하면서 생성자로 전달.
    - 클라이언트인 memberServiceImpl입장에서 보면 의존관계를 마치 외부에서 주입해주는 것 같다고 해서 DI(Dependency Injection) 의존관계 주입(의존성 주입)이라 한다.

- 테스트 코드 오류수정
    ```
    public class MemberServiceTest {

        MemberService memberService;

        @BeforeEach 
            public void beforeEach(){
            AppConfig appConfig = new AppConfig();
            memberService = appConfig.memberService();
        }
    }
    ```
    
    ```
    public class OrderServiceTest {
        MemberService memberService;
        OrderService orderService;

        @BeforeEach
        public void beforeEach(){
            AppConfig appConfig = new AppConfig();
            memberService = appConfig.memberService();
            orderService = appConfig.orderService();
        }
    }
    ```
    - 테스트 코드에서도 AppConfig를 생성하여 의존관계 주입을 받아 테스트를 실행한다.
    - 테스트 코드에서 @BeforeEach는 각 테스트를 실행하기 전에 호출한다.
    > Q질문. @BeforeEach안에 new AppConfig()를 하는 이유는 ?   
    저렇게 하면 테스트가 실행할때마다 새로운 AppConfig를 만들어줘야 하는데, 그래야 하는 이유가 있을까? 혹시 현재 임시 데이터베이스로 만든 HashMap을 테스트 후에 초기화해주고 재사용하기 번거로워서 테스트시마다 아예 새로 AppConfig를 만드는걸까?   
    A. 질문글남겨두기!!!!
