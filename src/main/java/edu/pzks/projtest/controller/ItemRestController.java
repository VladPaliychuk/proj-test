package edu.pzks.projtest.controller;


import edu.pzks.projtest.model.Item;
import edu.pzks.projtest.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/*
  @author   george
  @project   proj-test
  @class  ItemRestController
  @version  1.0.0 
  @since 09.09.24 - 12.01
*/
@RestController
@RequestMapping("api/v1/items")
@RequiredArgsConstructor
public class ItemRestController {
    private final ItemService itemService;

    @RequestMapping("/hello")
    public String hello() {
        return "<h1> !!!!!!! hello   !!!!!!!!!!!!!!!! <h1>" ;
    }

    @RequestMapping("/")
    public List<Item> showAll() {
        return itemService.getItems();
    }

    @RequestMapping("/id")
    public Item showOne() {
        return null;
    }





}
