package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.Sortby;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  public static List<String> mainReadFile(String[] args) throws 
      IOException, URISyntaxException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    ArrayList<String> l = new ArrayList<String>();
    String path = args[0];
    try {
      List<PortfolioTrade> user = mapper.readValue(
          Paths.get(Thread.currentThread().getContextClassLoader()
          .getResource(path).toURI()).toFile(),
            new TypeReference<List<PortfolioTrade>>() {
            });

      Iterator<PortfolioTrade> iterator = user.listIterator();
      while (iterator.hasNext()) {
        PortfolioTrade p = iterator.next();
        l.add(p.getSymbol());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return l;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }
  
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader()
    .getResource(filename).toURI()).toFile();
  }



  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/askadeshchhajed-ME_QMONEY/"
        + "qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@4dc8caa7";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "23:1";
    return Arrays.asList(new String[] { valueOfArgument0, 
      resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }

  // Copy the relavent code from #mainReadFile to parse the Json into
  // PortfolioTrade list.
  // Now That you have the list of PortfolioTrade already populated in module#1
  // For each stock symbol in the portfolio trades,
  // Call Tiingo api
  // (https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=&endDate=&token=)
  // with
  // 1. ticker = symbol in portfolio_trade
  // 2. startDate = purchaseDate in portfolio_trade.
  // 3. endDate = args[1]
  // Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>
  // Note - You may have to register on Tiingo to get the api_token.
  // Please refer the the module documentation for the steps.
  // Find out the closing price of the stock on the end_date and
  // return the list of all symbols in ascending order by its close value on
  // endDate
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // And make sure that its printing correct results.

  public static List<String> mainReadQuotes(String[] args) throws IOException,
      URISyntaxException,JsonMappingException,JsonGenerationException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    ArrayList<Double> l1 = new ArrayList<Double>();
    String path = args[0];

    List<PortfolioTrade> user = mapper.readValue(
        Paths.get(Thread.currentThread().getContextClassLoader()
        .getResource(path).toURI()).toFile(),
        new TypeReference<List<PortfolioTrade>>() {
          });

    Iterator<PortfolioTrade> iterator = user.listIterator();
    Map<Double, String> hm = new HashMap<Double, String>();
    while (iterator.hasNext()) {
      PortfolioTrade p = iterator.next();
      String s = p.getSymbol();
      LocalDate d = p.getPurchaseDate();
      String date = args[1];
      LocalDate enddate = LocalDate.parse(date);

      try {
        if (d.compareTo(enddate) > 0) {
          throw new RuntimeException();
        } else {
          String url = "https://api.tiingo.com/tiingo/daily/" + s + "/prices?startDate=" + d + "&endDate=" + args[1]
              + "&token=886114343d02aacc3e9322d2833d219d4714c450";
          RestTemplate restTemplate = new RestTemplate();

          String result = restTemplate.getForObject(url, String.class);
          List<TiingoCandle> collection = mapper.readValue(result, 
              new TypeReference<ArrayList<TiingoCandle>>() {
                });

          Iterator<TiingoCandle> it = collection.listIterator();
          while (it.hasNext()) {
            TiingoCandle adi = it.next();
    
            l1.add(adi.getClose());
            hm.put(adi.getClose(), p.getSymbol());
          }
         
        }
      } catch (RuntimeException e) {
        throw e;
      } 
    }
    TreeSet<Double> t = new TreeSet<Double>(l1);
    List<Double> ls1 = new ArrayList<Double>(t);
    
    Collections.sort(ls1);
    List<String> lp = new ArrayList<String>();
    for (int i = 0;i < ls1.size();i++) {
      String s = hm.get(ls1.get(i));
      if (!lp.contains(s)) {
        lp.add(s);

      }

    }
    return lp;

  }

 








  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory,
  //  Create PortfolioManager using PortfoliomanagerFactory,
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.
  //  Test the same using the same commands as you used in module 3
  //  use gralde command like below to test your code
  //  ./gradlew run --args="trades.json 2020-01-01"
  //  ./gradlew run --args="trades.json 2019-07-01"
  //  ./gradlew run --args="trades.json 2019-12-03"
  //   where trades.json is your json file

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args) throws 
       IOException, URISyntaxException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    ArrayList<Double> open = new ArrayList<Double>();
    ArrayList<Double> close = new ArrayList<Double>();
    ArrayList<AnnualizedReturn> l2 = new ArrayList<AnnualizedReturn>();
    

    
    String path = args[0];

    List<PortfolioTrade> user = mapper.readValue(
        Paths.get(Thread.currentThread().getContextClassLoader()
        .getResource(path).toURI()).toFile(),
          new TypeReference<List<PortfolioTrade>>() {
          });

    Iterator<PortfolioTrade> iterator = user.listIterator();
   
    while (iterator.hasNext()) {
      PortfolioTrade p = iterator.next();
      String s = p.getSymbol();
      
      LocalDate d = p.getPurchaseDate();
     
      String date = args[1];
      LocalDate enddate = LocalDate.parse(date);
     
      

      try {
        if (d.compareTo(enddate) > 0) {
          throw new RuntimeException();
        } else {
          String url = "https://api.tiingo.com/tiingo/daily/" + s + "/prices?startDate=" + d + "&endDate=" + args[1]
              + "&token=886114343d02aacc3e9322d2833d219d4714c450";
          RestTemplate restTemplate = new RestTemplate();
          ArrayList<Double> opent = new ArrayList<Double>();
          ArrayList<Double> closet = new ArrayList<Double>();

          String result = restTemplate.getForObject(url, String.class);
          List<TiingoCandle> collection = mapper.readValue(result, 
              new TypeReference<ArrayList<TiingoCandle>>() {
                });

          Iterator<TiingoCandle> it = collection.listIterator();
          while (it.hasNext()) {
            TiingoCandle adi = it.next();
            opent.add(adi.getOpen());
            closet.add(adi.getClose());
            
            
            
          }
          open.add(opent.get(0));
          close.add(closet.get(closet.size() - 1));
          opent.clear();
          closet.clear();

        }
      } catch (RuntimeException e) {
        throw e;
      }
      
      
    }
    
    int i = 0;
    int j = 0;
    Iterator<PortfolioTrade> iter = user.listIterator();
    while (iter.hasNext() && i < open.size() && j < close.size()) {
      PortfolioTrade t = iter.next();
      
      LocalDate enddate = LocalDate.parse(args[1]);
      AnnualizedReturn ann = calculateAnnualizedReturns(enddate, t, open.get(i), close.get(j));
      
      l2.add(ann);
      i++;
      j++;
    }
   
    Collections.sort(l2, new Sortby());
    
   
    
    
    
    
    return l2;
  }
   
    
   
    

   
  

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  annualized returns should be calculated in two steps -
  //  1. Calculate totalReturn = (sell_value - buy_value) / buy_value
  //  Store the same as totalReturns
  //  2. calculate extrapolated annualized returns by scaling the same in years span. The formula is
  //  annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //  Store the same as annualized_returns
  //  return the populated list of AnnualizedReturn for all stocks,
  //  Test the same using below specified command. The build should be successful
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
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

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory,
  //  Create PortfolioManager using PortfoliomanagerFactory,
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.
  //  Test the same using the same commands as you used in module 3
  //  use gralde command like below to test your code
  //  ./gradlew run --args="trades.json 2020-01-01"
  //  ./gradlew run --args="trades.json 2019-07-01"
  //  ./gradlew run --args="trades.json 2019-12-03"
  //  where trades.json is your json file
  //  Confirm that you are getting same results as in Module3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       
    LocalDate endDate = LocalDate.parse(args[1]);
    // String contents = readFileAsString(file);
    ObjectMapper objectMapper = getObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    String path = args[0];

    List<PortfolioTrade> user = objectMapper.readValue(
        Paths.get(Thread.currentThread().getContextClassLoader()
        .getResource(path).toURI()).toFile(),
          new TypeReference<List<PortfolioTrade>>() {
          });
    PortfolioTrade[] portfolioTrades = new PortfolioTrade[3];
    Iterator<PortfolioTrade> iterator = user.listIterator();
    int i = 0;
    while (iterator.hasNext() && i < portfolioTrades.length) {
      portfolioTrades[i] = iterator.next();
      i++;

    }
    RestTemplate restTemplate = new RestTemplate();
    PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);
    return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }
  

  











  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());




    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

