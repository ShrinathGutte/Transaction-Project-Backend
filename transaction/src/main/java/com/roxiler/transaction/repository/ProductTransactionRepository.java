package com.roxiler.transaction.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.roxiler.transaction.entity.ProductTransaction;

import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Integer> {
	Page<ProductTransaction> findByDateOfSaleBetween(OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);

    Page<ProductTransaction> findByDateOfSaleBetweenAndTitleContainingOrDescriptionContainingOrPriceContaining(
            OffsetDateTime startDate, OffsetDateTime endDate, String title, String description, String price, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(pt.price), 0) FROM ProductTransaction pt WHERE MONTH(pt.dateOfSale) = :month AND pt.dateOfSale IS NOT NULL")
    BigDecimal calculateTotalSaleAmount(@Param("month") int month);

    @Query("SELECT COUNT(pt) FROM ProductTransaction pt WHERE MONTH(pt.dateOfSale) = :month AND pt.dateOfSale IS NOT NULL")
    long countSoldItems(@Param("month") int month);

    @Query("SELECT COUNT(pt) FROM ProductTransaction pt WHERE MONTH(pt.dateOfSale) = :month AND pt.dateOfSale IS NULL")
    long countUnsoldItems(@Param("month") int month);
    
    
    @Query("SELECT pt FROM ProductTransaction pt WHERE MONTH(pt.dateOfSale) = :month")
    List<ProductTransaction> findByMonth(@Param("month") int month);
    

}
