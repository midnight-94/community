package life.zwp.community.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import life.zwp.community.dto.PaginationDTO;
import life.zwp.community.dto.QuestionDTO;
import life.zwp.community.exception.CustomizeErrorCode;
import life.zwp.community.exception.CustomizeException;
import life.zwp.community.mapper.CommentMapper;
import life.zwp.community.mapper.QuestionMapper;
import life.zwp.community.mapper.UserMapper;
import life.zwp.community.model.Question;
import life.zwp.community.model.User;
import life.zwp.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("publishServiceImpl")
@Transactional
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Override
    public void create(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public List<Question> findAll(Integer page, Integer size) {
        //开始分页
        PageHelper.startPage(page, size);
        return questionMapper.findAll();
    }


    @Override
    public PaginationDTO findQuestion(Integer page, Integer size, String search) {
        List<QuestionDTO> questionDTOS = new ArrayList<>();
//        List<Question> questions = findAll(page, size);
////        总数据
//        Long creator = 0L;
//        Integer totalCount = questionMapper.count(creator);
//        QuestionDTO questionDTO ;
//        User user;
//        PaginationDTO paginationDTO = new PaginationDTO();
//        for (Question question : questions) {
//            questionDTO = new QuestionDTO();
//            questionDTO.setQuestion(question);
//            //根据问题的发布人查询这个用户
//            creator = question.getCreator();
//            user = userMapper.findByCreator(creator);
//            questionDTO.setUser(user);
//            questionDTOS.add(questionDTO);
//        }
        Long creator = 0L;
        PageHelper.startPage(page,size);
        List<Question> questions = questionMapper.findAllByTitle(search);
        QuestionDTO questionDTO ;
        User user;
        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            questionDTO = new QuestionDTO();
            questionDTO.setQuestion(question);
            //根据问题的发布人查询这个用户
            creator = question.getCreator();
            user = userMapper.findByCreator(creator);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        //最后一步,获取分页信息
        PageInfo info = new PageInfo<>(questions);
        Integer totalCount =  new Long(info.getTotal()).intValue();
        paginationDTO.setContent(questionDTOS);
        paginationDTO.setPaginationDTO(totalCount,page,size);
        return paginationDTO;
    }

    @Override
    public PaginationDTO findQuestionByUserId(Integer page, Integer size, Long userId) {
        //        总数据
        Long creator = userId;
        Integer totalCount = questionMapper.count(creator);
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        Integer start = (page-1)*size;
        List<Question> questions = questionMapper.findQuestionByUserId(start, size, userId);
        User user = userMapper.findByCreator(userId);
        QuestionDTO questionDTO ;
        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            questionDTO = new QuestionDTO();
            questionDTO.setQuestion(question);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setContent(questionDTOS);
        paginationDTO.setPaginationDTO(totalCount,page,size);
        return paginationDTO;
    }

    @Override
    public Question findQuestionById(Long id) {
        return questionMapper.findQuestionById(id);
    }

    @Override
    public void update(Question question) {
        questionMapper.update(question);
    }

    @Override
    public QuestionDTO findQuestionDTOById(Long id) {
        QuestionDTO questionDTO = new QuestionDTO();
        Question question = new Question();
        User user = new User();
        Map<Object,Object> questionDTOMap = questionMapper.findQuestionMapById(id);
        if(questionDTOMap ==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        question.setId(Long.valueOf(questionDTOMap.get("id").toString()));
        question.setCreator(Long.valueOf(questionDTOMap.get("creator").toString()));
        question.setTitle(questionDTOMap.get("title").toString());
        question.setTags(questionDTOMap.get("tags").toString());
        question.setDescription(questionDTOMap.get("description").toString());
        question.setCommentCount(Integer.valueOf(questionDTOMap.get("comment_count").toString()));
        question.setViewCount(Integer.valueOf(questionDTOMap.get("view_count").toString()));
        question.setLikeCount(Integer.valueOf(questionDTOMap.get("like_count").toString()));
        question.setGmtCreate(Long.valueOf(questionDTOMap.get("gmt_create").toString()));
        question.setGmtModified(Long.valueOf(questionDTOMap.get("gmt_modified").toString()));

        String name = "";
        String headUrl = "";
        String bio = "";
        if(questionDTOMap.get("name") !=null){
            name =questionDTOMap.get("name").toString();
        }
        if(questionDTOMap.get("head_url") !=null){
            headUrl = questionDTOMap.get("head_url").toString();
        }
        if(questionDTOMap.get("bio") !=null){
            bio = questionDTOMap.get("bio").toString();
        }
        user.setBio(bio);
        user.setName(name);
        user.setHeadUrl(headUrl);
        user.setAccountId(questionDTOMap.get("account_id").toString());
        questionDTO.setUser(user);
        questionDTO.setQuestion(question);
        return questionDTO;
    }

    @Override
    public void updateViewCount(Question question) {
        questionMapper.updateViewCount(question);
    }

    @Override
    public List<Map<String,Object>> commentList(Long id, Integer type) {
        List<Map<String,Object>> comments =  commentMapper.commentList(id,type);
        return comments;
    }

    @Override
    public List<Question> relatedQuestion(Long id, String tags) {
        String tag = tags;
        if(tags.contains("，")) {
            tag = tags.replace("，", "|");
        }
        List<Question> relatedQuestions = questionMapper.relatedQuestion(id,tag);
        return relatedQuestions;
    }

    @Override
    public int deleteQuestion(Question question) {
        //先查找该问题是否存在
        Question q = questionMapper.findQuestionById(question.getId());
        if(q == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        //删除这个问题
        int count = questionMapper.delete(question);
        return count;
    }

    @Override
    public Integer count(Long userId) {
        return questionMapper.count(userId);
    }

}
