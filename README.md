# 쓰레드 로컬 - ThreadLocal

스프링에서 싱글톤으로 등록된 객체 혹은 static으로 등록된 공용변수의 경우 <br>여러 쓰레드가 접근 할 경우 값이 동시에 공유되기에 변환되어 기대한 값을 받을 수 없는 경우가 생겨난다.<br>때문에, 이러한 문제를 해결하기 위해 ThreadLocal객체가 존재한다.

아래 예제는 동시성 있는 문제 예제코드이다.

**FieldService.java**

```java
@Slf4j
public class FieldService {
  private String nameStore;
  public String logic(String name) {
    log.info("저장 name={} -> nameStore={}", name, nameStore); nameStore = name;
    sleep(1000);
    log.info("조회 nameStore={}",nameStore);
    return nameStore;
  }
  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
```

- 매우 단순한 로직이다. 파라미터로 넘어온 name 을 필드인 nameStore 에 저장한다. <br>그리고 1초간 쉰 다음 필드에 저장된 nameStore 를 반환한다.



**FieldServiceTest.java**

```java
@Slf4j
public class FieldServiceTest {
  private FieldService fieldService = new FieldService();
  @Test
  void field() {
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
    
    threadA.start(); //A실행 
    sleep(2000); //동시성 문제 발생X
    //sleep(100); //동시성 문제 발생O 
    threadB.start(); //B실행
    
    sleep(3000); //메인 쓰레드 종료 대기
    log.info("main exit");
  }
  
  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
```



아래 상황으로 실행하게 된다면

```java
 threadA.start();
 sleep(2000);
 threadB.start();
```

결과는 다음과 같이 표시된다. ( 동시성 문제가 발생X)

```java
 11:16:28.702 [Test worker] INFO hello.advanced.trace.threadlocal.FieldServiceTest - main start
 11:16:28.708 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userA -> nameStore=null
 11:16:29.739 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userA
 11:16:30.714 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userB -> nameStore=userA
 11:16:31.725 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
```



하지만 아래와 같이 threadA와 threadB의 실행순서 간격을 좁히면 **동시성 문제 발생**하게된다.

```java
* 동시성 문제가 발생O
threadA.start();
sleep(100);
threadB.start();
```

```java
11:22:08.372 [Test worker] INFO hello.advanced.trace.threadlocal.FieldServiceTest - main start
11:22:08.378 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userA -> nameStore=null
11:22:08.484 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userB -> nameStore=userA
11:22:09.399 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB 
11:22:09.490 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB 
```

[thread-A] 로 실행된 함수도 **nameStore=userB**의 데이터가 찍히게된다.

즉, 위 처음 설명과 같이 

스프링에서 싱글톤으로 등록된 객체 혹은 static으로 등록된 공용변수의 경우 <br>여러 쓰레드가 접근 할 경우 값이 동시에 공유되기에 변환되어 기대한 값을 받을 수 없는 경우가 생겨난다.<br>때문에, 이러한 문제를 해결하기 위해 ThreadLocal객체가 존재한다.

-----

## ThreadLocal

위 문제를 해결하기 위해 ThreadLocal 객체가 존재한다.

쓰레드 로컬은 해당 쓰레드만 접근할 수 있는 특별한 저장소를 말한다. <br>여러 쓰레드가 같은 변수 혹은 인스턴스를 사용하더라도<br>ThreadLocal객체는 쓰레드를 인식해서 확실하게 (변수 혹은 인스턴스)를 구분해준다.
쓰레드A, 쓰레드B 모두 ThreadLocal객체를 통해서 변수 혹은 인스턴스를 보관하고 꺼내지만 <br>ThreadLocal이 쓰레드에 따라 보관한 (변수 혹은 인스턴스)를 구분해주는 것이다.

<img width="646" alt="image-20220612221221892" src="https://user-images.githubusercontent.com/58017318/174431378-a14f37f1-dd0f-4e3e-8363-623d8f45fcee.png">

> 쓰레드 로컬을 통해서 데이터를 조회할 때도 thread-A 가 조회하면 <br>쓰레드 로컬은 thread-A 전용 보관소에서 userA 데이터를 반환해준다. <br>물론 thread-B 가 조회하면 thread-B 전용 보관소에서 userB 데이터를 반환해준다.
>
> 자바는 언어차원에서 쓰레드 로컬을 지원하기 위한 java.lang.ThreadLocal 클래스를 제공한다.



## ThreadLocal 적용

**ThreadLocal 사용법**

```java
선언   : private ThreadLocal<String> nameStore = new ThreadLocal<>();
값 저장: ThreadLocal.set(xxx) 
값 조회: ThreadLocal.get()
값 제거: ThreadLocal.remove()
```



**적용하기**

