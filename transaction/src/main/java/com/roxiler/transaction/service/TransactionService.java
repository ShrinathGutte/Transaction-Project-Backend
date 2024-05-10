package com.roxiler.transaction.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.roxiler.transaction.entity.ProductTransaction;
import com.roxiler.transaction.entity.StatisticsDTO;
import com.roxiler.transaction.repository.ProductTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Month;

@Service
public class TransactionService {
	private final ProductTransactionRepository transactionRepository;
	
	private final RestTemplate restTemplate;

    @Autowired
    public TransactionService(ProductTransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    public Page<ProductTransaction> getAllTransactions(int page, int perPage, String month, String searchText) {
        OffsetDateTime startDateOfMonth = OffsetDateTime.now().withMonth(parseMonth(month)).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime endDateOfMonth = startDateOfMonth.withDayOfMonth(lengthOfMonth(parseMonth(month))).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        if (searchText == null || searchText.isBlank()) {
            return transactionRepository.findByDateOfSaleBetween(startDateOfMonth, endDateOfMonth, PageRequest.of(page - 1, perPage));
        } else {
            return transactionRepository.findByDateOfSaleBetweenAndTitleContainingOrDescriptionContainingOrPriceContaining(
                    startDateOfMonth, endDateOfMonth, searchText, searchText, searchText, PageRequest.of(page - 1, perPage)
            );
        }
    }

    private int parseMonth(String month) {
        switch (month.toLowerCase()) {
            case "january":
                return 1;
            case "february":
                return 2;
            case "march":
                return 3;
            case "april":
                return 4;
            case "may":
                return 5;
            case "june":
                return 6;
            case "july":
                return 7;
            case "august":
                return 8;
            case "september":
                return 9;
            case "october":
                return 10;
            case "november":
                return 11;
            case "december":
                return 12;
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    private int lengthOfMonth(int month) {
        return Month.of(month).length(true);
    }
    
    public StatisticsDTO getStatistics(String month) {
        int parsedMonth = parseMonth(month);
        BigDecimal totalSaleAmount = transactionRepository.calculateTotalSaleAmount(parsedMonth);
        long totalSoldItems = transactionRepository.countSoldItems(parsedMonth);
        long totalUnsoldItems = transactionRepository.countUnsoldItems(parsedMonth);
        
        return new StatisticsDTO(totalSaleAmount, totalSoldItems, totalUnsoldItems);
    }
    
    public ProductTransaction[] fetchDataFromApi(String month) {
    	int parsedMonth = parseMonth(month);
        String apiUrl = "https://s3.amazonaws.com/roxiler.com/product_transaction.json?month=" + parsedMonth;
        return restTemplate.getForObject(apiUrl, ProductTransaction[].class);
    }

    
    public Map<String, Integer> generateBarChart(String month) {
        int parsedMonth = parseMonth(month);
        Map<String, Integer> priceRangeMap = initializePriceRangeMap();

        List<ProductTransaction> transactions = transactionRepository.findByMonth(parsedMonth);

        for (ProductTransaction transaction : transactions) {
            BigDecimal price = transaction.getPrice();
            String priceRange = getPriceRange(price);
            priceRangeMap.put(priceRange, priceRangeMap.getOrDefault(priceRange, 0) + 1);
        }

        return priceRangeMap;
    }

    private Map<String, Integer> initializePriceRangeMap() {
        Map<String, Integer> priceRangeMap = new HashMap<>();
        priceRangeMap.put("0 - 100", 0);
        priceRangeMap.put("101 - 200", 0);
        priceRangeMap.put("201 - 300", 0);
        priceRangeMap.put("301 - 400", 0);
        priceRangeMap.put("401 - 500", 0);
        priceRangeMap.put("501 - 600", 0);
        priceRangeMap.put("601 - 700", 0);
        priceRangeMap.put("701 - 800", 0);
        priceRangeMap.put("801 - 900", 0);
        priceRangeMap.put("901 - above", 0);
        return priceRangeMap;
    }
    
    private String getPriceRange(BigDecimal price) {
        if (price.compareTo(BigDecimal.valueOf(0)) >= 0 && price.compareTo(BigDecimal.valueOf(100)) <= 0) {
            return "0 - 100";
        } else if (price.compareTo(BigDecimal.valueOf(101)) >= 0 && price.compareTo(BigDecimal.valueOf(200)) <= 0) {
            return "101 - 200";
        } else if (price.compareTo(BigDecimal.valueOf(201)) >= 0 && price.compareTo(BigDecimal.valueOf(300)) <= 0) {
            return "201 - 300";
        } else if (price.compareTo(BigDecimal.valueOf(301)) >= 0 && price.compareTo(BigDecimal.valueOf(400)) <= 0) {
            return "301 - 400";
        } else if (price.compareTo(BigDecimal.valueOf(401)) >= 0 && price.compareTo(BigDecimal.valueOf(500)) <= 0) {
            return "401 - 500";
        } else if (price.compareTo(BigDecimal.valueOf(501)) >= 0 && price.compareTo(BigDecimal.valueOf(600)) <= 0) {
            return "501 - 600";
        } else if (price.compareTo(BigDecimal.valueOf(601)) >= 0 && price.compareTo(BigDecimal.valueOf(700)) <= 0) {
            return "601 - 700";
        } else if (price.compareTo(BigDecimal.valueOf(701)) >= 0 && price.compareTo(BigDecimal.valueOf(800)) <= 0) {
            return "701 - 800";
        } else if (price.compareTo(BigDecimal.valueOf(801)) >= 0 && price.compareTo(BigDecimal.valueOf(900)) <= 0) {
            return "801 - 900";
        } else {
            return "901 - above";
        }
    }
    
    public Map<String, Integer> generatePieChart(String month) {
        int parsedMonth = parseMonth(month);

        // Fetch transactions for the selected month
        List<ProductTransaction> transactions = transactionRepository.findByMonth(parsedMonth);

        // Map to store category and count of items
        Map<String, Integer> categoryCountMap = new HashMap<>();

        // Calculate count of items for each category
        for (ProductTransaction transaction : transactions) {
            String category = transaction.getCategory(); // Assuming there's a getCategory() method in ProductTransaction
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
        }

        return categoryCountMap;
    }
    
    public Map<String, Object> getCombinedData(String month) {
        Map<String, Object> combinedData = new HashMap<>();

        // Get statistics
        StatisticsDTO statistics = getStatistics(month);
        combinedData.put("statistics", statistics);

        // Get bar chart data
        Map<String, Integer> barChartData = generateBarChart(month);
        combinedData.put("barChartData", barChartData);

        // Get pie chart data
        Map<String, Integer> pieChartData = generatePieChart(month);
        combinedData.put("pieChartData", pieChartData);

        return combinedData;
    }

}
