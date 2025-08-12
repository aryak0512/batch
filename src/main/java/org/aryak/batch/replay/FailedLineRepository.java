package org.aryak.batch.replay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedLineRepository extends JpaRepository<FailedLine, Long> {

    @Query("SELECT fl FROM FailedLine fl WHERE fl.fileName = :fileName AND fl.processed = false ORDER BY fl.lineNumber")
    List<FailedLine> findUnprocessedFailedLinesByFileName(@Param("fileName") String fileName);

    @Query("SELECT DISTINCT fl.lineNumber FROM FailedLine fl WHERE fl.fileName = :fileName AND fl.processed = false ORDER BY fl.lineNumber")
    List<Integer> findUnprocessedLineNumbers(@Param("fileName") String fileName);

    @Modifying
    @Query("UPDATE FailedLine fl SET fl.processed = true WHERE fl.fileName = :fileName AND fl.lineNumber = :lineNumber")
    void markAsProcessed(@Param("fileName") String fileName, @Param("lineNumber") Integer lineNumber);
}
