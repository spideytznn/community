package life.majiang.community.controller;

import life.majiang.community.dto.PageDTO;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model,@RequestParam(name="page",defaultValue = "1")Integer page,@RequestParam(name="size",defaultValue = "5")Integer size){
        Cookie[] cookies = request.getCookies();
        if(cookies==null&&cookies.length==0)
            return "index";
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("token")){//找到cookies中名字为token的值把value赋给token
                String token=cookie.getValue();
                User user=userMapper.findByToken(token);
                if(user!=null){
                    request.getSession().setAttribute("user",user);
                }
                break;
            }
        }
        PageDTO pageDTO=questionService.list(page,size);
        model.addAttribute("pageDTO",pageDTO);
        return "index";
    }
}
