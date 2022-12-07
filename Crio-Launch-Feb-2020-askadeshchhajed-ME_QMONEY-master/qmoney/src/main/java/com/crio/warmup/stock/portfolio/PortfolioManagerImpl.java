package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {




  

  



  private RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  //CHECKSTYLE:OFF
  
 




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo thirdparty APIs to a separate function.
  //  It should be split into fto parts.
  //  Part#1 - Prepare the Url to call Tiingo based on a template constant,
  //  by replacing the placeholders.
  //  Constant should look like
  //  https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  //  Where ? are replaced with something similar to <ticker> and then actual url produced by
  //  replacing the placeholders with actual parameters.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        
       
        ObjectMapper mapper = new ObjectMapper();
        
        String url= buildUri(symbol, from, to);
        String result = restTemplate.getForObject(url, String.class);
        List<Candle> collection = mapper.readValue(result, 
              new TypeReference<ArrayList<Candle>>() {
                });
               
       
                
          
        return collection;
        
        
      
  }
 
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate + "&endDate=" + endDate
       + "&token=886114343d02aacc3e9322d2833d219d4714c450";
       return uriTemplate;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {

    List<AnnualizedReturn> l2 = new ArrayList<AnnualizedReturn>();
    Iterator<PortfolioTrade> iterator= portfolioTrades.listIterator();
    ArrayList<Double> open = new ArrayList<Double>();
      ArrayList<Double> close = new ArrayList<Double>();
    while (iterator.hasNext()) {
      PortfolioTrade p = iterator.next();
      String s = p.getSymbol();
      
      LocalDate d = p.getPurchaseDate();
      ArrayList<Double> opent = new ArrayList<Double>();
      ArrayList<Double> closet = new ArrayList<Double>();
      
     
      
     
      

      try {
        if (d.compareTo(endDate) > 0) {
          throw new RuntimeException();
        } else {
          List<Candle> candle = getStockQuote(s, d, endDate);
          Iterator<Candle> it = candle.listIterator();
          while (it.hasNext()) {
            Candle adi = it.next();
            opent.add(adi.getOpen());
            closet.add(adi.getClose());
         }
         open.add(opent.get(0));
         close.add(closet.get(closet.size()-1));
         opent.clear();
         closet.clear();
         for(int i=0;i<opent.size();i++)
         {
           System.out.println(opent.get(i));
         }
        }
      } catch (JsonProcessingException e) {
      } catch(NoSuchElementException e){
        
      }
      
      
        }
        int i = 0;
        int j = 0;
        Iterator<PortfolioTrade> iter = portfolioTrades.listIterator();
        while (iter.hasNext() && i < open.size() && j < close.size()) {
          PortfolioTrade t = iter.next();
          
          AnnualizedReturn ann = calculateReturns(endDate, t, open.get(i), close.get(j));
          
          l2.add(ann);
          i++;
          j++;
        }
           
    
   
    Collections.sort(l2,getComparator());
   
    
   
    
    
    
    
    return l2;
  }
    
    
    public static AnnualizedReturn calculateReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    Double totalReturn = (sellPrice - buyPrice) / buyPrice;
    Double annualizedreturns = (double) 0;
    LocalDate startdate = trade.getPurchaseDate();
    Double years = (double) (startdate.until(endDate, ChronoUnit.DAYS) / 365.2425f);
    if (years > 0) {
      annualizedreturns = Math.pow((1 + totalReturn),(1 / years)) - 1;
    }
    
        
        
        

    return new AnnualizedReturn(trade.getSymbol(),annualizedreturns,totalReturn);
  }
}
