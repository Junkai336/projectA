package com.example.member.article;

import com.example.member.article.comment.CommentDto;
import com.example.member.article.comment.CommentService;
import com.example.member.entity.UploadFile;
import com.example.member.repository.UploadFileRepository;
import com.example.member.service.MainService;
import com.example.member.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;
    private final UploadFileService uploadFileService;
    private final MainService mainService;

    // 게시판 -> 게시글 리스트
    @GetMapping(value = {"/list", "/list/{page}"})
    public String list(@PathVariable("page")Optional<Integer> page, Model model) {
        try {
            uploadFileService.emptyUploadFileCheck();
            uploadFileService.backwardUploadFileCheck();

            Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 8);
            Page<ArticleDto> articleDtoList = articleService.getArticleList(pageable);

            model.addAttribute("articleDtoList", articleDtoList);
            model.addAttribute("page", pageable.getPageNumber());
            model.addAttribute("maxPage", 5);

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "article/list";
    }
    @GetMapping(value = "/new")
    public String newBoard(Model model) {

        ArticleDto articleDto = new ArticleDto();
        model.addAttribute("articleDto", articleDto);
        return "article/articleForm";

    }

    // 게시글 등록 후 리스트로 돌아간다.
    @PostMapping(value = "/articleForm")
    public String newArticle(@Valid ArticleDto articleDto, BindingResult result
            , Model model, Principal principal) {
        // 이미 작성되었던 글이었던 경우(수정 시)
        if (articleDto.getId() != null) {
            Long article_id = articleDto.getId();
            try {
                uploadFileService.havingIdDelete();
                articleService.articleUpdate(articleDto);

                model.addAttribute("articleDto", articleDto);
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
            }
            return "redirect:/article/" + article_id;
        } else {
            // 새로 작성되는 글일 경우
            try {
                String email = principal.getName();
                articleService.saveArticle(articleDto, email);
            } catch (Exception e) {
                model.addAttribute("errorMessage", result.getFieldError());
            }

            return "redirect:/article/list";
        }
    }
    @GetMapping(value = "/{id}")
    public String show (@PathVariable Long id, Model model, Principal principal) {
        uploadFileService.backwardUploadFileCheck(id);
        String email = principal.getName();
        model.addAttribute("userEmail", email);
        ArticleDto articleDto =  articleService.findArticle(id);
        articleDto.setRegDateStr(mainService.localDateToString(articleDto));
        // model에 선택한 글의 id를 가지고 글의 내용을 들고 있는 Dto 객체를 추가
        model.addAttribute("articleDto", articleDto);
        // 글의 id값을 가지고 있는 comment를 List로 하여 model에 추가
        List<CommentDto> commentDtoList = commentService.commentDtoList(id);
        model.addAttribute("commentDtoList", commentDtoList);
        // 새로 입력받을 commentDto 객체를 넘겨준다.
        model.addAttribute("newCommentDto", new CommentDto());
        return "article/detail";
    }

    @GetMapping(value = "/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        uploadFileService.refreshUploadFileCheck(id);
        ArticleDto articleDto = articleService.findArticle(id);
        model.addAttribute("articleDto", articleDto);

        return "article/articleForm";
    }

    @PostMapping(value = "/articleUpdate")
    public String update(@Valid ArticleDto articleDto, BindingResult result, Model model) {

        try{
            articleService.articleUpdate(articleDto);
        }catch (Exception e){
            model.addAttribute("errorMsg", result.getFieldError());
        }


        return "redirect:/article/list";
    }

    @GetMapping(value = "/{id}/delete")
    public String delete(@PathVariable Long id, Model model) {

        try{
            // 삭제 시 해당 게시글에 달린 댓글을 삭제함
            commentService.deleteAllByRoomId(id);
            articleService.articleDelete(id);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            System.out.println("nnn article delete catch" + e.getMessage());
        }
        return "redirect:/article/list";
    }


}