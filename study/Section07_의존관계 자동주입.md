# 1. 다양한 의존관계 주입 방법
## (1) 생성자 주입
- 생성자를 통해 의존관계를 주입받음
```
@Component
public class OrderServiceImpl implements OrderService {

    // 생성자에 사용되는 필드는 final 붙이기(필수)
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy){
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```
- 특징
    - 생성자 호출 시점에 딱 1번만 호출되는 것이 보장 → 최초 한번 세팅 후 그 다음에는 세팅 불가(불변)
    - <U>**불변, 필수**</U> 의존관계에 사용
        - **불변** : 생성자를 딱 1번만 호출하기 때문에 값이 변하지 않음
        - **필수** : 생성자에서 사용하는 필드 2개는 final이 붙어 있음. 즉, memberRepository와 discountPolicy는 무조건 값이 들어가 있어야한다는 의미. 현재는 생성자 호출 시 해당 필드에 값이 채워지므로 final 필드 조건에 부합하지만, 만약 그렇지 않다면 에러 발생.
            ```
            // 에러나는 코드예시
            private final MemberRepository memberRepository;
            private final DiscountPolicy discountPolciy; // final필드인데 값이 없어서 에러나있음

            @Autowired
            public OrderServiceImpl(MemberRepository memberRepository){
                this.memberRepository = memberRepository;
            }
            ```
    - 생성자에서 사용하는 필드는 필수값으로 다 넣어주는 것이 관례 ! 만약 개발문서에 해당 필드는 null을 허용한다 라고 적혀있지 않은 이상, 생성자에서 사용하는 모든 필드는 값을 넣어주어야 한다.
    - 스프링 컨테이너에 빈을 등록함과 동시에 의존관계 자동주입도 이루어짐
        - 스프링 빈에 등록할 객체를 만들기 위해 new OrderServiceImpl()을 해야하는데, 이때 생성자 파라미터 값으로 넘겨줘야하는 객체가 있다면 그걸 스프링 컨테이너에서 찾아서 넘겨줌. 그렇기때문에 결국 생성자 주입방식은 빈 등록과 동시에 의존관계 자동주입도 이뤄짐.
- 생성자가 1개일때
    - 생성자가 1개만 있으면 @Autowired를 생략해도 자동 주입 된다. (물론 스프링 빈에만 해당)
> 좋은 개발 습관   
: 제약을 걸어 두는 것, 한계점이 명확하도록 코드를 짜는 것이 좋은 습관이다. 제약 없이 다 열어두면 어디서 수정되었는지, 내가 작성한 코드를 스스로 컨트롤할 수 없게됨.

## (2) 수정자 주입
```
@Component
public class OrderServiceImpl implements OrderService {

    private MemberRepository memberRepository;
    private DiscountPolicy discountPolicy;

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Autowired
    public void setDiscountPolicy(DiscountPolicy discountPolicy){
        this.discountPolicy = discountPolicy;
    }
}
```
- 스프링 컨테이너의 2가지 라이프사이클
    - 첫째. 스프링 빈을 등록한다.
    - 둘째. 의존관계를 자동으로 주입한다.
- 수정자(setter) 주입의 경우, OrderServiceImpl을 스프링 빈으로 등록한 뒤에 @Autowired 어노테이션을 보고 의존관계를 주입시켜줌.
    - 스프링 빈 등록과 동시에 의존관계 자동주입이 이뤄지는 생성자 주입방식과는 차이점을 보임
- 특징
    - <U>**선택, 변경**</U>가능성이 있는 의존관계에 사용
        - **선택** : 예를들어, 위 코드에서 memberRepository가 아직 스프링 빈으로 등록되지 않아서 setMemberRepository() 메소드로 의존관계 주입을 못하게 되더라도, 애플리케이션은 문제 없이 정상적으로 실행됨. 
        - **참고로** 선택적으로 의존관계를 주입하기 위해서는 @Autowired에 required = false 옵션을 달아주면 된다. (ex. @Autowired(required = false))
        - **변경** : 중간에 인스턴스를 교체하고 싶으면 강제로 set메소드를 호출시켜 안에 든 인스턴스를 변경할 수 있다.
- @Autowired
    - @Autowired의 기본 동작은 주입할 대상이 없으면 오류가 발생한다. 주입할 대상이 없어도 동작하게 하려면 @Autowired(required=false)로 지정하면 된다.

## (3) 필드 주입
```
@Component
public class OrderServiceImpl implements OrderService {

    @Autowired private MemberRepository memberRepository;
    @Autowired private DiscountPolicy discountPolicy;

}
```
- 필드에 바로 주입하는 방법
- 특징
    - 코드가 매우 간결하지만, 외부에서 변경이 불가능하여 테스트하기가 매우 어렵다.
    - DI프레임워크가 없으면 아무것도 할 수 없다. (즉, 순수한 자바코드만으로 테스트하기는 불가능하다.)
    - 아래의 경우를 제외하고는 사용하지 않기!
        - 애플리케이션의 실제 코드와는 관계 없는 테스트 코드
        - 스프링 설정을 목적으로 하는 @Configuration 같은 곳에서만 특별한 용도로 사용

## (4) 일반 메서드 주입
- 일반 메서드를 통해 주입받음
- 특징
    - 한번에 여러 필드를 주입 받을 수 있다
    - 대게 생성자 주입, 수정자 주입으로 해결할 수 있으므로, 일반 메서드 주입은 잘 사용하지 않음
    

# 2. 옵션 처리
# 3. 생성자 주입을 선택해라!
# 4. 롬복과 최신 트랜드
# 5. 조회 빈이 2개 이상 - 문제
# 6. @Autowired 필드 명, @Qualifier, @Primary
# 7. 애노테이션 직접 만들기
# 8. 조회한 빈이 모두 필요할 때, List, Map
# 9. 자동, 수동의 올바른 실무 운영 기준