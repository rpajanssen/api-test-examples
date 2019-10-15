package com.example.examples.repositories;

import com.example.examples.domain.db.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Note that we extend JpaRepository and not CrudRepository. This is because we want our methods to return List instead
 * of Iterable type instances.
 */
@Repository
@Transactional
public interface PersonEntityRepository extends JpaRepository<PersonEntity, Long> {

    /**
     * This method was required to be able to write a unit test (see PersonEntityRepositorySlicedTest)
     * using the DataJpaTest annotation. :(
     */
    List<PersonEntity> findByLastName(String lastName);

    void deleteById(Long id);
}
