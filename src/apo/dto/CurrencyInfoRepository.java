package apo.dto;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CurrencyInfoRepository extends JpaRepository<CurrencyInfo, Integer>
{

	Optional<CurrencyInfo> findById(CurrencyId id);

	@Query("SELECT MIN(c.value) FROM CurrencyInfo c WHERE c.id.code = :code AND c.id.date BETWEEN :from AND :to")
	Double getMin(@Param("code") String code, @Param("from") LocalDate from, @Param("to") LocalDate to);

	@Query("SELECT MAX(c.value) FROM CurrencyInfo c WHERE c.id.code = :code AND c.id.date BETWEEN :from AND :to")
	Double getMax(@Param("code") String code, @Param("from") LocalDate from, @Param("to") LocalDate to);

	@Query("SELECT AVG(c.value) FROM CurrencyInfo c WHERE c.id.code = :code AND c.id.date BETWEEN :from AND :to")
	Double getAvg(@Param("code") String code, @Param("from") LocalDate from, @Param("to") LocalDate to);

}
