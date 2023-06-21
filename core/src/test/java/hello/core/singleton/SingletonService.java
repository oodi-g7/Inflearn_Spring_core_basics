package hello.core.singleton;

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

    // 애플리케이션 실행시 동작순서
    // 1. 우선 애플리케이션이 실행되면서 SingletonService의 static영역에 new SingletonService()를 실행하여 해당 객체를 생성한 후 변수 instance에 참조를 걸어둠
    // 2. 즉 SingletonService 객체는 현재 static영역에 단 하나만 존재함.
    // 3. instance변수에 담긴 SingletonService객체에 대한 참조값을 사용할 수 있는 방법은 오롯이 getInstance() 이다.
    // 4. 또한 SingletonService를 생성할 수 있는 방법은 전혀 없음. (이미 기본생성자가 있는데 그게 private이므로)

    public void logic(){
        System.out.println("싱긅톤 객체 로직 호출");
    }
}
