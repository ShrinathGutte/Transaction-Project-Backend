package com.roxiler.transaction.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.roxiler.transaction.entity.ProductTransaction;
import com.roxiler.transaction.entity.StatisticsDTO;
import com.roxiler.transaction.service.ProductService;
import com.roxiler.transaction.service.TransactionService;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {
	
	private final ProductService productService;

	@Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
	
	@Autowired
	TransactionService transactionService;

	@PostMapping("/fetch-and-save")
    public String fetchAndSaveDataFromApi() {
        productService.fetchAndSaveDataFromApi();
        return "Data fetched from API and saved into ProductTransaction table";
    }
	
	@GetMapping("/fetch-data")
    public ProductTransaction[] fetchDataFromApi() {
        return productService.fetchDataFromApi();
    }
	
	@GetMapping("/productTransactions")
    public ProductTransaction[] getProductTransactions(@RequestParam String month) {
        // Call the service method to fetch data for the specified month
        return transactionService.fetchDataFromApi(month);
    }
	
	@GetMapping("/statistics")
    public StatisticsDTO getStatistics(@RequestParam String month) {
        return transactionService.getStatistics(month);
    }
	
	@GetMapping("/transactions")
    public Page<ProductTransaction> getAllTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String searchText) {
        return transactionService.getAllTransactions(page, perPage, month, searchText);
    }
	
	@GetMapping("/barchart")
    public Map<String, Integer> generateBarChart(@RequestParam String month) {
        return transactionService.generateBarChart(month);
    }
	
	@GetMapping("/piechart")
    public Map<String, Integer> generatePieChart(@RequestParam String month) {
        return transactionService.generatePieChart(month);
    }
	
	@GetMapping("/combined-data")
    public Map<String, Object> getCombinedData(@RequestParam String month) {
        return transactionService.getCombinedData(month);
    }
}
