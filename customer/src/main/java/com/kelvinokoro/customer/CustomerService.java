package com.kelvinokoro.customer;

import com.kelvinokoro.clients.fraud.FraudCheckResponse;
import com.kelvinokoro.clients.fraud.FraudClient;
import com.kelvinokoro.clients.notification.NotificationClient;
import com.kelvinokoro.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerService{

    private final CustomerRepository customerRepository;

    private final RestTemplate restTemplate;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        customerRepository.saveAndFlush(customer);
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        notificationClient.sendNotification(
                new NotificationRequest(customer.getId(),
                customer.getEmail(),
                String.format("Hi %s Welcome to Kelvin Services...",
                        customer.getFirstName())));
    }
}
