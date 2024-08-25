package customer.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cds.services.request.UserInfo;

@RestController
@RequestMapping("rest")
public class MyController {
    @Autowired
    private UserInfo userInfo;
    
    @GetMapping("userInfo")
    public String userInfo() {
       return userInfo.getName();
    }
}
