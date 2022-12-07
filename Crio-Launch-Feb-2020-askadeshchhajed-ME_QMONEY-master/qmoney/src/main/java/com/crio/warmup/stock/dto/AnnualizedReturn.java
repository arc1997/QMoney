package com.crio.warmup.stock.dto;

public class AnnualizedReturn {

  private final transient String symbol;
  private final transient  Double areturn;
  private final transient  Double totalReturns;

  public AnnualizedReturn(String symbol, Double areturn, Double totalReturns) {
    this.symbol = symbol;
    this.areturn = areturn;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return areturn;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }
}


