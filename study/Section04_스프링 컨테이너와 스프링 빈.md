# 1. 스프링 컨테이너 생성
- 컨테이너 = 사용하는 객체들을 전부 담고 있는 곳이라고 이해하기
- ApplicationContext를 스프링 컨테이너라고 한다.
- ApplicationContext는 인터페이스이다.
- AppConfig를 사용했던 방식이 애노테이션 기반의 자바 설정 클래스로 스프링 컨테이너를 만든 것 ! → AnnotationConfigApplication()
- AnnotationConfigApplication() 클래스는 ApplicationContext 인터페이스의 구현체이다.
- **[참고]**   
스프링 컨테이너는 BeanFactory, ApplicationContext 두 개를 구분해서 이야기하는데, BeanFactory를 직접 사용하는 경우는 드물기때문에 그냥 **ApplicationContext = 스프링 컨테이너** 로 이해하기

## 스프링 컨테이너의 생성과정
- (1) 스프링 컨테이너 생성
    - 1-1. new AnnotationConfigApplicationContext(AppConfig.class)
    - 1-2. 스프링 컨테이너를 생성할때는 구성정보(AppConfig.class)를 지정해주어야 함.
    - 1-3. 컨테이너 생성시, 내부에는 스프링 빈 저장소가 생김
    <img src="./image/sec04_1.png">
- (2) 구성정보를 활용하여 스프링 빈 등록
    - 2-1. 구성정보 활용시, 스프링 컨테이너는 파라미터로 넘어온 설정 클래스 내에 있는 어노테이션을 참고하여 스프링 빈을 등록함.
    - 2-2. **관례상 빈 이름은 메서드 이름을 사용한다.** 빈 이름을 직접부여할 수도 있다. **빈이름은 항상 다른 이름을 부여해야 한다.**
    <img src="./image/sec04_2.png">
- (3) 스프링 빈 동적인 객체 인스턴스 의존관계 설정
    - 3-1. 스프링 컨테이너는 설정 정보를 참고해서 의존관계를 주입(DI)한다.
    - 3-2. 단순히 자바 코드를 호출하는 것 같지만, 차이가 존재. 싱글톤 컨테이너에서 그 차이를 설명.
    <img src="./image/sec04_3.png">
- (4) 참고
    - 스프링은 빈을 생성하고, 의존관계를 주입하는 단계가 나누어져 있다.   
    그런데 지금처럼 자바 코드로 스프링 빈을 등록할 경우에는(AppConfig) 생성자를 호출하면서 의존관계 주입도 한번에 처리된다.
        > 예를 들자면, AppConfig의 memberService()를 호출했으면 return값인 new MemberServiceImpl()또한 호출되면서 파라미터로 담겨있던 AppConfig의 memberRepository() 까지 호출됨

    - 하지만 실제 스프링의 라이프사이클은 빈 생성과 의존관계 주입 단계가 나뉘어져 있으므로, 이후 강의를 통해 자동의존관계 주입을 배우면서 왜 단계가 나뉘어져야 하는지 배울 것임 !


# 2. 컨테이너에 등록된 모든 빈 조회

# 3. 스프링 빈 조회 : 기본

# 4. 스프링 빈 조회 : 동일한 타입이 둘 이상

# 5. 스프링 빈 조회 : 상속 관계

# 6. BeanFactory와 ApplicationContext

# 7. 다양한 설정 형식 지원 : 자바코드, XML

# 8. 스프링 빈 설정 메타 정보 : BeanDefinition