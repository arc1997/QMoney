
package com.crio.warmup.stock.dto;

import java.time.LocalDate;
import java.util.Collection;

public interface Candle {

  Double getOpen();

  Double getClose();

  Double getHigh();

  Double getLow();

  LocalDate getDate();
}
