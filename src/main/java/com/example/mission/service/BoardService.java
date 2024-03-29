package com.example.mission.service;

import com.example.mission.dto.AllArticleDto;
import com.example.mission.dto.BoardDto;
import com.example.mission.entity.Article;
import com.example.mission.entity.Board;
import com.example.mission.entity.BoardCategory;
import com.example.mission.repo.ArticleRepository;
import com.example.mission.repo.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final ArticleRepository articleRepository;
    private final BoardRepository boardRepository;

    // 1. 전체 게시판 카테고리 불러오기
    public List<BoardDto> readBoardCategories() {
        List<BoardDto> dtoList = new ArrayList<>();
        for (Board board : boardRepository.findAll()) {
            dtoList.add(BoardDto.fromEntity(board));
        }
        return dtoList;
    }

    // 1. 게시판아이디로 게시판 카테고리 불러오기
    public BoardDto findByBoardId(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        return BoardDto.fromEntity(board);
    }

    // 1. 전체 카테고리 게시물 불러오기
    public List<AllArticleDto> readAllArticles() {
        List<AllArticleDto> dtoList = new ArrayList<>();
        for (Article article : articleRepository.findAllByOrderByCreateDateDesc()) {
            dtoList.add(AllArticleDto.fromEntity(article));
        }
        return dtoList;
    }

    // 1. 카테고리별 게시물 전체 불러오기
    public List<AllArticleDto> readAllArticlesByBoardId(Long boardId) {
        List<AllArticleDto> dtoList = new ArrayList<>();
        for (Article article : articleRepository.findAllByBoardIdOrderByCreateDateDesc(boardId)) {
            dtoList.add(AllArticleDto.fromEntity(article));
        }
        return dtoList;
    }

    // 게시글 id로 board 찾기
    public BoardDto findBoardByArticleId(Long articleId) {
        return BoardDto.fromEntity(boardRepository.findBoardByArticleListId(articleId));
    }


    //5. 전체 게시물중 검색결과 가져오기
    public List<AllArticleDto> searchArticles(String criteria, String searchString, BoardCategory category) {
        List<AllArticleDto> dtoList = new ArrayList<>();

        // 카테고리가 없을 경우 + 기준에 따른 검색 결과
        if (category==null) {
            if ("title".equals(criteria)) {
                dtoList.addAll(articleRepository.findAllByTitleContaining(searchString)
                        .stream()
                        .map(AllArticleDto::fromEntity)
                        .collect(Collectors.toList()));
            } else if ("content".equals(criteria)) {
                dtoList.addAll(articleRepository.findAllByContentContaining(searchString)
                        .stream()
                        .map(AllArticleDto::fromEntity)
                        .collect(Collectors.toList()));
            } else {
                dtoList.addAll(articleRepository.findAllByTitleContainingOrContentContaining(searchString, searchString)
                        .stream()
                        .map(AllArticleDto::fromEntity)
                        .collect(Collectors.toList()));
            }
        } else {
            // 카테고리와 기준에 따른 검색 결과
            if ("title".equals(criteria)) {
                dtoList.addAll(articleRepository.findAllByTitleContainingAndBoard_Category(searchString, category)
                        .stream()
                        .map(AllArticleDto::fromEntity)
                        .collect(Collectors.toList()));
            } else if ("content".equals(criteria)) {
                dtoList.addAll(articleRepository.findAllByContentContainingAndBoard_Category(searchString, category)
                        .stream()
                        .map(AllArticleDto::fromEntity)
                        .collect(Collectors.toList()));
            } else {
                dtoList.addAll(articleRepository.findAllByTitleContainingOrContentContainingAndBoard_Category(searchString, searchString, category)
                        .stream()
                        .map(AllArticleDto::fromEntity)
                        .collect(Collectors.toList()));
            }
        }
        return dtoList;
    }
}
