package com.example.demo.test;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class StockController {
    private final StockService stockService;
    public StockController(StockService service){
        this.stockService =service;
    }

    @GetMapping("/stock")
    public Map Snack() {
        return stockService.getStock();
    }

    //  以 id 搜尋
    @GetMapping("/stock/{id}")
    public Stock Snack(@PathVariable("id") String id) {
        return stockService.getStock(id);
    }

    //  上傳完整的 Stock 物件
    @PostMapping("/stock")
    public Map addSnack(@RequestBody Stock stock ){
        return stockService.addStock(stock);
    }

    @DeleteMapping("/stock")
    public String deleteSnack(@RequestBody Stock stock){
        return stockService.deleteStock(stock);

        }
//
//    @PutMapping("/stock")
//    public String updateSnack(@RequestBody StockUpdateRequest request) {
//        return stockService.updateSnack(
//        request.getOldStock(),
//        request.getNewStock()
//                );
//    }
}