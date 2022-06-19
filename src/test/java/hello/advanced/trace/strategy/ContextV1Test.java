package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.strategy.ContextV1;
import hello.advanced.trace.strategy.code.strategy.Strategy;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2;
import hello.advanced.trace.template.code.AbstractTemplate;
import hello.advanced.trace.template.code.SubClassLogin1;
import hello.advanced.trace.template.code.SubClassLogin2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextV1Test {

    @Test
    void templateMethodV0(){
        logic1();
        logic2();
    }

    private void logic1(){
        long startTime = System.currentTimeMillis();
        //비즈니스 로직 실행
        log.info("비즈니스 로직 실행1");
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime = {}", resultTime);
    }

    private void logic2(){
        long startTime = System.currentTimeMillis();
        //비즈니스 로직 실행
        log.info("비즈니스 로직2 실행");
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime = {}", resultTime);
    }

    /**
     * 템프릿 메서드 패턴 적용1
     */
    @Test
    void templateMethodV1(){
        AbstractTemplate template1 = new SubClassLogin1();
        template1.exec();
        System.out.println("template1.getClass() = " + template1.getClass());
        AbstractTemplate template2 = new SubClassLogin2();
        template2.exec();
    }

    /**
     * 템프릿 메서드 패턴 적용2 익명 내부 클래스 활용
     */
    @Test
    void templateMethodV2(){
        AbstractTemplate template1 = new AbstractTemplate() {
            @Override
            protected void call() {
                log.info("비즈니스로직1 실행");
            }
        };
        template1.exec();

        AbstractTemplate template2 = new AbstractTemplate() {
            @Override
            protected void call() {
                log.info("비즈니스로직2 실행");
            }
        };
        template2.exec();

        /**
         * 익명 내부 클래스를 사용하면
            객체 인스턴스를 생성하면서 동시에 상속받아야할 자식 클래스도 정의할 수 있다.
         * 익명클래스는 직접 지정하는 이름이 없고 내부에 선언되어있기에
            getClass메소드를 찍어보면
            현재클래스$1 이렇게 표시된다.
            ==> class hello.advanced.trace.template.TemplateMethodTest$2
         */
        log.info("익명내부클래스 template2 이름 => {}", template2.getClass());
    }

    @Test
    void strategyV1(){
        StrategyLogic1 strategyLogic1 = new StrategyLogic1();
        ContextV1 contextV1 = new ContextV1(strategyLogic1);
        contextV1.execute();

        StrategyLogic2 strategyLogic2 = new StrategyLogic2();
        ContextV1 contextV2 = new ContextV1(strategyLogic2);
        contextV2.execute();
    }

    @Test
    void strategy2(){
        Strategy strategyLogic1 = new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직 실행1");
            }
        };
        ContextV1 contextV1 = new ContextV1(strategyLogic1);
        contextV1.execute();


        Strategy strategyLogic2 = new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직 실행2");
            }
        };
        ContextV1 contextV2 = new ContextV1(strategyLogic2);
        contextV2.execute();
    }

    @Test
    void strategy3(){
        // ContextV1생성하면서 구현채를 바로 넣어버리기
        ContextV1 contextV1 = new ContextV1(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직 실행1");
            }
        });
        contextV1.execute();

        ContextV1 contextV2 = new ContextV1(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직 실행2");
            }
        });
        contextV2.execute();
    }

    @Test
    void strategy4(){
        // ContextV1생성하면서 구현채를 바로 넣어버리는데 [람다]로 바꾸기
        ContextV1 contextV1 = new ContextV1(() -> log.info("비즈니스 로직 실행1"));
        contextV1.execute();

        ContextV1 contextV2 = new ContextV1(() -> log.info("비즈니스 로직 실행2"));
        contextV2.execute();
    }
}
