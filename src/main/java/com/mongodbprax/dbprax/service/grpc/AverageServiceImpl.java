package com.mongodbprax.dbprax.service.grpc;

import com.playground.grpc.average.AverageServiceGrpc;
import com.playground.grpc.average.AvgResponse;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class AverageServiceImpl extends AverageServiceGrpc.AverageServiceImplBase {
    @Override
    public StreamObserver<com.playground.grpc.average.Number> computeAvg(StreamObserver<AvgResponse> resp) {
        return new StreamObserver<com.playground.grpc.average.Number>() {
            int sum = 0, count = 0;
            @Override public void onNext(com.playground.grpc.average.Number value) { sum += value.getValue(); count++; }
            @Override public void onError(Throwable t) {}
            @Override public void onCompleted() {
                resp.onNext(AvgResponse.newBuilder().setAvg((double) sum / count).build());
                resp.onCompleted();
            }
        };
    }
}
