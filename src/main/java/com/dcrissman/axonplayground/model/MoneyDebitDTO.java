package com.dcrissman.axonplayground.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoneyDebitDTO {

    private double debitAmount;
    private String currency;
    
}