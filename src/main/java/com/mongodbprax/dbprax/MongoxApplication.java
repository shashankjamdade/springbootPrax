package com.mongodbprax.dbprax;

import com.mongodbprax.dbprax.service.OrderServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MongoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongoxApplication.class, args);
	}

}


//import io.grpc.Server;
//import io.grpc.ServerBuilder;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

/*@SpringBootApplication
public class MongoxApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MongoxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Server server = ServerBuilder.forPort(9090)
				.addService(new OrderServiceImpl())
				.build();

		server.start();
		System.out.println("gRPC server started on port 9090");
		server.awaitTermination();
	}
}*/
