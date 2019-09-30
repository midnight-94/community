package life.zwp.community.controller;

import life.zwp.community.dto.PaginationDTO;
import life.zwp.community.model.User;
import life.zwp.community.service.QuestionService;
import life.zwp.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 我的问题 -Controller
 */
@Controller("profileController")
@RequestMapping("/profile")
public class ProfileController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @GetMapping("/{action}")
    public String myQuestion(HttpServletRequest request, HttpServletResponse response, Model model,
                             @PathVariable(name = "action") String action,
                             @RequestParam(value = "page",defaultValue = "1") Integer page,
                             @RequestParam(value = "size",defaultValue = "10") Integer size
                             ){
        PaginationDTO paginationDTO = new PaginationDTO();
        //获取我发布的问题
        User user = (User)request.getSession().getAttribute("user");
        Long userId = user.getId();
        model.addAttribute("section",action);
        if("questions".equals(action)){
            model.addAttribute("sectionName","我的提问");
            paginationDTO = questionService.findQuestionByUserId(page,size,userId);
        } else if ("replies".equals(action)){
            model.addAttribute("sectionName","最新回复");
        }
        model.addAttribute("paginationDTO",paginationDTO);
        return "profile";
    }
}
