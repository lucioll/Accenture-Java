package com.codeoftheweb.salvo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game,Long> {
    Game findByCreationDate(LocalDateTime creationDate);
}
