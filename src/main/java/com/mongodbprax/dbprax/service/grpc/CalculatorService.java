package com.mongodbprax.dbprax.service.grpc;

import com.mongodbprax.dbprax.CalcRequest;
import com.mongodbprax.dbprax.CalcResponse;
import com.mongodbprax.dbprax.CalculatorGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService extends CalculatorGrpc.CalculatorImplBase {
    @Override
    public void add(CalcRequest req, StreamObserver<CalcResponse> resp) {
        int result = req.getA() + req.getB();
        resp.onNext(CalcResponse.newBuilder().setResult(result).build());
        resp.onCompleted();
    }

    @Override
    public void multiply(CalcRequest req, StreamObserver<CalcResponse> resp) {
        int result = req.getA() * req.getB();
        resp.onNext(CalcResponse.newBuilder().setResult(result).build());
        resp.onCompleted();
    }
}
