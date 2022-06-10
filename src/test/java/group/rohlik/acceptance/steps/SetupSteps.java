package group.rohlik.acceptance.steps;

import io.cucumber.java.Before;
import lombok.AllArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@AllArgsConstructor
public class SetupSteps {

    @PersistenceContext
    private EntityManager em;

    @Before
    @Transactional
    public void truncateDB() {
        List<Object> tables = em.createNativeQuery("show tables").getResultList();
        executeUpdate("SET FOREIGN_KEY_CHECKS = 0;");
        tables.forEach(o -> {
            String query = "truncate table %s".formatted(o.toString());
            executeUpdate(query);
        });
        executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");
    }

    private void executeUpdate(String sql) {
        em.createNativeQuery(sql).executeUpdate();
    }

    public static void notImplemented() {
        throw new RuntimeException("Step method not implemented");
    }
}
