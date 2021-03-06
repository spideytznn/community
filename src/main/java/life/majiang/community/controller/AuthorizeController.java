package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper usermapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code")String code, @RequestParam(name="state")String state, HttpServletRequest request, HttpServletResponse response){
        AccessTokenDTO accessTokenDTO=new AccessTokenDTO();
        try {
            accessTokenDTO.setCode(code);
            accessTokenDTO.setRedirect_uri(redirectUri);
            accessTokenDTO.setState(state);
            accessTokenDTO.setClient_id(clientId);
            accessTokenDTO.setClient_secret(clientSecret);
            String accessToken =githubProvider.getAccessToken(accessTokenDTO);
            GithubUser githubUser=githubProvider.getUser(accessToken);
            if(githubUser!=null&&githubUser.getId()!=null){
                User user=new User();
                String token = UUID.randomUUID().toString();
                user.setToken(token);
                user.setName(githubUser.getName());
                user.setAccountId(String.valueOf(githubUser.getId()));
                user.setGmtCreate(System.currentTimeMillis());
                user.setGmtModified(user.getGmtCreate());
                user.setBio(githubUser.getBio());
                user.setAvatarUrl(githubUser.getAvatarUrl());
                usermapper.insert(user);
                response.addCookie(new Cookie("token",token));
                //??????????????????cookie???session
                //request.getSession().setAttribute("user",githubUser);
                return "redirect:/";
            }else{
                //???????????????????????????
                return "redirect:/";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }
}
