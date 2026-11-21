package com.example.demo.test;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository){
        this.stockRepository = stockRepository;
    }

    public Map getStock(){
        return stockRepository.findStock();
    }

    public Stock getStock(String id){
        return stockRepository.findStock(id);
    }

    public Map addStock(Stock stock){
        return stockRepository.addsSnack(stock);
    }

//    public String  deleteSnack2(Stock stock) {
//
//        boolean removed = stockRepository.deleteStock(stock);
//        if (!removed) {
//            throw new IllegalArgumentException("找不到商品" + stock);
//        }
//        return stock + "刪除成功";
//    }

    public String deleteStock(Stock stock) {

        Stock removed = stockRepository.deleteStock(stock);
        if (removed == null) {
            throw new IllegalArgumentException("找不到商品" + stock);
        }
        return stock.getId() + "刪除成功";
    }

//    public String updateSnack(String oldSnack, String newSnack){
//        return stockRepository.updateSnack(oldSnack, newSnack);
//    }

}
