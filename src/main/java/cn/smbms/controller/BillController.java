package cn.smbms.controller;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.bill.BillServiceImpl;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Controller
public class BillController {

    @Resource
    private BillService billService;
    @Resource
    private ProviderService providerService;

    @RequestMapping("/billmain.html")
    public String main(Model model,String queryProductName,String queryProviderId,String queryIsPayment){
        List<Provider> providerList = null;
        providerList = providerService.getProviderList("","");
        model.addAttribute("providerList", providerList);

        if(StringUtils.isNullOrEmpty(queryProductName)){
            queryProductName = "";
        }

        List<Bill> billList = null;
        Bill bill = new Bill();
        if(StringUtils.isNullOrEmpty(queryIsPayment)){
            bill.setIsPayment(0);
        }else{
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if(StringUtils.isNullOrEmpty(queryProviderId)){
            bill.setProviderId(0);
        }else{
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
        billList = billService.getBillList(bill);
        model.addAttribute("billList", billList);
        model.addAttribute("queryProductName", queryProductName);
        model.addAttribute("queryProviderId", queryProviderId);
        model.addAttribute("queryIsPayment", queryIsPayment);
        return "billlist";
    }

        //添加
    @RequestMapping("/addBill.html")
    public String billAdd(){
        return "billadd";
    }
//添加
    @RequestMapping(value = "/addBill.html",method = RequestMethod.POST)
    public String saveAdd(Bill bill,HttpSession session){
        bill.setCreationDate(new Date());
        if (billService.add(bill)){
            return "redirect:/billmain.html";
        }
        return "billadd";

    }

    @RequestMapping("/providerList")
    @ResponseBody
    public List<Provider> providerList(){
        return providerService.getProviderList(null,null);
    }

    //修改
    @RequestMapping("/billmodify.html")
    public String modify(String id,Model model){
        Bill bill=billService.getBillById(id);
        model.addAttribute("bill",bill);
        return "billmodify";
    }
    @RequestMapping(value = "/billmodify.html",method = RequestMethod.POST)
        public String savemodify(Bill bill,HttpSession session){
        bill.setCreationDate(new Date());
            User bill1=(User) session.getAttribute(Constants.USER_SESSION);
            bill.setCreatedBy(bill1.getId());
            System.out.println(bill);
            if (billService.modify(bill)){
                return "redirect:/billmain.html";
            }
        return "billmodify";
        }

        //查看
        @RequestMapping(value = "/view.html")
        public String view(String id,Model model){
                Bill bill=billService.getBillById(id);
                model.addAttribute("bill",bill);
        return "billview";
        }



}
