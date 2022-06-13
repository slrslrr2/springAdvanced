package hello.advanced.trace.threadlocal;

import hello.advanced.trace.threadlocal.code.FieldService;
import hello.advanced.trace.threadlocal.code.ThreadLocalService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 동시성 문제가 발생X
 threadA.start();
 sleep(2000); // 이거나
 sleep(100); // 이거나 해도 동시성 문제 발생 안한다 ==> ThreadLocal 객체를 통해 데이터가 공유되지않았기때문에
 threadB.start();

 21:53:58.432 [Test worker] INFO hello.advanced.trace.threadlocal.ThreadLocalServiceTest - main start
 21:53:58.436 [thread-A] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 저장 name=userA -> nameStore=null
 21:53:58.542 [thread-B] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 저장 name=userB -> nameStore=null
 21:53:59.462 [thread-A] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 조회 nameStore=userA
 21:53:59.548 [thread-B] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 조회 nameStore=userB
 21:54:00.543 [Test worker] INFO hello.advanced.trace.threadlocal.ThreadLocalServiceTest - main exit
 */
@Slf4j
public class ThreadLocalServiceTest {
    private ThreadLocalService fieldService = new ThreadLocalService();

    @Test
    void field() throws InterruptedException {
        log.info("main start");
        Runnable userA = () -> {
            fieldService.logic("userA");
        };

        Runnable userB = () -> {
            fieldService.logic("userB");
        };

        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start();
//        sleep(2000); //동시성 문제가 발생X
        sleep(100); //동시성 문제가 발생O
        threadB.start();

        sleep(2000); //메인 쓰레드 종료 대기
        log.info("main exit");
    }

    private void sleep(int millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
