package com.example.member.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Override
    List<Article> findAll();

    @Query(value = "select * from article a where a.member_id= :member_id",nativeQuery = true)
    List<Article> findAllByMemberId(@Param("member_id")Long member_id);

    @Query(value = "select a from Article a order by id desc")
    List<Article> findArticles(Pageable pageable);

    @Query(value = "select count(a) from Article a")
    Long countArticle();

    @Query(value = "select * from article where category_status='NOTICE' ORDER BY reg_time desc limit 5",nativeQuery = true)
    List<Article> findArticlesNotice();

    @Query(value = "select * from article where category_status='EVENT' ORDER BY reg_time desc limit 5",nativeQuery = true)
    List<Article> findArticlesEvent();
}

