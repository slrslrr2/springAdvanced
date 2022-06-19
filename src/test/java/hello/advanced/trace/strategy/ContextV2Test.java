package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.strategy.*;
import hello.advanced.trace.template.code.AbstractTemplate;
import hello.advanced.trace.template.code.SubClassLogin1;
import hello.advanced.trace.template.code.SubClassLogin2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextV2Test {
    @Test
    void strategyV1(){
        ContextV2 context = new ContextV2();
        context.execute(new StrategyLogic1());

        ContextV2 context2 = new ContextV2();
        context2.execute(new StrategyLogic2());
    }

    @Test
    void stratageV2(){
        ContextV2 context = new ContextV2();
        context.execute(new Strategy() { // 익명함수 그대로 쓰기
            @Override
            public void call() {
                log.info("비즈니스 로직 실행1");
            }
        });

        ContextV2 context2 = new ContextV2();
        context2.execute(() -> log.info("비즈니스 로직 실행2")); // 람다로 익명함수 넘기기
    }
}