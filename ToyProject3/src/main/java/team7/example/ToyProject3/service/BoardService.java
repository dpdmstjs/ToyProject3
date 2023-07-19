package team7.example.ToyProject3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team7.example.ToyProject3.domain.board.Board;
import team7.example.ToyProject3.domain.board.BoardStatus;
import team7.example.ToyProject3.domain.board.BoardType;
import team7.example.ToyProject3.domain.user.User;
import team7.example.ToyProject3.dto.board.BoardRequest;
import team7.example.ToyProject3.dto.board.BoardResponse;
import team7.example.ToyProject3.exception.BoardException;
import team7.example.ToyProject3.exception.ErrorCode;
import team7.example.ToyProject3.repository.BoardRepository;
import team7.example.ToyProject3.util.BoardContentParseUtil;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public void savaBoard(BoardRequest.saveBoardDTO saveBoardDTO, User user) {
        BoardRequest.saveBoardDTO parseBoard = BoardContentParseUtil.parse(saveBoardDTO);
        Board board = parseBoard.toEntity(user);
        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponse.BoardListDTO> findAll(String boardType, String search, Pageable pageable) {
        BoardType type = BoardType.valueOf(BoardType.class, boardType.toUpperCase());

        Page<Board> boardList = boardRepository.findAll(search, type, pageable, BoardStatus.ENABLED);

        return boardList.map(board -> BoardResponse.BoardListDTO.builder()
                .boardId(board.getId())
                .thumbnailContent(board.getThumbnailContent())
                .title(board.getTitle())
                .thumbnail(board.getThumbnail())
                .nickName(board.getUser().getNickname())
                .build());
    }

    @Transactional(readOnly = true)
    public BoardResponse.BoardDetailDTO getDetailBoard(Long boardId) {
        Board board = boardRepository.findBoardByIdAndBoardStatus(boardId, BoardStatus.ENABLED)
                .orElseThrow(() -> new BoardException(ErrorCode.ERROR_BOARD_ENTITY_NOT_FOUND));

        return BoardResponse.BoardDetailDTO.toDTO(board);
    }

    @Transactional
    public void deleteBoard(Long boardId, User user) {
        Board board = boardRepository.findBoardByIdAndUserId(boardId, user.getId())
                .orElseThrow(() -> new BoardException(ErrorCode.ERROR_BOARD_ENTITY_NOT_FOUND));
        boardRepository.deleteById(board.getId());
    }

    @Transactional
    public void updateBoard(BoardRequest.updateBoardDTO updateBoardDTO, Long boardId) {
        Board board = boardRepository.findBoardByIdAndBoardStatus(boardId, BoardStatus.ENABLED)
                .orElseThrow(() -> new BoardException(ErrorCode.ERROR_BOARD_ENTITY_NOT_FOUND));
        board.updateBoard(updateBoardDTO);
    }

    @Transactional
    public BoardResponse.BoardUpdateDTO updateBoardForm(Long boardId, User user) {
        Board board = boardRepository.findBoardByIdAndUserId(boardId, user.getId())
                .orElseThrow(() -> new BoardException(ErrorCode.ERROR_BOARD_ENTITY_NOT_FOUND));

        return BoardResponse.BoardUpdateDTO.toDTO(board);
    }

}
