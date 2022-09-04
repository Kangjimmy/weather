package zerobase.weather;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JpaMemoRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {

        //given
        //when
        jpaMemoRepository.save(
                new Memo(2, "insert test using jpa")
        );

        //then
        List<Memo> memoList = jpaMemoRepository.findAll();
        Assertions.assertTrue(memoList.size() > 0);
    }

    @Test
    void findByIdTest() {
        //given
        Memo newMemo = new Memo(11, "jpa");

        //when
        Memo memo = jpaMemoRepository.save(newMemo);
        System.out.println(memo.getId());

        //then
        Optional<Memo> result = jpaMemoRepository.findById(memo.getId());

        Assertions.assertEquals(result.get().getId(), memo.getId());
    }
}
