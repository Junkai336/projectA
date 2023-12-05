package com.example.member.dto;

import com.example.member.entity.Board;
import com.example.member.entity.Comment;
import com.example.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class CommentDto {

    private Long id;
    @NotBlank(message = "댓글을 작성 후 등록해주세요.")
    private String comment;

    private Member member;

    private LocalDateTime createdByTime;

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setMember(comment.getMember());
        commentDto.setComment(comment.getComment());
        commentDto.setCreatedByTime(LocalDateTime.now());
        return commentDto;
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> commentList){
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentDto commentDto = toCommentDto(comment);
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }

}