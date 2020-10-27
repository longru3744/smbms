package cn.smbms.controller;


import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.role.RoleServiceImpl;
import cn.smbms.service.user.UserService;
import cn.smbms.service.user.UserServiceImpl;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.json.JsonArray;
import javax.mail.Multipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    //跳转登录页面
    @RequestMapping("/login.html")
    public String login() {
        return "login";
    }

    //返回登录
    @RequestMapping("/loginout.html")
    public String loginout(HttpSession session) {
        session.removeAttribute(Constants.USER_SESSION);

        return "redirect:/user/login.html";

    }

    //处理登录页面
    @RequestMapping(value = "/login.html", method = RequestMethod.POST)
    public String doLogin(String userCode, String userPassword, HttpSession session, HttpServletRequest request) {
        User user = userService.login(userCode, userPassword);
        if (user != null) {
            session.setAttribute(Constants.USER_SESSION, user);
            return "redirect:/user/frame.html";
        } else {
            request.setAttribute("error", "用户名和密码不符");
            return "login";
        }

    }


    //跳转页面登录权限
    @RequestMapping("/frame.html")
    public String frame(HttpSession session) {
        User user = (User) session.getAttribute(Constants.USER_SESSION);
//        int num=5/0;
        if (user == null) {
            return "login";
        }
        return "frame";
    }

    //    查看方法
    @RequestMapping("/userlist.html")
    public String main(Model model, String queryname, @RequestParam(value = "queryUserRole", defaultValue = "0") Integer userRole,
                       @RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex) {
        //查询用户列表
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;

        System.out.println("queryUserName servlet--------" + queryname);
        System.out.println("queryUserRole servlet--------" + userRole);
        System.out.println("query pageIndex--------- > " + pageIndex);
        //总数量（表）
        int totalCount = userService.getUserCount(queryname, userRole);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(pageIndex);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if (pageIndex < 1) {
            pageIndex = 1;
        } else if (pageIndex > totalPageCount) {
            pageIndex = totalPageCount;
        }

        userList = userService.getUserList(queryname, userRole, pageIndex, pageSize);
        model.addAttribute("userList", userList);
        List<Role> roleList = null;
        roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        model.addAttribute("roleList", roleList);
        model.addAttribute("queryUserName", queryname);
        model.addAttribute("queryUserRole", queryname);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", pageIndex);
        return "userlist";
    }


    //添加
//    @RequestMapping(value = "/add.html",method = RequestMethod.GET)
//    public String add(@ModelAttribute("user") User user){
//        return "user/useradd";
//    }
//添加
//    @RequestMapping(value = "/add.html",method = RequestMethod.POST)
//    public String addSave(User user,HttpSession session){
//        user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
//        user.setCreationDate(new Date());
//        if (userService.add(user)){
//            return "redirect:/user/userlist.html";
//        }
//        return "user/useradd";
//    }

//    @ExceptionHandler(value = Exception.class)
//    public String ex(Exception e){
//        return "";
//    }


    //跳转添加页面
    @RequestMapping("/addUser.html")
    public String addUser(@ModelAttribute User user) {
        return "useradd";
    }
    //处理添加的功能
//    @RequestMapping(value = "/addUser.html" ,method = RequestMethod.POST)
//    public String saveUser(User user,HttpSession session){
//        user.setCreationDate(new Date());//创建时间
//        //创建者
//        User user_login= (User) session.getAttribute(Constants.USER_SESSION);
//        user.setCreatedBy(user_login.getId());
//        if (userService.add(user)){
//            return "redirect:/user/userlist.html";
//        }
//        return "useradd";
//    }

    //单文件上传
    @RequestMapping(value = "/addUser.html", method = RequestMethod.POST)
    public String saveUser(User user, HttpServletRequest request, @RequestParam(value = "attan", required = false) MultipartFile attan) {
        String picPath = "";//路径
        String newFileName="";
        if (!attan.isEmpty()) {//判断是否有路径
            String path = request.getServletContext().getRealPath("/statics") + File.separator + "fileUpload\\";//文件上传的路径 上传到哪去
            if (attan.getSize() > 500000) {//判断文件大小
                request.setAttribute("error", "文件太大，上传失败");
                return "useradd";
            }
            List<String> suffexs = Arrays.asList(new String[]{".jpg", ".jfif", ".png", ".gif"});
            String oldFileName = attan.getOriginalFilename();//获取文件明
            String suffex = oldFileName.substring(oldFileName.lastIndexOf("."), oldFileName.length());
            System.out.println("源文件后缀：" + suffex);
            if (!suffexs.contains(suffex)) {
                request.setAttribute("error", "文件类型错误，上传失败");
                return "useradd";
            }
             newFileName = System.currentTimeMillis() + "" + new Random().nextInt(100000) + suffex;
            File file = new File(path, newFileName);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                attan.transferTo(file);
            } catch (IOException e) {
                e.printStackTrace();
                request.setAttribute("error", "上传失败");
                return "useradd";
            }
            picPath = path + newFileName;//图片路径
        }
        user.setPicPath(newFileName);

        user.setCreationDate(new Date());//创建时间
        //创建者
        User user_login = (User) request.getSession().getAttribute(Constants.USER_SESSION);
        user.setCreatedBy(user_login.getId());
        if (userService.add(user)) {
            return "redirect:/user/userlist.html";
        }
        return "useradd";
    }


    //处理修改页面
    @RequestMapping("/modify.html")
    public String modify(String uid, Model model) {
        User user = userService.getUserById(uid);
        model.addAttribute("user", user);
        return "usermodify";
    }

    @RequestMapping(value = "/modify.html", method = RequestMethod.POST)
    public String saveModify(User user, HttpSession session) {
        user.setModifyDate(new Date());//创建时间
        //创建者
        User user_login = (User) session.getAttribute(Constants.USER_SESSION);
        user.setModifyBy(user_login.getId());
        System.out.println(user);
        if (userService.modify(user)) {
            return "redirect:/user/userlist.html";
        }
        return "usermodify";
    }


//    //查看处理
//    @RequestMapping(value = "/view.html/{id}")
//    public String view(@PathVariable String id, Model model) {
//        User user = userService.getUserById(id);
//        String picPath = user.getPicPath();
//        System.out.println(picPath);
//       /* user.setPicPath(picPath.substring(picPath.lastIndexOf("\\") + 1));*/
//
//        model.addAttribute("user", user);
//        return "userview";
//    }
    @RequestMapping(value = "/view"/*,produces = {"application/json;charsetUTF-8"}*/)
    @ResponseBody
    public User view(String id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return user;
    }

@RequestMapping("/pwdmodify.html")
    public String pwdmodify(String id,Model model){
        User user=userService.getUserById(id);
        model.addAttribute("user",user);
        return "pwdmodify";
    }

    @RequestMapping(value = "/pwdmodify.html",method = RequestMethod.POST)
        public String savepwdModify(User user,HttpSession session){
        User user1=(User) session.getAttribute(Constants.USER_SESSION);
        user.setModifyBy(user.getId());
        System.out.println(user);
        if (userService.modify(user)){
            return "redirect:/user/userlist.html";
        }
        return "pwdmodify";
        }

        public String del(){
        return "";
        }
}






