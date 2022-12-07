package com.crio.warmup.stock.dto;

import java.io.Serializable;
import java.util.Comparator;

public class Sortby implements Comparator<AnnualizedReturn>,Serializable {
  private static final long serialVersionUID = 1L;

  @Override
  public int compare(AnnualizedReturn o1, AnnualizedReturn o2) {
    if (o1.getAnnualizedReturn() > o2.getAnnualizedReturn()) {
      return -1; 
    } else {
      return 1; 
    }
  }
}