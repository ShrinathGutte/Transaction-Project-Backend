package com.roxiler.transaction.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatisticsDTO {
	
	private BigDecimal totalSaleAmount;
    private long totalSoldItems;
    private long totalUnsoldItems;

}
