package cn.smbms.controller;

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class ProviderController {
    @Resource
    private ProviderService providerService;

    @RequestMapping("/providermain.html")
    public String main(Model model, String queryProName, String queryProCode) {
        if (StringUtils.isNullOrEmpty(queryProName)) {
            queryProName = "";
        }
        if (StringUtils.isNullOrEmpty(queryProCode)) {
            queryProCode = "";
        }
        List<Provider> providerList = null;
        providerList = providerService.getProviderList(queryProName, queryProCode);
        model.addAttribute("providerList", providerList);
        model.addAttribute("queryProName", queryProName);
        model.addAttribute("queryProCode", queryProCode);
        return "providerlist";
    }

    //跳转页面
    @RequestMapping("/addProvider.html")
    public String providerAdd(@ModelAttribute Provider provider) {
        return "provideradd";
    }

    @RequestMapping(value = "/addProvider.html", method = RequestMethod.POST)
    public String saveProvider(Provider provider, HttpSession session) {
        provider.setCreationDate(new Date());
        /*Provider provider1=(Provider) session.getAttribute(Constants.USER_SESSION);
        provider.setCreatedBy(provider1.getId());*/
        if (providerService.add(provider)) {
            return "redirect:/providermain.html";
        }

        return "provideradd";
    }

    //修改页面
    @RequestMapping("/modify.html")
    public String modify(String id, Model model) {
        Provider provider = providerService.getProviderById(id);
        model.addAttribute("provider", provider);

        return "providermodify";
    }
//修改
    @RequestMapping(value = "/modify.html", method = RequestMethod.POST)
    public String saveModify(Provider provider, HttpSession session) {
        provider.setCreationDate(new Date());
        User provider1 = (User) session.getAttribute(Constants.USER_SESSION);
        provider.setCreatedBy(provider1.getId());
        System.out.println(provider);
        if (providerService.modify(provider)) {
            return "redirect:/providermain.html";
        }
        return "providermodify";

    }
    //查看
            @RequestMapping(value = "/view.html/{id}")

            public String view(@PathVariable String id,Model model){
                Provider provider=providerService.getProviderById(id);
                model.addAttribute("provider",provider);
            return "providerview";
            }



}
