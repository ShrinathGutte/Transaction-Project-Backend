package com.roxiler.transaction.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.roxiler.transaction.entity.ProductTransaction;
import com.roxiler.transaction.repository.ProductTransactionRepository;

@Service
public class ProductService {
	
	private final RestTemplate restTemplate;
    private final ProductTransactionRepository productTransactionRepository;

    @Autowired
    public ProductService(RestTemplate restTemplate, ProductTransactionRepository productTransactionRepository) {
        this.restTemplate = restTemplate;
        this.productTransactionRepository = productTransactionRepository;
    }

    @Transactional
    public void fetchAndSaveDataFromApi() {
        String apiUrl = "https://s3.amazonaws.com/roxiler.com/product_transaction.json";
        ProductTransaction[] transactions = restTemplate.getForObject(apiUrl, ProductTransaction[].class);
        if (transactions != null) {
            productTransactionRepository.saveAll(Arrays.asList(transactions));
        }
    }
    
    public ProductTransaction[] fetchDataFromApi() {
        String apiUrl = "https://s3.amazonaws.com/roxiler.com/product_transaction.json";
        return restTemplate.getForObject(apiUrl, ProductTransaction[].class);
    }
    
    
}
