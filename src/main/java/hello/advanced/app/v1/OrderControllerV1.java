package hello.advanced.app.v1;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 V1은 각 Component마다 trace.begin을 통해
 TraceSatus를 생성하기때문에,
 TraceStatus 생성 시, new TraceId가 되기때문에

 문제1, Component 별로 transaction ID가 생성된다.
 문제2, depth 표현이 안되고있다.
 */
@RestController
@RequiredArgsConstructor
public class OrderControllerV1 {

    private final OrderServiceV1 orderService;
    private final HelloTraceV1 trace;

    @GetMapping("/v1/request")
    public String request(String itemId){

        TraceStatus status = null;
        try{
            status = trace.begin("OrderControllerV1.request()");
            orderService.orderItem(itemId);
            trace.end(status);
            return "ok";
        } catch(Exception e) {
            trace.exception(status, e);
            throw e;// 예외를 꼭 다시 던져줘야 상위에서도 exception로그를 찍을 수 있다.
        }
    }
}
