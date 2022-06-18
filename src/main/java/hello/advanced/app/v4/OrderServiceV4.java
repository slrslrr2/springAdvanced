package hello.advanced.app.v4;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV4 {
    private final OrderRepositoryV4 orderRepository;
    private final LogTrace trace;

    public void orderItem(String memberId) {
        AbstractTemplate<Void> template = new AbstractTemplate(trace) {
            @Override
            protected Object call() {
                orderRepository.save(memberId);
                return null;
            }
        };
        template.execute("OrderService.orderItem()");
    }
}
