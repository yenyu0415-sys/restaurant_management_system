package com.example.demo.test;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StockRepository {

    public Map<String, Stock> stocks = new HashMap<>();

    public StockRepository() {
        stocks.put(
                "2330", //  key
                new Stock("2330", "TSMC", 2500.0)   //  value
        );
        stocks.put("0050", new Stock("0050", "台灣 50", 120.0));
        stocks.put("2454", new Stock("2454", "聯發科", 4500.0));
    }


    public Map findStock() {
        return stocks;
    }

    public Stock findStock(String id) {
        return stocks.get(id);
    }

    public Map addsSnack(Stock stock) {
        stocks.put(stock.getId(), stock);
        return stocks;
    }

    public Map deleteStock2(Stock stock) {
        stocks.remove(stock);
        return stocks;
    }

    public Stock deleteStock(Stock stock) {
        return stocks.remove(stock.getId());
    }

//    public String updateSnack(String oldSnack, String newSnack) {
//        int index = stocks.indexOf(oldSnack);
//
//        if (index != -1) {
//            stocks.set(index, newSnack);
//            return stocks + "修改成功";
//        }
//        return "找不到商品" + stocks;
//    }
}
