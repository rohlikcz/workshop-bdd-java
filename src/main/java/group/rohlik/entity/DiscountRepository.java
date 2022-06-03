package group.rohlik.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Optional<Discount> findByCode(String code);

    @Query("SELECT d FROM discounts d WHERE d.type in ('MIN_PRICE')")
    List<Discount> findAllAutomatics();
}
