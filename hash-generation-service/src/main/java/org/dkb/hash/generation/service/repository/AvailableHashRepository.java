package org.dkb.hash.generation.service.repository;

import jakarta.persistence.LockModeType;
import org.dkb.hash.generation.service.entity.AvailableHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailableHashRepository extends JpaRepository<AvailableHash, Long> {

	@Query("SELECT a.id FROM AvailableHash a WHERE a.used is TRUE ORDER BY a.id DESC LIMIT 1")
	Long findLastGeneratedId();

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM AvailableHash a WHERE a.used is FALSE ORDER BY a.id LIMIT 1")
	Optional<AvailableHash> findFirstUnusedHash();

	Optional<AvailableHash> findByHash(String hash);
}
