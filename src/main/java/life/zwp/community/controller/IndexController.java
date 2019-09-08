package life.zwp.community.controller;

import life.zwp.community.dto.PaginationDTO;
import life.zwp.community.service.QuestionService;
import life.zwp.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController extends BaseController{
    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @GetMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model,
                        @RequestParam(value = "page",defaultValue = "1") Integer page,
                        @RequestParam(value = "size",defaultValue = "10") Integer size){
        getUserFormSession(request);
        //获取首页问题列表,和发布用户头像
        //userId =0 ,查询所有人的问题
        PaginationDTO paginationDTO =  questionService.findQuestion(page,size);
        model.addAttribute("paginationDTO",paginationDTO);
        return "index";
    }


}
