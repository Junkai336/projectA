package com.example.member.article;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;


    @GetMapping(value = "/articleForm")
    public String articleForm(Model model){

        model.addAttribute("articleDto", new ArticleDto());
        return "article/articleForm";
    }

    // 게시글 등록 후 리스트로 돌아간다.
    @PostMapping(value = "/articleForm")
    public String newArticle(@Valid ArticleDto articleDto, BindingResult result
    , Model model, Principal principal){
        try{
            String email = principal.getName();
            articleService.createArticle(email, articleDto);
        }catch (Exception e){
            model.addAttribute("errorMessage",result.getFieldError());
        }

        return "redirect:/article/list";
    }

    // 게시판 -> 게시글 리스트
    @GetMapping(value = "/list")
    public String list(Model model){
        try{
            List<ArticleDto> articleDtoList = articleService.showList();
            model.addAttribute("articleDtoList", articleDtoList);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "article/list";
    }

    // 게시글 보기
    @GetMapping(value = "/{article_id}")
    public String detail(@PathVariable Long article_id, Model model){

        try{
            ArticleDto articleDto = articleService.articleDetail(article_id);
            model.addAttribute("articleDto", articleDto);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "article/detail";
    }



    // 게시글 삭제
    @GetMapping(value ="/{article_id}/Delete")
    public String deleteArticle(@PathVariable Long article_id, Model model,
                                Principal principal){
        String email = principal.getName();
        try{
            articleService.deleteArticle(article_id, email);
        }catch (Exception e){
            model.addAttribute("errorMessage", "권한이 없습니다.");
        }
        return "redirect:/article/list";
    }

}
