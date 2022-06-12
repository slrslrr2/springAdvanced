package hello.advanced.trace.threadlocal;

import hello.advanced.trace.threadlocal.code.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 동시성 문제가 발생X
 threadA.start();
 sleep(2000);
 threadB.start();

 11:16:28.702 [Test worker] INFO hello.advanced.trace.threadlocal.FieldServiceTest - main start
 11:16:28.708 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userA -> nameStore=null
 11:16:29.739 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userA
 11:16:30.714 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userB -> nameStore=userA
 11:16:31.725 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
*/

/**
 * 동시성 문제가 발생O
 threadA.start();
 sleep(100);
 threadB.start();

 11:22:08.372 [Test worker] INFO hello.advanced.trace.threadlocal.FieldServiceTest - main start
 11:22:08.378 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userA -> nameStore=null
 11:22:08.484 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userB -> nameStore=userA
 11:22:09.399 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
 11:22:09.490 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
 */
@Slf4j
public class FieldServiceTest {
    private FieldService fieldService = new FieldService();

    @Test
    void field() throws InterruptedException {
        log.info("main start");

        /**
         Runnable userA = new Runnable(){
            @Override
            public void run(){
                fieldService.login("userA");
            }
         };
         */
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
