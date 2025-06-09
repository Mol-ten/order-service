package kz.molten.techshop.orderservice.application.service;

import kz.molten.techshop.orderservice.api.dto.request.CustomerDeliveryDTO;
import kz.molten.techshop.orderservice.api.exception.CustomerDeliveryInfoNotFoundException;
import kz.molten.techshop.orderservice.application.mapper.CustomerDeliveryInfoMapper;
import kz.molten.techshop.orderservice.domain.model.CustomerDelivery;
import kz.molten.techshop.orderservice.domain.repository.CustomerDeliveryInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDeliveryInfoService {
    private final CustomerDeliveryInfoRepository customerDeliveryInfoRepository;


    public CustomerDelivery create(Long customerUserId, CustomerDeliveryDTO deliveryDTO) {
        log.info("Saving CustomerDelivery for customer with id: {}", customerUserId);

        CustomerDelivery deliveryInfo = CustomerDeliveryInfoMapper.toDomain(deliveryDTO, customerUserId);

        customerDeliveryInfoRepository.save(deliveryInfo);

        log.info("CustomerDelivery was saved successfully");

        return deliveryInfo;
    }

    public CustomerDelivery getByCustomerUserId(Long customerUserId) {
        log.info("Fetching CustomerDelivery by customerUserId: {}", customerUserId);

        return customerDeliveryInfoRepository.findByCustomerUserId(customerUserId)
                .orElseThrow(() -> new CustomerDeliveryInfoNotFoundException("CustomerDelivery with id: %d was not found"
                        .formatted(customerUserId)));
    }
}
