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
