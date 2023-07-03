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
- 생성자가 1개일때
    - 생성자가 1개만 있으면 @Autowired를 생략해도 자동 주입 된다. (물론 스프링 빈에만 해당)

## (2) 수정자 주입
> 좋은 개발 습관   
: 제약을 걸어 두는 것, 한계점이 명확하도록 코드를 짜는 것이 좋은 습관이다. 제약 없이 다 열어두면 어디서 수정되었는지, 내가 작성한 코드를 스스로 컨트롤할 수 없게됨.

# 2. 옵션 처리
# 3. 생성자 주입을 선택해라!
# 4. 롬복과 최신 트랜드
# 5. 조회 빈이 2개 이상 - 문제
# 6. @Autowired 필드 명, @Qualifier, @Primary
# 7. 애노테이션 직접 만들기
# 8. 조회한 빈이 모두 필요할 때, List, Map
# 9. 자동, 수동의 올바른 실무 운영 기준