```java
@Slf4j
public class ThreadLocalService {
  private ThreadLocal<String> nameStore = new ThreadLocal<>();
  public String logic(String name) {
    log.info("저장 name={} -> nameStore={}", name, nameStore.get());
    nameStore.set(name);
    sleep(1000);
    String returnValue = nameStore.get();
    nameStore.remove(); // 꼭 제거해주어야한다!!!!!!
    log.info("조회 nameStore={}", returnValue);
    return returnValue;
  }
  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
```



```java
public class ThreadLocalServiceTest {
  private ThreadLocalService service = new ThreadLocalService();
  @Test
  void threadLocal() {
    log.info("main start");
    Runnable userA = () -> {
      service.logic("userA");
    };
    Runnable userB = () -> {
      service.logic("userB");
    };
    
    Thread threadA = new Thread(userA);
    threadA.setName("thread-A");
    Thread threadB = new Thread(userB);
    threadB.setName("thread-B");
    threadA.start();
    sleep(100);
    threadB.start();
    sleep(2000);
    log.info("main exit");
  }
  
  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
```



**실행 결과**

```java
[Test worker] main start
[Thread-A] 저장 name=userA -> nameStore=null 
[Thread-B] 저장 name=userB -> nameStore=null 
[Thread-A] 조회 nameStore=userA
[Thread-B] 조회 nameStore=userB
[Test worker] main exit
```

> 쓰레드 로컬 덕분에 쓰레드 마다 각각 별도의 데이터 저장소를 가지게 되었다. 결과적으로 동시성 문제도 해결되었다.

-------



## ThreaLocal 사용 시 주의사항

쓰레드 로컬의 값을 사용 후 제거하지 않고 그냥 두면 WAS(톰캣)처럼 **쓰레드 풀**을 사용하는 경우에 심각한 **문제가 발생**할 수 있다.
 다음 예시를 통해서 알아보자.

**사용자A 저장 요청**
<img width="646" alt="image-20220612224928472" src="https://user-images.githubusercontent.com/58017318/174431383-28654ccd-86ec-4be7-ab4e-309abd6322ff.png">
1. **사용자A**가 저장 **HTTP를 요청**했다.
2. WAS는 쓰레드 풀에서 쓰레드를 하나 조회한다.
3. 쓰레드 **thread-A 가 할당**되었다.
4. **thread-A 는 사용자A 의 데이터**를 **쓰레드 로컬에 저장**한다.
5. 쓰레드 로컬의 **thread-A 전용 보관소**에 **사용자A 데이터를 보관**한다.



**사용자 A저장 요청 종료**

<img width="644" alt="image-20220612225103261" src="https://user-images.githubusercontent.com/58017318/174431384-5242e2be-692b-40fb-8eae-e0ae3c1a3e4b.png">

1. **사용자A**의 HTTP **응답이 끝**난다.
2. WAS는 사용이 끝난 **thread-A**를 **쓰레드 풀**에 **반환**한다. <br>**쓰레드를 생성**하는 **비용**은 **비싸**기 때문에 쓰레드를 제거하지 않고, 보통 **쓰레드 풀을 통해서 쓰레드를 재사용**한다.
3. **thread-A**는 **쓰레드풀에 아직 살아있다**. <br>따라서 쓰레드 **로컬의 thread-A 전용 보관소**에 **🔸사용자A 데이터도 함께 살아있게 된다.🔸**



**사용자B 조회 요청**

<img width="642" alt="image-20220612225433818" src="https://user-images.githubusercontent.com/58017318/174431386-e6fdda48-ee15-4840-8202-ab2955e877d3.png">

1. **사용자B**가 조회를 위한 새로운 **HTTP 요청**을 한다.
2. WAS는 **쓰레드 풀에서 쓰레드를 하나 조회**한다.
3. 쓰레드 **thread-A 가 할당**되었다. (물론 다른 쓰레드가 할당될 수 도 있다.)
4. 이번에는 조회하는 요청이다. **thread-A 는 쓰레드 로컬에서 데이터를 조회**한다. 
5. 쓰레드 로컬은 **thread-A 전용 보관소**에 있는 **사용자A 값을 반환**한다.
6. 결과적으로 사용자A 값이 반환된다.
7. **사용자B는 사용자A의 정보를 조회**하게 된다.

결과적으로 **사용자B는 사용자A의 데이터를 확인하**게 되는 **심각한 문제가 발생**하게 된다.
 이런 문제를 예방하려면 사용자A의 요청이 끝날 때 쓰레드 로컬의 값을 🔸**ThreadLocal.remove() 를 통해서 꼭 제거**🔸해야 한다.
 쓰레드 로컬을 사용할 때는 이 부분을 꼭! 기억하자.



