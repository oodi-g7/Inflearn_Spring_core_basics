package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan (
        // @ComponentScan : @Component 어노테이션이 붙은 클래스들을 찾아서 자동으로 스프링 빈으로 등록시켜줌
        // excludeFilters : 컴포넌트 스캔 중에 뺄 것을 지정
        // type : 어노테이션타입으로 지정, classes : configuration 어노테이션이 붙었으면 제외대상
        // @Configuration 클래스 파일을 조회해보면, Configuration클래스 또한 @Component 어노테이션이 붙어있어서 컴포넌트 스캔시 자동등록 대상에 포함됨.
        // 하지만 현재 @Configuration 어노테이션을 붙인(ex.AppConfig) 클래스들은 스프링 빈 수동등록 방식으로 작성된 것이기 때문에, 자동등록 대상에서 제외해줘야 충돌이 발생하지 않음
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {
}
